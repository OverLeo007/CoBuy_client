package ru.hihit.cobuy.ui.components.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.UniversalModal
import ru.hihit.cobuy.ui.components.composableElems.modals.groupsScreen.AddGroupModal
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GroupsScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val isRefreshing by vm.isLoading.observeAsState(false)

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val groups by vm.groups.collectAsState()
    Log.d("GroupsScreen", groups.toString())



    val openAddModal = remember { vm.openAddModal }
    val isAddingGroup = remember {
        vm.isAddingGroup
    }

    when {
        openAddModal.value -> {
            AddGroupModal(
                onAdd = {
                    vm.addGroup(it)

//                    openAddModal.value = false
                },
                onDismiss = { openAddModal.value = false },
                title = stringResource(R.string.new_group),
                isGroupAdding = isAddingGroup
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
                    vm.onRefresh()
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(groups) { group ->
                        GroupItem(
                            group = group,
                            onClick = {
                                navHostController.navigate(Route.Group + "/${group.id}")
                            },
                            onDelete = {
                                vm.deleteGroup(group)
                            },
                            onLeave = {
                                vm.leaveGroup(group)
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
    group: GroupData,
    onClick: () -> Unit = {},
    onDelete: (GroupData) -> Unit = {},
    onLeave: (GroupData) -> Unit = {}
) {
    val curUserId = LocalContext.current
        .getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
        .getInt("user_id", 0)

    val openModal = remember { mutableStateOf(false) }
    val modalButtons = mutableMapOf<String, (GroupData) -> Unit>()
    modalButtons += stringResource(id = R.string.leave_word) to {
        onLeave(group)
        openModal.value = false
    }

    if (curUserId == group.ownerId) {
        modalButtons += (stringResource(id = R.string.delete_word) to {
            onDelete(group)
            openModal.value = false
        })
    }


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

            ImagePlaceholder(
                uri = group.avaUrl,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                name = group.name,
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


