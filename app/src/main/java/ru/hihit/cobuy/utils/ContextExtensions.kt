package ru.hihit.cobuy.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.UserData
import java.io.File
import java.io.FileOutputStream


fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}



fun Context.saveToPreferences(key: String, value: Any) {
    val sharedPreferences = this.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)

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


fun Context.saveManyToPreferences(map: Map<String, Any>) {
    for ((key, value) in map) {
        saveToPreferences(key, value)
    }
}


fun Context.getUserDataFromPreferences(): UserData {
    val id = getFromPreferences("user_id", 0)
    val email = getFromPreferences("user_email", "")
    val name = getFromPreferences("user_name", "")
    return UserData(id, email, name)
}


fun Context.saveUserDataToPreferences(userData: UserData) {
    saveToPreferences("user_id", userData.id)
    saveToPreferences("user_email", userData.email)
    saveToPreferences("user_name", userData.name)
}


@Suppress("UNCHECKED_CAST")
fun <T> Context.getFromPreferences(key: String, defaultValue: T): T {
    val sharedPreferences = this.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
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


fun Context.clearPreferences() {
    val sharedPreferences = this.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()
    }
}


fun Context.removeFromPreferences(key: String) {
    val sharedPreferences = this.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove(key)
        apply()
    }
}

fun Context.removeManyPreferences(vararg keys: String) {
    for (key in keys) {
        removeFromPreferences(key)
    }
}


fun Context.makeShareQrIntent(qrBitmap: ImageBitmap, groupName: String): Intent {
    val file = File(this.cacheDir, "qr_code.png")
    val fOut = FileOutputStream(file)

    val androidBitmap = qrBitmap.asAndroidBitmap()

    val whiteBmp =
        Bitmap.createBitmap(androidBitmap.width, androidBitmap.height, androidBitmap.config)
    val canvas = Canvas(whiteBmp)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(androidBitmap, 0f, 0f, null)

    whiteBmp.compress(Bitmap.CompressFormat.PNG, 85, fOut)
    fOut.flush()
    fOut.close()

    val uri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_TEXT,
            this@makeShareQrIntent.getString(R.string.share_qr_text, groupName)
        )
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    val clipData = ClipData.newUri(this.contentResolver, "QR Code", uri)
    shareIntent.clipData = clipData
    return shareIntent
}