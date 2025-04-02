package ru.hihit.cobuy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
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


fun prepareBitmapToRequest(bitmap: Bitmap, fileName: String, maxSizeInMB: Int = 5): MultipartBody.Part {
    val maxSizeInBytes = maxSizeInMB * 1024 * 1024
    var quality = 100
    var compressedData: ByteArray

    do {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        compressedData = bos.toByteArray()
        quality -= 5
    } while (compressedData.size > maxSizeInBytes && quality > 0)
    Log.d("BitmapPreparer", "Quality: $quality, size: ${compressedData.size}")
    val reqFile = compressedData.toRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", fileName, reqFile)
}

fun Context.getMultipartImageFromUri(uri: Uri): MultipartBody.Part? {
    val bitmap = getBitmapFromUri(uri)
    bitmap?.let {
        return prepareBitmapToRequest(bitmap, "image.jpg")
    }
    return null
}

fun Context.getBitmapFromUri(uri: Uri): Bitmap? {
    return try {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
    } catch (e: Exception) {
        null
    }
}


fun String.extractAllByRegex(regex: String): List<String> {
    return regex.toRegex().findAll(this).map { it.groups[1]?.value.orEmpty() }.toList()
}
