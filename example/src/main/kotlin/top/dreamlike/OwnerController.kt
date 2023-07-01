package top.dreamlike

import io.vertx.core.Vertx
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import top.dreamlike.ksp.helper.ManualResponse

@Path("/path")
class OwnerController {
    @Path("/test")
    @GET
    fun test(@HeaderParam("header") header: String, c: String) : JsonObject {
        return JsonObject.of("header", header)
    }

    @Path("/test1")
    @GET
    @ManualResponse
    suspend fun test5(@QueryParam("key") key: String, @Context rc: RoutingContext) {
        rc.response().end(key)
    }

    @Path("/test2")
    @POST
    @Consumes("application/json", "applcation/xml")
    @Produces("application/json", "applcation/xml")
    fun test6(
        c: String,
        @Context vertx: Vertx, @Context serverRequest: HttpServerRequest
    ):String {
        return "EventLoop size : ${vertx.nettyEventLoopGroup().count()}, ${serverRequest.remoteAddress()}, $c"
    }
}