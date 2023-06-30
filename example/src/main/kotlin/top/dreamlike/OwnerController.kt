package top.dreamlike

import io.netty.channel.Channel
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import top.dreamlike.helper.ManualResponse

@Path("/path")
class OwnerController {
    @Path("/test")
    @GET
    fun test(@CookieParam("cookie1") cookies: String, c: String) : JsonObject {
        return JsonObject()
    }

    @Path("/test")
    @GET
    @ManualResponse
    suspend fun test5(@CookieParam("cookie1") cookies: Set<Cookie>, c: String) {

    }

    @Path("/test")
    @POST
    @Consumes("application/json", "applcation/xml")
    @Produces("application/json", "applcation/xml")
    fun test6(
        @CookieParam("cookie1") cookies: Set<Cookie>, c: String,
        @Context vertx: Vertx, @Context serverRequest: HttpServerRequest
    ) {

    }
}