package top.dreamlike.ksp.helper


import top.dreamlike.ksp.Arg
import top.dreamlike.ksp.VertxJaxRsSymbolProcessor
import top.dreamlike.ksp.model.FunctionParameterData
import java.math.BigDecimal


fun generateHeaderArg(functionParameterData: FunctionParameterData, index: Int): Arg {
    val referenceName = "headerValue${index}"
    return when(functionParameterData.typeQualifiedName) {
        Int::class.qualifiedName, Int::class.java.name -> Arg(referenceName, """val $referenceName = rc.request().getHeader("${functionParameterData.key}").toInt()""")
        Long::class.qualifiedName, Long::class.java.name -> Arg(referenceName, """val $referenceName = rc.request().getHeader("${functionParameterData.key}").toLong()""")
        Double::class.qualifiedName, Double::class.java.name -> Arg(referenceName, """val $referenceName = rc.request().getHeader("${functionParameterData.key}").toDouble()""")
        Float::class.qualifiedName, Float::class.java.name -> Arg(referenceName, """val $referenceName = rc.request().getHeader("${functionParameterData.key}").toFloat()""")
        BigDecimal::class.qualifiedName -> Arg(referenceName, """val $referenceName =  ${BigDecimal::class.qualifiedName}(rc.request().getHeader("${functionParameterData.key}"))""")
        String::class.java.name, String::class.qualifiedName -> Arg(referenceName, """val $referenceName = rc.request().getHeader("${functionParameterData.key}")""")
        else -> {
            VertxJaxRsSymbolProcessor.logger.error("${functionParameterData.typeQualifiedName} dont be supported!")
//           SHOULD NOT REACH HERE!!!!!!!!
            Arg("", "")
        }
    }
}
