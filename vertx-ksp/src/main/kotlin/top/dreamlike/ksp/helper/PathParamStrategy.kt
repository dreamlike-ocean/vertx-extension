package top.dreamlike.ksp.helper

import top.dreamlike.ksp.Arg
import top.dreamlike.ksp.VertxJaxRsSymbolProcessor
import top.dreamlike.ksp.model.FunctionParameterData
import java.math.BigDecimal

fun generatePathParamArg(functionParameterData: FunctionParameterData, index: Int): Arg {
    //todo 编译期来校验path合法性
    val referenceName = "pathValue${index}"
    return when(functionParameterData.typeQualifiedName) {
        Int::class.qualifiedName, Int::class.java.name -> Arg(referenceName, """val $referenceName = rc.pathParam("${functionParameterData.key}").toInt()""")
        Long::class.qualifiedName, Long::class.java.name -> Arg(referenceName, """val $referenceName = rc.pathParam("${functionParameterData.key}").toLong()""")
        Double::class.qualifiedName, Double::class.java.name -> Arg(referenceName, """val $referenceName = rc.pathParam("${functionParameterData.key}").toDouble()""")
        Float::class.qualifiedName, Float::class.java.name -> Arg(referenceName, """val $referenceName = rc.pathParam("${functionParameterData.key}").toFloat()""")
        BigDecimal::class.qualifiedName -> Arg(referenceName, """val $referenceName =  ${BigDecimal::class.qualifiedName}(rc.pathParam("${functionParameterData.key}"))""")
        String::class.java.name, String::class.qualifiedName -> Arg(referenceName, """val $referenceName = rc.pathParam("${functionParameterData.key}")""")
        else -> {
            VertxJaxRsSymbolProcessor.logger.error("${functionParameterData.typeQualifiedName} dont be supported!")
//           SHOULD NOT REACH HERE!!!!!!!!
            Arg("", "")
        }
    }
}
