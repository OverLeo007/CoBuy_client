package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.hihit.cobuy.ui.theme.getColorByHash

@Composable
fun AvatarPlaceholder(modifier: Modifier, name: String) {
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
}