package top.dreamlike.ksp.helper

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import top.dreamlike.ksp.Arg
import top.dreamlike.ksp.model.FunctionParameterData

private val bufferExtractor = Arg("buffer", "")

private val stringExtractor = Arg("str", "val str = buffer.toString()")

private val jsonExtractor = Arg("jsonObject", "val jsonObject = buffer.toJsonObject()")

private val objectExtractor = { qualifiedName :String -> Arg("bodyRes", "val bodyRes =  ${Json::class.qualifiedName}.decodeValue(buffer, $qualifiedName::class.java)") }


fun generateBodyArg(functionParameterData: FunctionParameterData) = when(functionParameterData.typeQualifiedName) {
    Buffer::class.qualifiedName!! -> bufferExtractor
    String::class.qualifiedName!!, String::class.java.name -> stringExtractor
    JsonObject::class.qualifiedName -> jsonExtractor
    else -> objectExtractor(functionParameterData.typeQualifiedName)
}