package ru.hihit.cobuy.ui.components.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.models.Group
import ru.hihit.cobuy.models.User
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import ru.hihit.cobuy.utils.copyToClipboard
import ru.hihit.cobuy.utils.createQRCodeBitmap
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GroupScreen(
    navHostController: NavHostController,
    vm: GroupViewModel
) {
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    val openModal = remember {
        mutableStateOf(false)
    }

    when {
        openModal.value -> EditModal(
            onDismissRequest = { openModal.value = false },
            group = Group.default(), // TODO: pass group from vm
            onImageSelected = { vm.onImageSelected(it) },
            onUserRemoved = { vm.onUserRemoved(it) },
            onNameChanged = { vm.onNameChanged(it) }
        )
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000L)
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                Toast
                                    .makeText(context, "Open group edit", Toast.LENGTH_SHORT)
                                    .show()
                                openModal.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(vm.groupIconUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Group ${vm.groupId} Avatar",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "Group ${vm.groupId}")
                        }
                    }
                },
                navHostController = navHostController,
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем списки", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ListItem(listName = "Выполненный список покупок", fillPercents = 1F)
                    }
                    items(56) {
                        ListItem(
                            listName = "Список покупок $it",
                            fillPercents = Random.nextFloat(),
                            onClick = {
                                Toast.makeText(context, "Open list $it", Toast.LENGTH_SHORT).show()
                                navHostController.navigate(Route.List + "/${it}")
                            }
                        )
                    }
                }
            }
        }
        AddButton(
            onClick = { Toast.makeText(context, "List added", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}

@Composable
fun ListItem(
    listName: String,
    fillPercents: Float,
    onClick: () -> Unit = {}
) {
    Box(
        Modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = listName,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { fillPercents },
                    modifier = Modifier.fillMaxWidth(0.8F),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F),
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "${(fillPercents * 100).roundToInt()}%",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (fillPercents == 1F) 0.5f else 1F)
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditModal(
    onDismissRequest: () -> Unit = {},
    onImageSelected: (Uri) -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onUserRemoved: (User) -> Unit = {},
    group: Group = Group.default(),
) {

    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(group.avaUrl)) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    var isEditing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(group.name) }


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

    when {
        openRemoveUserModal.value -> RemoveUserModal(
            onDismissRequest = { openRemoveUserModal.value = false },
            onConfirmRequest = {
                openRemoveUserModal.value = false
                onUserRemoved(it)
            },
            group = group,
            user = userToDelete
        )
    }


    val openInviteModal = remember {
        mutableStateOf(false)
    }

    when {
        openInviteModal.value -> AddUserModal(
            onDismissRequest = { openInviteModal.value = false },
            group = group
        )
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
                                    text = "Редактирование",
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
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Group ${group.name} Avatar",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") },
                                placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
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
                                text = "Участники",
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
                            items(56) {
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
                                    Text(
                                        text = "Райан гослинг",
//                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { /*TODO*/ }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.star_unfilled_24px),
                                                contentDescription = "is_admin",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                userToDelete =
                                                    User.default() /*TODO: сделать подтягивание юзеров с vm*/
                                                openRemoveUserModal.value = true
                                            }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.person_remove_24px),
                                                contentDescription = "delete",
                                                tint = MaterialTheme.colorScheme.onTertiary
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                            }

                        }

                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserModal(
    onDismissRequest: () -> Unit = {},
    context: Context = LocalContext.current,
    group: Group = Group.default()
) {
    val qrCodeBitmap = createQRCodeBitmap(
        context,
        group.inviteLink,
        logoRes = R.mipmap.ic_launcher_round
    )
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6F),
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
                        }
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(
                            PaddingValues(
                                top = 4.dp,
                                bottom = 24.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        )

                    ) {
                        Image(bitmap = qrCodeBitmap, contentDescription = "QR code")
                        Text(
                            text = "Пригласить в ${group.name}",
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = group.inviteLink,
                                maxLines = 2,
                                modifier = Modifier.weight(1f),
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(onClick = {
                                context.copyToClipboard(group.inviteLink)
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.content_copy_24px),
                                    contentDescription = "Add user",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun RemoveUserModal(
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: (User) -> Unit = {},
    group: Group = Group.default(),
    user: User? = User.default()
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
                text = "Вы уверены, что хотите удалить пользователя ${user?.name} из группы ${group.name}?",
                color = MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().
                padding(PaddingValues(bottom = 8.dp)),
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.weight(1F)

                ) {
                    Text(
                        text = "Отмена",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { user?.let { onConfirmRequest(it) } },
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}