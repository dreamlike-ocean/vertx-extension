package top.dreamlike.ksp

import top.dreamlike.ksp.VertxJaxRsSymbolProcessor.Companion.generateProxyClassName
import top.dreamlike.ksp.model.ClassData


class Template {
    companion object {
        fun multipartRoute(afterBody: String, routeRef: String = "route") = """
         $routeRef.handler { rc-> 
            rc.request().setExpectMultipart(true)
            rc.request().endHandler {
                $afterBody
            }
        }
        """.trimIndent()

        fun bodyRoute(afterBody: String, routeRef: String = "route") = """
         $routeRef.handler { rc->
            rc.request().body().onSuccess { buffer -> 
               $afterBody
           }
        }
        """.trimIndent()

        fun normalRoute(handle: String, routeRef: String = "route") = """
         $routeRef.handler { rc->
             $handle
         }
        """.trimIndent()

        fun suspendScope(handle: String, vertxRef: String?): String {
            return """
              CoroutineScope(${vertxRef ?: "rc.vertx()"}.dispatcher() as CoroutineContext)
                    .launch {
                       try {
                        $handle
                       }catch(t :Throwable) {
                         rc.fail(t)
                       }
                     }
        """.trimIndent()
        }

        fun proxyClass(mountCode: String, classData: ClassData) = """
            package ${classData.packagePath}
            $preImport
            class ${generateProxyClassName(classData.simpleName)}(val router:Router,val ${classData.referenceName} : ${classData.qualifiedName}) {
                 init {
                   $mountCode
                 }
            }
        """.trimIndent()

        private val preImport = """
            import io.vertx.ext.web.Router
            import io.vertx.kotlin.coroutines.dispatcher
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.launch
            import kotlin.coroutines.CoroutineContext
        """.trimIndent()
    }


}
