package top.dreamlike

import io.vertx.ext.web.Router

class Binder {
    companion object {
       inline fun create(scope : Binder.() -> Unit) {
           Binder().scope()
       }
    }
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