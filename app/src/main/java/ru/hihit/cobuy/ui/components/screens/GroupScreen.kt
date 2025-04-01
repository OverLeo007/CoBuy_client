package ru.hihit.cobuy.ui.components.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.footerPreference
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.ui.components.composableElems.FloatingActionButtonImpl
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen.AddListModal
import ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen.EditModal
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navHostController: NavHostController,
    vm: GroupViewModel
) {
    val context = LocalContext.current

    val isGroupLoading by remember { vm.isGroupLoading }

    var isRefreshing by remember { vm.isRefreshing }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarScope = rememberCoroutineScope()

    val group by vm.group.collectAsState()
    val lists by vm.filteredLists.collectAsStateWithLifecycle()

    val openEditModal = remember {
        mutableStateOf(false)
    }

    val openAddListModal = remember {
        mutableStateOf(false)
    }

    val isArchive by vm.showArchived.collectAsState()
    var archiveIcon = if (isArchive) {
        painterResource(R.drawable.unarchive)
    } else {
        painterResource(R.drawable.archive)
    }
    var onArchiveButtonMessage: String = if (isArchive) {
        stringResource(R.string.show_active_lists)
    } else {
        stringResource(R.string.show_archived_lists)
    }

    val dismissStateScope = rememberCoroutineScope()


    val listsLazyColumnState = rememberLazyListState()

    when {
        openEditModal.value -> EditModal(
            onDismissRequest = { openEditModal.value = false },
            group = group,
            onImageSelected = { vm.onImageSelected(context, it) },
            onUserRemoved = { vm.onKickUser(it) },
            onNameChanged = { vm.onNameChanged(it) },
            onQrGet = { vm.getInviteLink() },
            onQrShare = { qr, cntxt -> vm.shareQr(qr, cntxt) },
            vm = vm
        )

        openAddListModal.value -> AddListModal(
            onAdd = {
                vm.onAddList(it)
                openAddListModal.value = false
            },
            onDismiss = { openAddListModal.value = false }
        )

    }



    Scaffold(
        topBar = {
            TopAppBarImpl(
                title = {
                    if (isGroupLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    openEditModal.value = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ImagePlaceholder(
                                    uri = group.avaUrl,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    name = group.name
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(text = group.name)
                            }
                        }
                    }
                },
                navHostController = navHostController,
            )
        },
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp)
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Bottom,
            ) {

                FloatingActionButtonImpl(
                    onClick = {
                        vm.changeShowArchived()
                        snackBarScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(
                                message = onArchiveButtonMessage,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                    }
                ) {
                    Icon(archiveIcon, contentDescription = "Archive")
                }
                FloatingActionButtonImpl(
                    onClick = { openAddListModal.value = true }
                )
            }
        }
    ) { paddingValues ->
        Log.d("GroupScreen", "displaying lists: $lists")
        SwipeRefreshImpl(
            swipeState = swipeRefreshState,
            onRefresh = {
                vm.onRefresh()
            }) {
            HorizontalDivider()
            LazyColumn(
                state = listsLazyColumnState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(
                    items = lists,
                    key = { it.id.toString() }
                ) { list ->
                    ListItem(
                        list,
                        onClick = {
                            navHostController.navigate(Route.List + "/${list.id}")
                        },
                        placementModifier = Modifier.animateItem(),
                        onDelete = { vm.onDeleteList(it) },
                        onArchive = { vm.onArchiveList(it) },
                        onUnarchive = { vm.onUnarchiveList(it) },
                        dismissStateScope = dismissStateScope,
                        showArchived = isArchive,
                        snackbarScope = snackBarScope,
                        snackbarHostState = snackbarHostState
                    )
                }
                val hiddenListsCount = lists.filter {
                    list -> list.isCompleted
                }.size
                if (!(vm.showCompleted.value) && hiddenListsCount > 0) {
                    footerPreference(
                        key = "footer",
                        summary = { Text(stringResource(R.string.hidden_lists_count) + " " + hiddenListsCount) },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(150.dp))
                }
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ListItem(
    listItem: ListData,
    onClick: () -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onArchive: (Int) -> Unit = {},
    onUnarchive: (Int) -> Unit = {},
    showArchived: Boolean = false,
    @SuppressLint("ModifierParameter") placementModifier: Modifier,
    snackbarScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    dismissStateScope: CoroutineScope
) {
    val context = LocalContext.current
    val isArchived = remember { mutableStateOf(listItem.hidden) }
    val isDeleted = remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (!isDeleted.value) {
                        Log.d("GroupScreen", "onDeleteList")
                    }
                    isDeleted.value = true
                    snackbarScope.launch {
                        val result = showSnackbar(
                            snackbarHostState,
                            message = "Список \"${listItem.name}\" будет удален",
                            actionLabel = "Отмена"
                        )
                        when (result) {
                            SnackbarResult.Dismissed -> {
                                onDelete(listItem.id)
                                Toast.makeText(
                                    context,
                                    "Список удален",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            SnackbarResult.ActionPerformed -> {
                                isDeleted.value = false
                            }
                        }
                    }
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    val isUnarchiveAction = listItem.hidden
                    isArchived.value = !isUnarchiveAction

                    snackbarScope.launch {
                        val message = if (isUnarchiveAction)
                            "Список \"${listItem.name}\" будет удален из архива"
                        else
                            "Список \"${listItem.name}\" будет архивирован"

                        val result = showSnackbar(
                            snackbarHostState,
                            message = message,
                            actionLabel = "Отмена"
                        )

                        when (result) {
                            SnackbarResult.Dismissed -> {
                                if (isUnarchiveAction) {
                                    onUnarchive(listItem.id)
                                    Toast.makeText(context, "Список удален из архива", Toast.LENGTH_SHORT).show()
                                } else {
                                    onArchive(listItem.id)
                                    Toast.makeText(context, "Список архивирован", Toast.LENGTH_SHORT).show()
                                }
                            }
                            SnackbarResult.ActionPerformed -> {
                                isArchived.value = isUnarchiveAction
                            }
                        }
                    }
                }
                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState true
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * 0.25f }
    )

    LaunchedEffect(isDeleted.value, isArchived.value) {
        if (!isDeleted.value || !isArchived.value) {
            dismissStateScope.launch {
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }
    }

    val fillPercents: Float = if (listItem.productsCount == 0 || listItem.checkedProductsCount == 0) {
        0f
    } else {
        listItem.checkedProductsCount.toFloat() / listItem.productsCount
    }
    Log.w("ListItem", "list ${listItem.name}: visible: ${!isDeleted.value && (isArchived.value == showArchived)}")

    AnimatedVisibility(
        modifier = placementModifier,
        visible = !isDeleted.value && (isArchived.value == showArchived),
        enter = fadeIn(
            animationSpec = tween(durationMillis = 150)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 150)
        )
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                DismissListItemBackground(dismissState, showArchived)
            }
        ) {
            Column {
                Box(
                    Modifier
                        .combinedClickable(onClick = onClick)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = listItem.name,
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
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissListItemBackground(
    dismissState: SwipeToDismissBoxState,
    isInArchive: Boolean = false
) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.surfaceBright
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val leftIcon = if (isInArchive) {
            painterResource(R.drawable.unarchive)
        } else {
            painterResource(R.drawable.archive)
        }

        Icon(
            painter = leftIcon,
            tint = Color.White,
            contentDescription = "Archive or Unarchive"
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            tint = Color.White,
            contentDescription = "delete"
        )
    }
}


suspend fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String
): SnackbarResult {
    return snackbarHostState
        .showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long,
            withDismissAction = true
        )
}








