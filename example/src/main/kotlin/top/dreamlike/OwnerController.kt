package top.dreamlike

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerRequest
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import top.dreamlike.helper.ManualResponse

@Path("/path")
class OwnerController {
    @Path("/test")
    @GET
    fun test(s: String, @Context vertx: Vertx, @HeaderParam("header_verlue") headerP: String,@Context request: HttpServerRequest) {

    }

    @Path("/test")
    @GET
    @ManualResponse
    fun test1(@Context vertx: Vertx) = "123123"


    @Path("/test")
    @GET
    fun test2(@Context vertx: Vertx) = "123123"
}