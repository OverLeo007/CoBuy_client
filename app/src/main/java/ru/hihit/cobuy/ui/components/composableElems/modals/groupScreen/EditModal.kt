package ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.UserData
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import ru.hihit.cobuy.utils.getFromPreferences


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditModal(
    onDismissRequest: () -> Unit = {},
    onImageSelected: (Uri) -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onUserRemoved: (UserData) -> Unit = {},
    onQrGet: () -> Unit = {},
    onQrShare: (ImageBitmap, Context) -> Unit,
    group: GroupData,
    vm: GroupViewModel
) {

    var imageUri by remember { mutableStateOf(group.avaUrl) }
    var userToDelete by remember { mutableStateOf<UserData?>(null) }

    var isEditing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(group.name) }
    var users = group.members.toMutableList()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                onImageSelected(it)
            }
        }

    val openRemoveUserModal = remember {
        mutableStateOf(false)
    }

    val openInviteModal = remember {
        mutableStateOf(false)
    }

    when {
        openRemoveUserModal.value -> RemoveUserModal(
            onDismissRequest = { openRemoveUserModal.value = false },
            onConfirmRequest = {
                openRemoveUserModal.value = false
                onUserRemoved(it)
                users.remove(it)
            },
            group = group,
            user = userToDelete!!
        )

        openInviteModal.value -> {
            onQrGet()
            AddUserModal(
                onDismissRequest = { openInviteModal.value = false },
                vm = vm,
                onShare = { qr, context -> onQrShare(qr, context) }
            )
        }
    }


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
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.editing_word),
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        },
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
                            IconButton(
                                onClick = {
                                    onDismissRequest()
                                },
                                enabled = false
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.Transparent
                                )
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            )
                        ) {
                            ImagePlaceholder(
                                uri = imageUri,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") },
                                name = group.name
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            if (isEditing) {
                                OutlinedTextField(
                                    modifier = Modifier.weight(1F),
                                    value = text,
                                    onValueChange = { newText -> text = newText },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (text.length in 3..100) {
                                                isEditing = false
                                                isError = false
                                                onNameChanged(text)
                                            }
                                            isError = true
                                        }
                                    ),
                                    singleLine = true
                                )

                            } else {
                                Text(
                                    modifier = Modifier.weight(1F),
                                    text = text,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            IconButton(
                                onClick = {
                                    isEditing = true
                                },
                                enabled = !isEditing
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.edit_square_24px),
                                    contentDescription = "Modify name",
                                    modifier = Modifier
                                        .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(PaddingValues(start = 12.dp, end = 8.dp))
                        ) {
                            Text(
                                text = stringResource(R.string.participants_word),
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                            IconButton(onClick = { openInviteModal.value = true }) {
                                Icon(
                                    painterResource(id = R.drawable.person_add_24px),
                                    contentDescription = "Modify name",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                        LazyColumn {
                            itemsIndexed(users) { index, user ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(
                                            PaddingValues(
                                                start = 24.dp,
                                                end = 8.dp
                                            )
                                        )
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Row {
                                        Text(
                                            text = user.name,
//                                        color = MaterialTheme.colorScheme.onTertiary
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Icon(
                                            painterResource(id = R.drawable.star_filled_24px),
                                            contentDescription = "is_admin",
                                            tint = if (user.id == group.ownerId) MaterialTheme.colorScheme.onTertiary else Color.Transparent
                                        )
                                    }
                                    val curUserId = LocalContext.current
                                        .getFromPreferences("user_id", 0)
                                    Row(
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        val canDelete = user.id != group.ownerId && group.ownerId == curUserId

                                        IconButton(
                                            enabled = canDelete,
                                            onClick = {
                                                userToDelete = user
                                                openRemoveUserModal.value = true
                                            }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.person_remove_24px),
                                                contentDescription = "delete",
                                                tint = if (canDelete) MaterialTheme.colorScheme.onTertiary else Color.Transparent
                                            )
                                        }
                                    }

                                }
                                if (index < group.members.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                                }
                            }
                        }
//                        Row {
//                            Text(
//                                text = "Hidden lists mode",
//                                color = MaterialTheme.colorScheme.onTertiary
//                            )
//
//                        }
                    }
                }
            }
        }
    }
}