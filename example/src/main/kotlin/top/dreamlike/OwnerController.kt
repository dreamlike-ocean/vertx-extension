package top.dreamlike

import io.netty.channel.Channel
import io.vertx.core.Vertx
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import top.dreamlike.helper.ManualResponse

@Path("/path")
class OwnerController {
    @Path("/test")
    @GET
    fun test(@CookieParam("") cookies:Set<String>,c :String) {

    }
}