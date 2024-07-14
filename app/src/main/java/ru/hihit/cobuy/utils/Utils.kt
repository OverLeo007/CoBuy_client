package ru.hihit.cobuy.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


fun Context.copyToClipboard(text: CharSequence){
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label",text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun parseJson(json: String): Map<String, Any> {
    val gson = Gson()
    val type: Type = object : TypeToken<Map<String, Any>>() {}.type
    return gson.fromJson(json, type)
}

fun saveToPreferences(context: Context, key: String, value: Any) {
    val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)

    with(sharedPreferences.edit()) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Set<*> -> {
                @Suppress("UNCHECKED_CAST")
                putStringSet(key, value as Set<String>)
            }
            else -> throw IllegalArgumentException("Unsupported type of value")
        }
        apply()
    }
}

fun saveManyToPreferences(context: Context, map: Map<String, Any>) {
    for ((key, value) in map) {
        saveToPreferences(context, key, value)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> getFromPreferences(context: Context, key: String, defaultValue: T): T {
    val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    return when (defaultValue) {
        is String -> sharedPreferences.getString(key, defaultValue) as T
        is Int -> sharedPreferences.getInt(key, defaultValue) as T
        is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
        is Float -> sharedPreferences.getFloat(key, defaultValue) as T
        is Long -> sharedPreferences.getLong(key, defaultValue) as T
        is Set<*> -> sharedPreferences.getStringSet(key, defaultValue as Set<String>) as T
        else -> throw IllegalArgumentException("Unsupported type of value")
    }
}

fun clearPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()
    }
}


fun removeFromPreferences(context: Context, key: String) {
    val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove(key)
        apply()
    }
}

fun removeManyPreferences(context: Context, vararg keys: String) {
    for (key in keys) {
        removeFromPreferences(context, key)
    }
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