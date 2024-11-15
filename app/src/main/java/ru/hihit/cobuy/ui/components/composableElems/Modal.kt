package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
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
                    Row(

                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.clickable { buttons[buttonName]?.invoke(subject) }
                            .fillMaxWidth()
                            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp))

                    ) {
                        Text(
                            text = buttonName,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Left
                        )
                    }
                    if (index < buttons.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    }
                }
            }
        }
    }
}
