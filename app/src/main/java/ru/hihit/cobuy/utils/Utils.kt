package ru.hihit.cobuy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.hihit.cobuy.BuildConfig
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



@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uri::class)
object UriSerializer : KSerializer<Uri> {
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}



fun prepareBitmapToRequest(bitmap: Bitmap, fileName: String): MultipartBody.Part {
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val data = bos.toByteArray()

    val reqFile = data.toRequestBody("image/jpeg".toMediaTypeOrNull())
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