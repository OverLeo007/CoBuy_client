package ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.utils.createQRCodeBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserModal(
    onDismissRequest: () -> Unit = {},
    onShare: (ImageBitmap, Context) -> Unit,
    context: Context = LocalContext.current,
    group: GroupData,
) {
    val qrCodeBitmap =  createQRCodeBitmap(
        context,
        group.inviteLink!!,
        logoRes = R.mipmap.ic_launcher_round,
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8F),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box {
                Column {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onDismissRequest()
                                },
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.arrow_back_ios_24px),
                                    contentDescription = "Back",
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { onShare(qrCodeBitmap, context) }) {
                                Icon(
                                    painterResource(id = R.drawable.share_24px),
                                    contentDescription = "share"
                                )
                            }
                        }
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    top = 4.dp,
                                    bottom = 24.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                )
                            )
                            .fillMaxWidth()
                    ) {
                        Image(bitmap = qrCodeBitmap, contentDescription = "QR code", modifier = Modifier)

                        Text(
                            text = stringResource(R.string.invite_to_group_word, group.name),
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.titleLarge
                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = group.inviteLink,
//                                maxLines = 2,
//                                modifier = Modifier.weight(1f),
//                                overflow = TextOverflow.Ellipsis
//                            )
//                            IconButton(onClick = {
//                                context.copyToClipboard(group.inviteLink)
//                            }) {
//                                Icon(
//                                    painterResource(id = R.drawable.content_copy_24px),
//                                    contentDescription = "Add user",
//                                    tint = MaterialTheme.colorScheme.onTertiary
//                                )
//                            }
//                        }
                    }
                }
            }
        }
    }
}