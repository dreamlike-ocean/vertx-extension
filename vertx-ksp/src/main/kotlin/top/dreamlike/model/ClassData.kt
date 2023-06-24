package top.dreamlike.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import jakarta.ws.rs.Path

data class ClassData(val rootPath:String, val referenceName:String, val qualifiedName:String) {
    companion object {
        fun KSClassDeclaration.parse() : ClassData {
            val pathAnnotation = this.annotations.find {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == Path::class.qualifiedName!!
            }!!
            val rootPath = pathAnnotation.arguments.find {
                it.name!!.asString() == "value"
            }!!.value as String

            val s = this.simpleName.asString()
            val referenceName = s.replaceRange(0..0, s[0].lowercase() )
            val qualifiedName = this.qualifiedName!!.asString()

            return ClassData(rootPath, referenceName, qualifiedName)
        }
    }
}
