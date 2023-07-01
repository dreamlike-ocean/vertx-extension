package top.dreamlike.ksp.model


import com.google.devtools.ksp.symbol.KSValueParameter
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.MatrixParam
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import top.dreamlike.ksp.helper.fetchValueString


data class FunctionParameterData(
    val typeQualifiedName: String,
    val key: String,
    val parameterType: ParameterType,
    val genericTypes: List<String>,
    val ksValueParameter: KSValueParameter
) {
    //   val paramTypeList = it.parameters.map { a -> a.type.resolve().declaration.qualifiedName!!.asString() }
    companion object {
        fun KSValueParameter.parseFunctionParameter(): FunctionParameterData {
            val declaration = this.type.resolve().declaration
            val genericTypes = this.type.element?.typeArguments?.filter { it.type != null } ?.map { it.type!!.resolve().declaration.qualifiedName!!.asString() }  ?: emptyList()
//            VertxSymbolProcessor.logger.warn("current parameter: ${this.name?.asString()}, type :${declaration.qualifiedName?.asString()}")
            val typeQualifiedName = declaration.qualifiedName!!.asString()
            var key = ""
            var parameterType: ParameterType = ParameterType.BODY
            for (annotation in this.annotations) {
                val currentType = annotation.annotationType.resolve()
                val currentAnnotationQualifiedName =
                    currentType.declaration.qualifiedName!!.asString()
//                VertxSymbolProcessor.logger.warn("currentAnnotationQualifiedName: $currentAnnotationQualifiedName")
                when {
                    QueryParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.QUERY
                    }

                    PathParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.PATH_PARAM
                    }

                    HeaderParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.HEADER
                    }

                    CookieParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.COOKIE
                    }

                    MatrixParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.MATRIX
                    }

                    FormParam::class.qualifiedName == currentAnnotationQualifiedName -> {
                        key = annotation.fetchValueString()
                        parameterType = ParameterType.FORM
                    }

                    Context::class.qualifiedName == currentAnnotationQualifiedName -> {
                        parameterType = ParameterType.CONTEXT
                    }
                }

            }
            return FunctionParameterData(typeQualifiedName, key, parameterType, genericTypes, this)
        }

    }

}
