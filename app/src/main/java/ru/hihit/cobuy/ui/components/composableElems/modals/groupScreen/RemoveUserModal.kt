package ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.UserData


@Composable
fun RemoveUserModal(
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: (UserData) -> Unit = {},
    group: GroupData,
    user: UserData
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.confirm_user_removal, user.name, group.name),
                color = MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(bottom = 8.dp)),
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.weight(1F)

                ) {
                    Text(
                        text = stringResource(R.string.cancel_word),
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { onConfirmRequest(user) },
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        text = stringResource(R.string.delete_word),
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}