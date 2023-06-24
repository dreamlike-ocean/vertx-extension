package top.dreamlike
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod.*
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.await
import top.dreamlike.db.DBScope.Companion.openDBScope
import top.dreamlike.web.RouterCoroutineScope.Companion.co_route
class A(val a:Int)

fun main() {
//    val vertx = Vertx.vertx()
//    val kotlinModule = KotlinModule.Builder()
//            .withReflectionCacheSize(512)
//            .configure(KotlinFeature.NullToEmptyCollection, false)
//            .configure(KotlinFeature.NullToEmptyMap, false)
//            .configure(KotlinFeature.NullIsSameAsDefault, false)
//            .configure(KotlinFeature.SingletonSupport, false)
//            .configure(KotlinFeature.StrictNullChecks, false)
//            .build()
//    DatabindCodec.mapper().registerModule(kotlinModule)
//    DatabindCodec.prettyMapper().registerModule(kotlinModule)
//    vertx.deployVerticle(TestVerticle())

    println(String::class.qualifiedName!!)
    println( String::class.java.name)
}


class TestVerticle : AbstractVerticle() {

    override fun start() {

        val router = Router.router(vertx).co_route {

            GET bindPath "/all" produce "application/json" bindToHandle {
                 val res = GLOBAL_FACTORY.openDBScope {
                    AsyncMapper<Owner.OwnerMapper, _> {
                        this.findAll()
                    }
                }
                res
            }

            POST bindPath "/form" bindToHandle {
                it.request().setExpectMultipart(true)

            }

        }


        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
        println("listen  end")
    }
}

