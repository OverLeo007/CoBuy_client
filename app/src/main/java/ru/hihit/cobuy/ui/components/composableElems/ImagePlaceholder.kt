package ru.hihit.cobuy.ui.components.composableElems

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.hihit.cobuy.ui.theme.getColorByHash


@Composable
fun ImagePlaceholder(
    uri: Uri?,
    modifier: Modifier,
    name: String,
    contentScale: ContentScale = ContentScale.Fit
) {
    if (uri == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(color = getColorByHash(name))
            }
            Text(
                text = name.first().uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
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