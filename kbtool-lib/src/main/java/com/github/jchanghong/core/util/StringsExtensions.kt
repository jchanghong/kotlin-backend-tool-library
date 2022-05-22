@file:Suppress("NOTHING_TO_INLINE")

package com.github.jchanghong.core.util

import com.google.common.base.Strings

inline infix operator fun String.times(n: Int): String = Strings.repeat(this, n)
inline infix operator fun Int.times(s: String): String = Strings.repeat(s, this)

inline fun Double.toFixed(digits: Int): String = java.lang.String.format("%.${digits}f", this)
inline fun Float.toFixed(digits: Int): String = toDouble().toFixed(digits)

inline fun String?.isNotNullOrBlank() = !isNullOrBlank()

inline fun <T> T.toStringTransform(transform: (T) -> String) = let(transform)

inline fun <T> T?.toStringTransform(nullString: String = null.toString(), transform: (T) -> String) =
    letOrElse(nullString, transform)

inline fun String.splitLines() = split('\n')

inline fun <T> T?.wrap() = "$this"

inline fun <T> T?.wrapString() = if (this is CharSequence) wrap() else toString()

inline fun concat(vararg params: Any?) = params.joinToString("")

inline fun join(vararg params: Any?) = params.joinToString(" ")

inline fun joinWith(separator: String = " ", vararg params: Any?) = params.joinToString(separator)

inline fun String.replace(ignoreCase: Boolean = false, vararg vars: Pair<String, String>): String {
    var copy = this
    vars.forEach { copy = copy.replace(it.first, it.second, ignoreCase) }
    return copy
}

inline fun String.remove(substring: String) = replace(substring, "")
