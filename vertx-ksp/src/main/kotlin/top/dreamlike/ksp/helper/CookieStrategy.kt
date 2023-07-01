package top.dreamlike.ksp.helper

import io.vertx.core.http.Cookie
import top.dreamlike.ksp.Arg
import top.dreamlike.ksp.VertxJaxRsSymbolProcessor
import top.dreamlike.ksp.model.FunctionParameterData
import java.math.BigDecimal

fun generateCookieArg(functionParameterData: FunctionParameterData, index: Int): Arg {
    val referenceName = "cookieValue${index}"
    return when (functionParameterData.typeQualifiedName) {
        Int::class.qualifiedName, Int::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0].value.toInt()"""
        )

        Long::class.qualifiedName, Long::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0].value.toLong()"""
        )

        Double::class.qualifiedName, Double::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0].value.toDouble()"""
        )

        Float::class.qualifiedName, Float::class.java.name -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0].value.toFloat()"""
        )

        BigDecimal::class.qualifiedName -> Arg(
            referenceName,
            """val $referenceName =  ${BigDecimal::class.qualifiedName}(rc.request().cookies("${functionParameterData.key}").toList()[0].value)"""
        )

        String::class.java.name, String::class.qualifiedName -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0].value"""
        )

        Cookie::class.qualifiedName -> Arg(
            referenceName,
            """val $referenceName = rc.request().cookies("${functionParameterData.key}").toList()[0]"""
        )

        Set::class.qualifiedName, Set::class.java.name -> if (functionParameterData.genericTypes.isNotEmpty() && functionParameterData.genericTypes[0] == Cookie::class.qualifiedName) {
            Arg(referenceName, """val $referenceName = rc.request().cookies()""")
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

        List::class.qualifiedName, List::class.java.name -> if (functionParameterData.genericTypes.isNotEmpty() && functionParameterData.genericTypes[0] == Cookie::class.qualifiedName) {
            Arg(
                referenceName,
                """val $referenceName = rc.request().cookies().toList()"""
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