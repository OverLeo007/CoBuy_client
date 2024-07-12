package ru.hihit.cobuy.ui.components.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    vm: SettingsViewModel
) {
    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(vm.avatarUrl)) }

    var isEditing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(vm.user.name) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                vm.onAvatarSelected(it) // Вызовите функцию обратного вызова с Uri изображения
            }
        }

    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.settings_word))
                    }
                },
                navHostController = navHostController,
                actions = {
                    IconButton(
                        onClick = {
                            vm.onLogout()
                            navHostController.navigate(Route.Authorization)
                        }

                    ) {
                        Icon(
                            painterResource(id = R.drawable.logout_24px),
                            contentDescription = "Logout icon",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(vm.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(127.dp)
                        .clip(CircleShape)
                        .clickable {
                            launcher.launch("image/*")
                        },
                    placeholder = ColorPainter(MaterialTheme.colorScheme.primary)

                )
                Spacer(modifier = Modifier.size(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.edit_square_24px),
                        contentDescription = "List Icon",
                        modifier = Modifier
                            .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
                        tint = Color.Transparent
                    )
                    if (isEditing) {
                        OutlinedTextField(
//                            modifier = Modifier.weight(1F),
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
                                        vm.onNameChanged(text)
                                    }
                                    isError = true
                                }
                            ),
                            singleLine = true
                        )

                    } else {
                        Text(
//                            modifier = Modifier.weight(1F),
                            text = text,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = {
                            isEditing = true
                        },
                        enabled = !isEditing
                    ) {
                        Icon(
                            painterResource(id = R.drawable.edit_square_24px),
                            contentDescription = "List Icon",
                            modifier = Modifier
                                .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            LazyColumn {
                item {
                    Text(
                        text = "Notification settings",
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
                items(10) {
                    var checked by remember {
                        mutableStateOf(true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notification $it",
                        )
                        val num = it
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                Toast.makeText(
                                    context,
                                    "Notification $num is ${if (it) "enabled" else "disabled"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                }
                item {
                    Text(
                        text = "Other settings",
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
                items(5) {
                    var checked by remember {
                        mutableStateOf(true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Setting $it",
                        )
                        val num = it
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                Toast.makeText(
                                    context,
                                    "Setting $num is ${if (it) "enabled" else "disabled"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                }
            }
        }
    }


}