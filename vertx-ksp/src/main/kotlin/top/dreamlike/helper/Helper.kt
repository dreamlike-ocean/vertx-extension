package top.dreamlike.helper

import com.google.devtools.ksp.symbol.KSAnnotation

inline fun <reified T> KSAnnotation.fetchValue() = this.arguments.find { it.name!!.asString() == "value" }!!.value as T


inline fun KSAnnotation.fetchValueString() :String = this.fetchValue<String>()