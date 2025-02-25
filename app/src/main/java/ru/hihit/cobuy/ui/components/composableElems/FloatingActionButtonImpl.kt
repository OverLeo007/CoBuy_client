package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Floating action button with a plus icon by default.
 */
@Composable
fun FloatingActionButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {
        Icon(Icons.Filled.Add, contentDescription = "Add")
    }
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(PaddingValues(end = 16.dp, bottom = 8.dp)),

        shape = RoundedCornerShape(10.dp),
        content = content
    )
}