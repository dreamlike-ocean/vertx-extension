package top.dreamlike

import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import java.math.BigDecimal
import kotlin.coroutines.resume


class Template {
    companion object {
        fun multipart(afterBody :String) = """
         route.handler { rc-> 
            rc.request().setExpectMultipart(true)
            rc.request().endHandler {
                $afterBody
            }
        }
        """.trimIndent()

        fun body(afterBody :String) = """
         route.handler { rc->
            rc.request().body().onSuccess { buffer -> 
               $afterBody
           }
        }
        """.trimIndent()

    }

    fun s(route: Route) {
        route.handler { rc->
            rc.request().setExpectMultipart(true)
            rc.request().endHandler {
                var attribute = rc.request().getFormAttribute("")
            }
        }
    }
}
