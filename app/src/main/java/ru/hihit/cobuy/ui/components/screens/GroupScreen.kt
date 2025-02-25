package ru.hihit.cobuy.ui.components.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.ui.components.composableElems.FloatingActionButtonImpl
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.UniversalModal
import ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen.AddListModal
import ru.hihit.cobuy.ui.components.composableElems.modals.groupScreen.EditModal
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import kotlin.math.roundToInt

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
    val lists by vm.lists.collectAsState()

    val openEditModal = remember {
        mutableStateOf(false)
    }

    val openAddListModal = remember {
        mutableStateOf(false)
    }

    var isArchive by remember { mutableStateOf(false)}
    var archiveIcon = if (isArchive) {
        painterResource(R.drawable.unarchive)
    } else {
        painterResource(R.drawable.archive)
    }


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
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Bottom,
//                modifier = Modifier.align(Alignment.BottomEnd)
            ) {

                FloatingActionButtonImpl(
                    onClick = {
                        isArchive = !isArchive
                        when (isArchive) {

                            true -> snackBarScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = "Просмотр арихивированных списков",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            false -> snackBarScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = "Просмотр активных списков",
                                    duration = SnackbarDuration.Short
                                )
                            }
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
                    items = lists.filter { list -> list.hidden == isArchive },
                    key = { it.id }
                ) { list ->
                    ListItem(
                        list,
                        fillPercents = if (list.productsCount == 0 || list.checkedProductsCount == 0) {
                            0f
                        } else {
                            list.checkedProductsCount.toFloat() / list.productsCount
                        },
                        onClick = {
                            navHostController.navigate(Route.List + "/${list.id}")
                        },
                        onDelete = { vm.onDeleteList(it) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(150.dp))
                }
            }
        }
    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize(),
//    ) {
//        Column {
////            TopAppBarImpl(
////                title = {
////                    if (isGroupLoading) {
////                        Box(
////                            modifier = Modifier
////                                .fillMaxSize(),
////                            contentAlignment = Alignment.Center
////                        ) {
////                            CircularProgressIndicator(
////                                color = MaterialTheme.colorScheme.onSurface
////                            )
////                        }
////                    } else {
////                        Box(
////                            modifier = Modifier
////                                .fillMaxSize()
////                                .clickable {
////                                    openEditModal.value = true
////                                },
////                            contentAlignment = Alignment.Center
////                        ) {
////                            Row(
////                                modifier = Modifier
////                                    .fillMaxWidth(),
////                                verticalAlignment = Alignment.CenterVertically,
////                                horizontalArrangement = Arrangement.Center
////                            ) {
////                                ImagePlaceholder(
////                                    uri = group.avaUrl,
////                                    modifier = Modifier
////                                        .size(48.dp)
////                                        .clip(CircleShape),
////                                    name = group.name
////                                )
////                                Spacer(modifier = Modifier.size(8.dp))
////                                Text(text = group.name)
////                            }
////                        }
////                    }
////                },
////                navHostController = navHostController,
////            )
////            SwipeRefreshImpl(
////                swipeState = swipeRefreshState,
////                onRefresh = {
////                    vm.onRefresh()
//////                    isRefreshing = true
////                }) {
////                LazyColumn(
////                    modifier = Modifier.fillMaxSize()
////                ) {
////                    items(lists) { list ->
////                        ListItem(
////                            list,
////                            fillPercents = if (list.productsCount == 0 || list.checkedProductsCount == 0) {
////                                0f
////                            } else {
////                                list.checkedProductsCount.toFloat() / list.productsCount
////                            },
////                            onClick = {
////                                navHostController.navigate(Route.List + "/${list.id}")
////                            },
////                            onDelete = { vm.onDeleteList(it) }
////                        )
////                    }
////                }
////            }
//        }
//        Column(
//            verticalArrangement = Arrangement.Bottom,
//            modifier = Modifier.align(Alignment.BottomEnd)
//        ) {
//
//            FloatingActionButton(
//                onClick = {
//                    Toast.makeText(context, "Opened archive", Toast.LENGTH_SHORT).show()
//                },
//                modifier = Modifier
//                    .padding(PaddingValues(end = 16.dp, bottom = 8.dp)),
//                shape = RoundedCornerShape(10.dp),
//            ) {
//                Icon(painterResource(R.drawable.folder_eye), contentDescription = "Archive")
//            }
//            AddButton(
//                onClick = { openAddListModal.value = true },
//                modifier = Modifier
//            )
//        }
//    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItem(
    listItem: ListData,
    fillPercents: Float,
    onClick: () -> Unit = {},
    onDelete: (Int) -> Unit = {}
) {

    val openModal = remember { mutableStateOf(false) }
    val modalButtons = mapOf<String, (ListData) -> Unit>(
        stringResource(R.string.delete_word) to {
            onDelete(it.id)
            openModal.value = false
        }
    )

    when {
        openModal.value ->
            UniversalModal(
                subject = listItem,
                buttons = modalButtons,
                onDismiss = { openModal.value = false }
            )
    }

    Box(
        Modifier
            .combinedClickable(onClick = onClick, onLongClick = { openModal.value = true })
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











