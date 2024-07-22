package ru.hihit.cobuy.utils

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type


fun parseJson(json: String): Map<String, Any> {
    val gson = Gson()
    val type: Type = object : TypeToken<Map<String, Any>>() {}.type
    return gson.fromJson(json, type)
}

fun isJwt(token: String): Boolean {
    val parts = token.split(".")
    if (parts.size != 3) {
        return false
    }

    val base64UrlRegex = "^[A-Za-z0-9_-]*$".toRegex()
    for (part in parts) {
        if (!base64UrlRegex.matches(part)) {
            return false
        }
    }

    return true
}


fun String?.toUri(): Uri? {
    return this?.let { Uri.parse(it) }
}


data class MutablePair<out A, out B>(
    var first: @UnsafeVariance A,
    var second: @UnsafeVariance B
) : Serializable {

    override fun toString(): String = "($first, $second)"
}
