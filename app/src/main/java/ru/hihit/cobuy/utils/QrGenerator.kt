package ru.hihit.cobuy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.graphics.ImageBitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.content.res.ResourcesCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

@Throws(WriterException::class)
fun createQRCodeBitmap(
    context: Context,
    text: String,
    width: Int = 500,
    height: Int = 500,
    logoRes: Int
): ImageBitmap {
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
            (width * 1.0f / 5f / logoWidth).coerceAtMost(height * 1.0f / 5f / logoHeight)

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