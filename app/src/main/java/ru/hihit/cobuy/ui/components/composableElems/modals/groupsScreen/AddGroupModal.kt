package ru.hihit.cobuy.ui.components.composableElems.modals.groupsScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.hihit.cobuy.R
import ru.hihit.cobuy.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AddGroupModal(
    onAdd: (Group) -> Unit = {},
    onDismiss: () -> Unit = {},
    title: String = stringResource(R.string.new_group),
    namePlaceholder: String = stringResource(R.string.group_name),
    isGroupAdding: MutableState<Boolean> = mutableStateOf(false)
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isNameCorrect by remember { mutableStateOf(true) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var text by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
            }
        }

    Dialog(onDismissRequest = onDismiss) {
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
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onDismiss()
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
                            onClick = {},
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        if (imageUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        launcher.launch("image/*")
                                    },
                                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)

                            )
                        } else {
                            Image(
                                painter = ColorPainter(MaterialTheme.colorScheme.primary),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") },

                                )
                        }
                        Spacer(modifier = Modifier.size(16.dp))
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
                                    keyboardController?.hide()
                                }
                            ),
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = namePlaceholder,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            },
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    Spacer(modifier = Modifier.size(20.dp))
                    if (!isNameCorrect) {
                        Text(
                            text = stringResource(R.string.enter_group_name),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.size(16.dp))

                    }
                    Button(onClick = {
                        isNameCorrect = text.isNotEmpty()
                        if (isNameCorrect)
                            onAdd(Group(name = text, avaUrl = imageUri.toString()))
                    },
                        enabled = !isGroupAdding.value
                    ) {
                        if (isGroupAdding.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(text = stringResource(R.string.submit_word))
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}