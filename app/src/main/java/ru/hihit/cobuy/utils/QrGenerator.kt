package ru.hihit.cobuy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlin.math.roundToInt


@Throws(WriterException::class)
fun createQRCodeBitmap(
    context: Context,
    text: String,
    logoRes: Int,
): ImageBitmap {
    val displayMetrics = context.resources.displayMetrics
    val width = (displayMetrics.widthPixels  * 0.8f).roundToInt()
    val height = width
    val hints = HashMap<EncodeHintType, ErrorCorrectionLevel>()
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (bitMatrix[x, y]) {
                if ((x < width / 2 - width / 10 || x > width / 2 + width / 10)
                    || (y < height / 2 - height / 10 || y > height / 2 + height / 10)) {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }
    }

    val logoDrawable = ResourcesCompat.getDrawable(context.resources, logoRes, null)
    var logo = logoDrawable?.toBitmap()
    if (logo != null) {
        val logoWidth = logo.width
        val logoHeight = logo.height
        val scaleFactor =
            (width * 0.8f / 5f / logoWidth).coerceAtMost(height * 0.8f / 5f / logoHeight)

        val output = Bitmap.createBitmap(logo.width, logo.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = Rect(0, 0, logo.width, logo.height)
        val rectF = RectF(rect)
        canvas.drawRoundRect(rectF, logo.width.toFloat() / 2, logo.height.toFloat() / 2, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawRect(rectF, paint)

        logo = output

        canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.drawBitmap(logo, (width - logoWidth) / 2f, (height - logoHeight) / 2f, null)
    }

    return bitmap.asImageBitmap()
}



@Throws(WriterException::class)
fun createQRCodeBitmap(
    context: Context,
    text: String
): ImageBitmap {
    val displayMetrics = context.resources.displayMetrics
    val qrWidth = (displayMetrics.widthPixels  * 0.8f).roundToInt()
    val qrHeight = qrWidth
    val frameWidth = qrWidth / 10 // размер рамки - 10% от размера QR-кода
    val width = qrWidth + 2 * frameWidth
    val height = qrHeight + 2 * frameWidth

    val hints = HashMap<EncodeHintType, ErrorCorrectionLevel>()
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints)

    val qrBitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888)
    for (x in 0 until qrWidth) {
        for (y in 0 until qrHeight) {
            if (bitMatrix[x, y]) {
                qrBitmap.setPixel(x, y, Color.BLACK)
            } else {
                qrBitmap.setPixel(x, y, Color.WHITE)
            }
        }
    }

    val bitmapWithFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmapWithFrame)
    canvas.drawColor(Color.WHITE) // заполняем весь bitmap белым цветом
    canvas.drawBitmap(qrBitmap, frameWidth.toFloat(), frameWidth.toFloat(), null) // рисуем QR-код с отступом рамки

    return bitmapWithFrame.asImageBitmap()
}