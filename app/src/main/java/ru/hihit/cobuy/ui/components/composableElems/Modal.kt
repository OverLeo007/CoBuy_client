package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun <T> UniversalModal(
    subject: T,
    buttons: Map<String, (T) -> Unit>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8F),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            LazyColumn {
                itemsIndexed(buttons.keys.toList()) { index, buttonName ->
                    TextButton(onClick = { buttons[buttonName]?.invoke(subject) }) {
                        Text(buttonName, color = MaterialTheme.colorScheme.onSurface)
                    }
                    // Проверка, чтобы не отображать разделитель после последней кнопки
                    if (index < buttons.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    }
                }
            }
        }
    }
}