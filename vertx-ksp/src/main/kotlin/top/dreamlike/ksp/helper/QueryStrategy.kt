package top.dreamlike.ksp.helper

import top.dreamlike.ksp.Arg
import top.dreamlike.ksp.VertxJaxRsSymbolProcessor
import top.dreamlike.ksp.model.FunctionParameterData
import java.math.BigDecimal

fun generateQueryArg(functionParameterData: FunctionParameterData, index: Int): Arg {
    val referenceName = "queryValue${index}"
    return when (functionParameterData.typeQualifiedName) {
        Int::class.qualifiedName, Int::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.queryParam("${functionParameterData.key}")[0].toInt()"""
        )

        Long::class.qualifiedName, Long::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.queryParam("${functionParameterData.key}")[0].toLong()"""
        )

        Double::class.qualifiedName, Double::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.queryParam("${functionParameterData.key}")[0].toDouble()"""
        )

        Float::class.qualifiedName, Float::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.queryParam("${functionParameterData.key}")[0].toFloat()"""
        )

        BigDecimal::class.qualifiedName -> Arg(
            referenceName,
            """val $referenceName =  ${BigDecimal::class.qualifiedName}(rc.queryParam("${functionParameterData.key}")[0])"""
        )

        String::class.java.name, String::class.qualifiedName -> Arg(
            referenceName,
            """val $referenceName = rc.queryParam("${functionParameterData.key}")[0]"""
        )

        Set::class.qualifiedName, Set::class.java.name -> if (functionParameterData.genericTypes.isNotEmpty() && functionParameterData.genericTypes[0] == String::class.qualifiedName) {
            Arg(referenceName, """val $referenceName = rc.queryParam().toSet()""")
        } else {
            VertxJaxRsSymbolProcessor.logger.error(
                "${functionParameterData.typeQualifiedName} <${
                    functionParameterData.genericTypes.joinToString(
                        ","
                    )
                }}> dont be supported!"
            )
//           SHOULD NOT REACH HERE!!!!!!!!
            Arg("", "")
        }
//todo 非String集合的场景补充
        List::class.qualifiedName, List::class.java.name -> if (functionParameterData.genericTypes.isNotEmpty() && functionParameterData.genericTypes[0] == String::class.qualifiedName) {
            Arg(
                referenceName,
                """val $referenceName = rc.queryParam()"""
            )
        } else {
            VertxJaxRsSymbolProcessor.logger.error(
                "${functionParameterData.typeQualifiedName} <${
                    functionParameterData.genericTypes.joinToString(
                        ","
                    )
                }}> dont be supported!"
            )
//           SHOULD NOT REACH HERE!!!!!!!!
            Arg("", "")
        }


        else -> {
            functionParameterData.ksValueParameter.type.resolve().declaration.typeParameters
            VertxJaxRsSymbolProcessor.logger.error("${functionParameterData.typeQualifiedName} dont be supported!")
//           SHOULD NOT REACH HERE!!!!!!!!
            Arg("", "")
        }
    }
}