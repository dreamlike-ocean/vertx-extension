package top.dreamlike


class Template {
    companion object {
        fun multipartRoute(afterBody :String, routeRef:String = "route") = """
         $routeRef.handler { rc-> 
            rc.request().setExpectMultipart(true)
            rc.request().endHandler {
                $afterBody
            }
        }
        """.trimIndent()

        fun bodyRoute(afterBody :String, routeRef:String = "route") = """
         $routeRef.handler { rc->
            rc.request().body().onSuccess { buffer -> 
               $afterBody
           }
        }
        """.trimIndent()

        fun normalRoute(handle: String, routeRef:String = "route") = """
         $routeRef.handler { rc->
             $handle
         }
        """.trimIndent()

        fun suspendScope(handle: String, vertxRef :String?) :String {
            return """
              kotlinx.coroutines.CoroutineScope(${vertxRef?:"it.vertx()"}.dispatcher() as kotlin.coroutines.CoroutineContext)
                    .launch {
                       $handle
                     }
        """.trimIndent()
        }

        val preImport = """
            import io.vertx.kotlin.coroutines.dispatcher
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.launch
            import kotlin.coroutines.CoroutineContext
        """.trimIndent()
    }


}
