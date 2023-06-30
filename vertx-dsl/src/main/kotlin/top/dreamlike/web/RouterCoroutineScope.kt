package top.dreamlike.web

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.streams.ReadStream
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.http.HttpResponse.BodyHandlers
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RouterCoroutineScope(val router: Router) {

    companion object {

        fun Router.co_route(functions: RouterCoroutineScope.() -> Unit): Router {
            functions(RouterCoroutineScope(this))
            return this
        }

    }



    infix fun HttpMethod.bindPath(path: String?) = when {
        this == HttpMethod.GET -> path?.let { router.get(it) } ?: router.get()
        this == HttpMethod.PUT -> path?.let { router.put(it) } ?: router.put()
        this == HttpMethod.DELETE -> path?.let { router.delete(it) } ?: router.delete()
        this == HttpMethod.POST -> path?.let { router.post(it) } ?: router.post()
        else -> throw UnsupportedOperationException()
    }


    infix fun Route.consume(contentType: String) = this.consumes(contentType)

    infix fun Route.produce(contentType: String) = this.produces(contentType)

    infix fun <T> Route.bindToHandle(handle: suspend (RoutingContext) -> T) = co_handle_result(handle)

    infix fun Route.bindToFlowHandle(handle: suspend (RoutingContext) -> ReadStream<Buffer>) = co_handle_flow(handle)

    infix fun Route.bindTo(handle: suspend (RoutingContext) -> Unit) = co_handle_void(handle)

    suspend fun RoutingContext.awaitBodyEnd() = suspendCoroutine { c ->
        this.request().endHandler {
            c.resume(Unit)
        }
    }

    private fun Route.co_handle_flow(handle: suspend (RoutingContext) -> ReadStream<Buffer>) {
        handler {
            CoroutineScope(it.vertx().dispatcher() as CoroutineContext)
                    .launch {
                        try {
                            val stream = handle(it)
                            stream.pipeTo(it.response()).await()
                            it.response().end()
                        } catch (e: Exception) {
                            it.fail(e)
                        }
                    }
        }
    }


    private fun <T> Route.co_handle_result(handle: suspend (RoutingContext) -> T) {
        handler {
            CoroutineScope(it.vertx().dispatcher() as CoroutineContext)
                    .launch {
                        try {
                            val res = handle(it)
                            it.response().end(Json.encodeToBuffer(res))
                        } catch (e: Exception) {
                            it.fail(e)
                        }
                    }
        }
    }


    private fun Route.co_handle_void(handle: suspend (RoutingContext) -> Unit) {
        handler {
            CoroutineScope(it.vertx().dispatcher() as CoroutineContext)
                    .launch {
                        try {
                            handle(it)
                        } catch (e: Exception) {
                            it.fail(e)
                        }
                    }
        }
    }


}