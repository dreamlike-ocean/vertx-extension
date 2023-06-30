package top.dreamlike

import io.vertx.ext.web.Router
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class Binder {
    companion object {
        inline infix fun <reified T> T.bindTo(router: Router) {
            val simpleName = T::class.simpleName
            val qualifiedName = T::class.qualifiedName
            if (simpleName == null || qualifiedName == null) {
                throw IllegalArgumentException("dont support anonymous object")
            }
            val proxyClassName = VertxJaxRsSymbolProcessor.generateProxyClassName(simpleName)
            val proxyClassNameQualifiedName = qualifiedName.replace(simpleName, proxyClassName)
            Class.forName(proxyClassNameQualifiedName)
                .kotlin
                .constructors
                .first()
                .call(router, this)
        }
    }
}