package top.dreamlike.model

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.OPTIONS
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import top.dreamlike.Template
import top.dreamlike.VertxJaxRsSymbolProcessor
import top.dreamlike.helper.*
import top.dreamlike.model.FunctionParameterData.Companion.parseFunctionParameter

data class FunctionData(
    val path: String?,
    val referenceName: String,
    val qualifiedName: String,
    val modifier: Set<Modifier>,
    val ownerClass: ClassData,
    val httpMethod: String?,
    val consumer: Array<String>?,
    val produces: Array<String>?,
    val returnType: String?,
    val params: List<FunctionParameterData>,
    val functionDeclaration: KSFunctionDeclaration,
) {
    companion object {
        val Method_Annotation = mapOf(
            GET::class.java.name to "io.vertx.core.http.HttpMethod.GET",
            POST::class.java.name to "io.vertx.core.http.HttpMethod.POST",
            DELETE::class.java.name to "io.vertx.core.http.HttpMethod.DELETE",
            PUT::class.java.name to "io.vertx.core.http.HttpMethod.PUT",
            PATCH::class.java.name to "io.vertx.core.http.HttpMethod.PATCH",
            OPTIONS::class.java.name to "io.vertx.core.http.HttpMethod.OPTIONS"
        )

        fun KSFunctionDeclaration.parse(ownerClass: ClassData): FunctionData {
            val modifier = modifiers
            var path: String? = null
            var httpMethod: String? = null
            var consumer: Array<String>? = null
            var produces: Array<String>? = null
            val referenceName = this.simpleName.asString()
            val qualifiedName = this.qualifiedName!!.asString()
            var resType :String? = this.returnType!!.resolve().declaration.qualifiedName!!.asString()
            for (annotation in this.annotations) {
                val currentAnnotationQualifiedName =
                    annotation.annotationType.resolve().declaration.qualifiedName!!.asString()
                when {
                    Method_Annotation.contains(currentAnnotationQualifiedName) -> httpMethod =
                        Method_Annotation[currentAnnotationQualifiedName]

                    Path::class.qualifiedName == currentAnnotationQualifiedName -> path =
                        annotation.arguments.find { it.name!!.asString() == "value" }!!.value as String

                    Consumes::class.qualifiedName == currentAnnotationQualifiedName -> consumer =
                        annotation.arguments.find { it.name!!.asString() == "value" }!!.value as Array<String>

                    Produces::class.qualifiedName == currentAnnotationQualifiedName -> produces =
                        annotation.arguments.find { it.name!!.asString() == "value" }!!.value as Array<String>

                    ManualResponse::class.qualifiedName == currentAnnotationQualifiedName -> resType = null
                }
            }

            val functionParameterData = this.parameters.map { it.parseFunctionParameter() }

            return FunctionData(
                path,
                referenceName,
                qualifiedName,
                modifier,
                ownerClass,
                httpMethod,
                consumer,
                produces,
                resType,
                functionParameterData,
                this
            )
        }
    }

    inline fun manualResponse() = returnType == null

    /**
     * 必须包含Method or Path
     * 不可以超过一个body参数 无标识则为body参数
     *
     */
    fun allowGenerate() :Boolean {
        if (httpMethod == null && path == null) {
            VertxJaxRsSymbolProcessor.logger.error("$qualifiedName need http method or path!")
            return false
        }

        if (params.any{it.parameterType == ParameterType.MATRIX}) {
            VertxJaxRsSymbolProcessor.logger.error("$qualifiedName has matrix param! current we dont support matrix param!")
            return false
        }

        val bodyRequire = params.filter { it.parameterType == ParameterType.BODY }
        if (bodyRequire.size > 1) {
            VertxJaxRsSymbolProcessor.logger.error("$qualifiedName has more than one `body` param!")
            return false
        }


        val bodyParam = bodyRequire.getOrNull(0)
        val formParams = params.filter { it.parameterType == ParameterType.FORM }

        if (formParams.isNotEmpty() && bodyParam != null) {
            VertxJaxRsSymbolProcessor.logger.error("$qualifiedName body param is illegal, this function has formParam and body!")
            return false
        }

       val hasIllegalContextParam = params
           .filter { it.parameterType == ParameterType.CONTEXT }
           .any { allContextObject[it.typeQualifiedName] == null }

        if (hasIllegalContextParam) {
            VertxJaxRsSymbolProcessor.logger.error("$qualifiedName has illegal ContextParam! current we only support ${allContextObject.keys}!")
            return false
        }

        return true
    }


    fun generateRouteHandle(index :Int): String {
        val path = ownerClass.rootPath.legalPath() + path.legalPath()
        val needParseBody = params.any { it.parameterType == ParameterType.BODY }
        val needForm = params.any { it.parameterType == ParameterType.FORM }


        val routeStatement = when {
            path.isBlank() && httpMethod == null -> "val route$index = router.route()"
            httpMethod == null -> """ val route$index = router.route("$path") """
            else -> """ val route$index = router.route($httpMethod,"$path") """
        }

        val functionCall = params.generateCurrenFunctionCall()
        val bindHandleStatement = when {
            needParseBody -> Template.bodyRoute(functionCall, "route$index")
            needForm -> Template.multipartRoute(functionCall, "route$index")
            else -> Template.normalRoute(functionCall, "route$index")
        }

        return "$routeStatement\n$bindHandleStatement"
    }

    //这里是前序全部处理完毕 比如该惰性的都惰性了
    // 来具体生成 从Route到T::function(...args)的代码
    fun List<FunctionParameterData>.generateCurrenFunctionCall() : String {
        var functionCallStatement = ownerClass.referenceName + "." + referenceName
        val parseParamArgs = this.mapIndexed { index, param ->
            when (param.parameterType) {
                ParameterType.BODY -> generateBodyArg(param)
                ParameterType.COOKIE -> generateCookieArg(param, index)
                ParameterType.HEADER -> generateHeaderArg(param, index)
                ParameterType.FORM -> generateFormArg(param, index)
                ParameterType.CONTEXT -> generateContextArg(param)
                ParameterType.QUERY -> generateQueryArg(param, index)
                ParameterType.PATH_PARAM -> generatePathParamArg(param, index)
                //todo 矩阵太复杂不实现 前序已经排除 这里抛出todo只是为了通过编译
                ParameterType.MATRIX -> TODO()
            }
        }
        val assignBlock = parseParamArgs.joinToString("\n") { it.assignStatement }
        functionCallStatement = "$functionCallStatement(${parseParamArgs.joinToString(",") { it.referenceName }})"
        functionCallStatement = if (modifier.contains(Modifier.SUSPEND)) {
            Template.suspendScope(functionCallStatement, parseParamArgs.VertxRef())
        } else {
            functionCallStatement
        }
        return "$assignBlock\n$functionCallStatement"
    }

}