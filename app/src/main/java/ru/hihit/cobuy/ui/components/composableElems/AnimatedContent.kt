package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout

@Composable
fun AnimatedContent(
    isAnimating: Boolean,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = Color.LightGray.copy(alpha = 0.5f),
        targetValue = Color.LightGray,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    BoxWithConstraints {
        if (isAnimating) {
            Box(
                modifier = Modifier
                    .size(maxWidth, maxHeight)
                    .background(color)
            )
        } else {
            content()
        }
    }
}