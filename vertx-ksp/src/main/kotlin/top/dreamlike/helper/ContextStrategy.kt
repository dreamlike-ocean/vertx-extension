package top.dreamlike.helper;

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import top.dreamlike.Arg
import top.dreamlike.model.FunctionParameterData


private val vertxExtractor = Arg("vertx", "val vertx = rc.vertx()")

private val contextExtractor = Arg("context", "val context = rc.vertx().orCreateContext")

private val requestExtractor =  Arg("request", "val request = rc.request()")

private val responseExtractor = Arg("response", "val response = rc.response()")

private val routerContextExtractor = Arg("rc", "")

val allContextObject =
    mapOf(
        Vertx::class.qualifiedName!! to vertxExtractor, Context::class.qualifiedName!! to contextExtractor,
        HttpServerRequest::class.qualifiedName!! to requestExtractor, HttpServerResponse::class.qualifiedName!! to responseExtractor,
        RoutingContext::class.qualifiedName!! to routerContextExtractor
    )


fun generateContextArg(parameterData: FunctionParameterData) = allContextObject[parameterData.typeQualifiedName]!!