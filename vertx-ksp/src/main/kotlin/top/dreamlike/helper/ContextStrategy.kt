package top.dreamlike.helper;

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import top.dreamlike.Arg


private val vertxExtractor = Arg("vertx", "val vertx = it.vertx()")

private val contextExtractor = Arg("context", "val context = it.vertx().orCreateContext")

private val requestExtractor =  Arg("request", "val request = it.request()")

private val responseExtractor = Arg("response", "val response = it.response()")

private val routerContextExtractor = Arg("rc", "val rc = it")

private val allContextObject =
    mapOf(
        Vertx::class.qualifiedName!! to vertxExtractor, Context::class.qualifiedName!! to contextExtractor,
        HttpServerRequest::class.qualifiedName!! to requestExtractor, HttpServerResponse::class.qualifiedName!! to responseExtractor
    )


fun generateContext(qualifierName: String) = allContextObject[qualifierName]