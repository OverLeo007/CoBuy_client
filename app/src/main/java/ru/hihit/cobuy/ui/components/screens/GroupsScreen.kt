package ru.hihit.cobuy.ui.components.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.models.Group
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.UniversalModal
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GroupsScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(Toast.LENGTH_SHORT.toLong())
            isRefreshing = false
        }
    }


    val openAddModal = remember { mutableStateOf(false) }

    when {
        openAddModal.value -> {
            AddGroupModal(
                onAdd = {
                    vm.addGroup(it)

                    openAddModal.value = false
                },
                onDismiss = { openAddModal.value = false },
                title = stringResource(R.string.new_group)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.groups))
                    }
                },
                navHostController = navHostController,
                isBackArrow = false,
                navigation = {
                    IconButton(
                        onClick = {
                            val launchPermission: (cameraPermissionState: PermissionState) -> Unit = {
                                if (!it.hasPermission) {
                                    it.launchPermissionRequest()
                                }
                            }

                            launchPermission(cameraPermissionState)
                            navHostController.navigate(Route.Scanner)
                        },
                    ) {
                        Icon(
                            painterResource(id = R.drawable.qr_code_scanner_24px),
                            contentDescription = stringResource(R.string.scan_qr),
                        )
                    }
                }
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем группы", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
//                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(5) {
                        GroupItem(
                            group = Group(
                                name = "Прогеры №$it",
                                membersCount = Random.nextInt(1, 10),
                                listsCount = Random.nextInt(1, 10),
                                avaUrl = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1",
                            ),
                            onClick = {
                                navHostController.navigate(Route.Group + "/${it}")
                            },
                            onDelete = {group ->
                                vm.deleteGroup(group)
                            }
                        )

                    }
                }
            }
        }

        AddButton(
            onClick = { openAddModal.value = true },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupItem(
    group: Group,
    onClick: () -> Unit = {},
    onDelete: (Group) -> Unit = {}
) {

    val openModal = remember { mutableStateOf(false) }
    val modalButtons = mapOf<String, (Group) -> Unit>(
        stringResource(R.string.delete_word) to {
            onDelete(group)
            openModal.value = false
        }
    )

    when {
        openModal.value ->
            UniversalModal(
                subject = group,
                buttons = modalButtons,
                onDismiss = { openModal.value = false }
            )
    }


    Box(
        Modifier
            .combinedClickable(onClick = onClick, onLongClick = { openModal.value = true })
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(group.avaUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Group ${group.name} Avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.height(48.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.name, overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painterResource(id = R.drawable.group_24px),
                        contentDescription = "People Icon",
                        modifier = Modifier
                            .sizeIn(maxHeight = MaterialTheme.typography.bodySmall.fontSize.value.dp)
                            .padding(PaddingValues(end = 4.dp)),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = "${group.membersCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painterResource(id = R.drawable.list_alt_24px),
                        contentDescription = "List Icon",
                        modifier = Modifier
                            .sizeIn(maxHeight = MaterialTheme.typography.bodySmall.fontSize.value.dp)
                            .padding(PaddingValues(end = 4.dp)),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = "${group.listsCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onTertiary)
                    )
                }
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AddGroupModal(
    onAdd: (Group) -> Unit = {},
    onDismiss: () -> Unit = {},
    title: String = stringResource(R.string.new_group),
    namePlaceholder: String = stringResource(R.string.group_name),

) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isNameCorrect by remember { mutableStateOf(true) }
    var isImageCorrect by remember { mutableStateOf(true) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var text by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                isImageCorrect = true
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
                    if (!isImageCorrect) {
                        Text(
                            text = stringResource(R.string.choose_group_avatar),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (!isNameCorrect) {
                        Text(
                            text = stringResource(R.string.enter_group_name),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (!isNameCorrect || !isImageCorrect) {
                        Spacer(modifier = Modifier.size(16.dp))
                    }

                    Button(onClick = {
                        isNameCorrect = text.isNotEmpty()
                        isImageCorrect = imageUri != null
                        if (isNameCorrect && isImageCorrect)
                            onAdd(Group(name = text, avaUrl = imageUri.toString()))
                    }) {
                        Text(text = stringResource(R.string.submit_word))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}