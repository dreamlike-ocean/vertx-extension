package top.dreamlike.model

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import io.vertx.core.http.HttpMethod
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.OPTIONS
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import top.dreamlike.VertxSymbolProcessor
import top.dreamlike.helper.ManualResponse
import top.dreamlike.model.FunctionParameterData.Companion.parseFunctionParameter

data class FunctionData(
    val path: String?,
    val referenceName: String,
    val qualifiedName: String,
    val modifier: Set<Modifier>,
    val ownerClass: ClassData,
    val httpMethod: HttpMethod?,
    val consumer: Array<String>?,
    val produces: Array<String>?,
    val returnType: String?,
    val list: List<FunctionParameterData>,
    val functionDeclaration: KSFunctionDeclaration,
) {
    companion object {
        val Method_Annotation = mapOf(
            GET::class.java.name to HttpMethod.GET,
            POST::class.java.name to HttpMethod.POST,
            DELETE::class.java.name to HttpMethod.DELETE,
            PUT::class.java.name to HttpMethod.PUT,
            PATCH::class.java.name to HttpMethod.PATCH,
            OPTIONS::class.java.name to HttpMethod.OPTIONS
        )

        fun KSFunctionDeclaration.parse(ownerClass: ClassData): FunctionData {
            val modifier = modifiers
            var path: String? = null
            var httpMethod: HttpMethod? = null
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

    inline fun autoHandleResponse() = returnType != null

    fun allowGenerate() {
        if (httpMethod == null && path == null) {
            VertxSymbolProcessor.logger.error("$qualifiedName need http method or path!")
            return
        }
        val bodyRequire =list.filter { it.parameterType == ParameterType.BODY }.size
        if (bodyRequire > 1) {
            VertxSymbolProcessor.logger.error("$qualifiedName has more than one `body` param!")
            return
        }

    }
}