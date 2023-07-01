package top.dreamlike

import io.vertx.core.Vertx
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.*
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
    suspend fun test5(@CookieParam("cookie1") cookies: Set<Cookie>, owner: Owner, @Context rc: RoutingContext) {

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