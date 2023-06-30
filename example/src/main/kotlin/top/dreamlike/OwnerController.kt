package top.dreamlike

import io.netty.channel.Channel
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.CookieParam
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Context
import top.dreamlike.helper.ManualResponse

@Path("/path")
class OwnerController {
    @Path("/test")
    @GET
    fun test(@CookieParam("cookie1") cookies: String, c: String) {

    }

    @Path("/test1")
    @GET
    suspend fun test1(@Context vertx: Vertx, c: String) {

    }

    @Path("/test1")
    @GET
    fun test2(@Context vertx: Vertx, @FormParam("param") id: Long) {

    }

    @Path("/test1/:param")
    @GET
    fun test3(@Context vertx: Vertx, @PathParam("param") id: Long) {

    }

    @Path("/test1")
    @GET
    fun test4(@Context vertx: Vertx, body: Buffer) {

    }

    @Path("/test")
    @GET
    fun test5(@CookieParam("cookie1") cookies: Set<Cookie>, c: String) {

    }

    @Path("/test")
    @POST
    fun test6(
        @CookieParam("cookie1") cookies: Set<Cookie>, c: String,
        @Context vertx: Vertx, @Context serverRequest: HttpServerRequest
    ) {

    }
}