# Vertx-Extension

[English Version](README-EN.md)

[中文版本](README.md)

This project contains two modules:

* Provide more semantic Vertx-web router binding based on kotlin dsl capability.

* Ksp implements compile-time generated jax-rs binding, similar to Quarkus`
  s [Reactive Router](https://quarkus.io/guides/reactive-routes)

## Vertx-dsl

An example code of DSL can be found in [co_router](example/src/main/kotlin/top/dreamlike/App.kt#L48)

### web-router

```kotlin 
val router = Router.router(vertx).co_route {

    GET bindPath "/all" produce "application/json" bindToHandle {
        val res = GLOBAL_FACTORY.openDBScope {
            AsyncMapper<Owner.OwnerMapper, _> {
                this.findAll()
            }
        }
        res
    }
}
```

The code above is equivalent to the following code.

```kotlin
router.get("all")
    .produce("application/json")
    .handler {
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
```

There are three forms of `bindHandle` in total.

* Route.bindToFlowHandle ==> suspend (RoutingContext) -> ReadStream<Buffer>
* Route.bindTo ==>  suspend (RoutingContext) -> Unit
* bindToHandle ==> suspend (RoutingContext) -> T

The principle behind it is very simple

1. Extend the function of Router `fun Router.co_route(functions: RouterCoroutineScope.() -> Unit): Router` .
2. The `RouterCoroutineScope` defines a set of Route extension functions based on the infix function and extension
   function features in Kotlin. For
   example, `infix fun Route.produce(contentType: String) = this.produces(contentType)`.
3. Finally, the implementation of `bindToHandle` is based on the above principle. It can be implemented directly like
   this.

```kotlin
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
```

### Mybatis-extension

This feature is designed specifically for the Vertx-web HTTP model. Compared with reactive web frameworks such as
Reactor-netty, Vertx-web has a very obvious feature - its directly support and no additional code is required to
construct Request-Local Scope.

When processing a new request, it will duplicate the context where the Router is located, so the Context obtained by
using `vertx.currentContext` is Request Local.

Therefore, the `SqlSession` can be considered to be stored in Context

So there is only one question left. Where is the mapper code executed? Of course, it is executed in the thread pool.But
additional thread pools have an impact on performance. Therefore, I use virtual threads to run the mapper’s JDBC code
and set its scheduler to the EventLoop corresponding to the current Context.

But there is still one problem here. What about pinning? Therefore, an adapted virtual thread JDBC implementation and an
adapted connection pool implementation must be used here. The instance code here uses MariaDB and hikariPool to connect
to MySQL.

Here, extension functions and reified features are still used to implement the requirements. In order to narrow the
scope of extension function matching, an empty marker interface must be inherited for the mapper class.

```kotlin
suspend inline fun <reified T : MybatisMarkInterface, V> AsyncMapper(
    once: Boolean = true,
    autoCommit: Boolean = true,
    crossinline handle: T.() -> V
): V {
    val sqlSession = currentSession() ?: factory.openSession(autoCommit)
    Vertx.currentContext().putLocal(SQL_SESSION_KEY, sqlSession)
    val future = StartOnCurrentContext(context) {
        try {
            sqlSession.getMapper(T::class.java).handle()
        } finally {
            if (once) {
                sqlSession.close()
                context.remove(SQL_SESSION_KEY)
            }
        }
    }

    return future.await()
}
```

## JAX-RS Binding

The implementation is very simple. It is based on KSP to scan annotations and generate Controller code.

Usage：

```kotlin
var router = Router.router(vertx)
var controller = OwnerController()
Binder.create {
    controller bindTo router
}
```

The generated code is as follows:
![](pic/kspRes.png)

The support for JAX-RX annotations is as follows:

* @Path: Only classes marked with @Path are supported to be scanned and generate code. Regex path is not supported for
  now.
* @GET @POST and other Restful annotations are fully supported.
* @Context currently only supports injection
  of `io.vertx.core.Vertx`,`io.vertx.core.Context`,`io.vertx.core.http.HttpServerRequest`,`io.vertx.core.http.HttpServerResponse`,`io.vertx.ext.web.RoutingContext`, `io.vertx.core.MultiMap` (
  this is used to inject headers).
* @Cookie supports Int, String, Long, Double, Float, BigDecimal, Set<io.vertx.core.http.Cookie>, List<
  io.vertx.core.http.Cookie>, and `io.vertx.core.http.Cookie` as parameters.
* @HeaderParam supports Int, String, Long, Double, Float, and BigDecimal as parameters.
* @QueryParam supports Int, String, Long, Double, Float, BigDecimal, Set<String>, List<String> as parameters.
* @FormParam supports Int, String, Long, Double, Float and BigDecimal as parameters.
* @MatrixParam is **not supported**.
* @Consumes,@Produces are supported.

Additional:

* By default, the return value of the Controller method will be used as the http response in json serialization.
* If the method marked with @ManualResponse needs the user to handle http response manually, such as requiring injection
  of `io.vertx.core.http.HttpServerResponse` and other Context objects to return.
* Supports Controller suspend function and will start a coroutine with the current context.