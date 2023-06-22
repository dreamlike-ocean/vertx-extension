package top.dreamlike

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router

enum class RequestType(val handle : (Router, String?) -> Route) {
    GET ({ router,path ->
        path?.let { router.get(it) } ?: router.get()
    }),

    POST ({ router,path ->
        path?.let { router.post(it) } ?: router.post()
    }),
    PUT ({ router,path ->
        path?.let { router.put(it) } ?: router.put()
    }),
    DELETE ({ router,path ->
        path?.let { router.delete(it) } ?: router.delete()
    })


}