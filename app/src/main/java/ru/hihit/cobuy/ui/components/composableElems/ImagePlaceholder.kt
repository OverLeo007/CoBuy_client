package ru.hihit.cobuy.ui.components.composableElems

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.hihit.cobuy.ui.theme.getColorByHash


@Composable
fun ImagePlaceholder(
    uri: Uri?,
    modifier: Modifier,
    name: String,
    contentScale: ContentScale = ContentScale.Crop,
    isFullScreen: Boolean = false,
    onFullScreenChange: (Boolean) -> Unit = {},
    isFullText: Boolean = false
) {


    if (uri == null) {
        val targetColor = getColorByHash(name)
        val animatedColor by animateColorAsState(targetValue = targetColor,
            label = "placeholder color animation")
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(color = animatedColor)
            }
            Text(
                text = if (isFullText) name else name.first().uppercase(),
                style = if (isFullText) MaterialTheme.typography.bodySmall else MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = if (isFullText) Modifier.align(Alignment.Center) else Modifier
            )
        }
    } else {
        if (isFullScreen) {

            Dialog(onDismissRequest = { onFullScreenChange(false) }) {
                val zoomState = rememberZoomState()
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState),
                    contentDescription = "Fullscreen Picture",
                    contentScale = ContentScale.Fit,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar",
                contentScale = contentScale,
                modifier = modifier,
                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
            )
        }
    }
}