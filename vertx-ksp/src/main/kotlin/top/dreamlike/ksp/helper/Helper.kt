package top.dreamlike.ksp.helper

import com.google.devtools.ksp.symbol.KSAnnotation

inline fun <reified T> KSAnnotation.fetchValue() = this.arguments.find { it.name!!.asString() == "value" }!!.value as T


fun KSAnnotation.fetchValueString(): String = this.fetchValue<String>()


fun String?.legalPath() = when {
    this == null -> ""
    this.trim()[0] != '/' -> "/${this.trim()}"
    else -> this
}

