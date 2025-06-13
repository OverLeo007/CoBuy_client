package ru.hihit.cobuy.ui.components.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.pusher.client.channel.PusherEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import me.zhanghai.compose.preference.Preferences
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.groups.KickUserRequest
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.models.GroupData
import ru.hihit.cobuy.api.models.ListData
import ru.hihit.cobuy.api.models.UserData
import ru.hihit.cobuy.api.requesters.GroupRequester
import ru.hihit.cobuy.api.requesters.ImageRequester
import ru.hihit.cobuy.api.requesters.ListRequester
import ru.hihit.cobuy.api.requesters.MiscRequester
import ru.hihit.cobuy.api.requesters.handle
import ru.hihit.cobuy.models.EventType
import ru.hihit.cobuy.pusher.events.GroupChangedEvent
import ru.hihit.cobuy.pusher.events.ListChangedEvent
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.utils.getMultipartImageFromUri
import ru.hihit.cobuy.utils.makeShareQrIntent
import ru.hihit.cobuy.utils.toUri

class GroupViewModel(
    private val groupId: Int,
    private val navHostController: NavHostController,
    val preferencesFlow: Flow<Preferences>
) : PusherViewModel() {

    val showArchived = MutableStateFlow(false)

    val showCompleted = preferencesFlow.map {
        it.asMap().getOrDefault(SettingKeys.SHOW_COMPLETED_LISTS, false) as Boolean
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )


    var isGroupLoading = mutableStateOf(true)
    var isRefreshing = mutableStateOf(false)
    var isInviteLinkLoading = mutableStateOf(false)

    var group: MutableStateFlow<GroupData> =
        MutableStateFlow(GroupData(0, "", "".toUri(), "", 0, 0, 0, emptyList()))

    private var _lists: MutableStateFlow<List<ListData>> = MutableStateFlow(emptyList())
    val lists: StateFlow<List<ListData>> = _lists.asStateFlow()

    val filteredLists: StateFlow<List<ListData>> = combine(
        _lists,
        showCompleted,
        showArchived
    ) { items, showCompleted, showArchived ->
        items.filter { item ->
            item.hidden == showArchived
        }.filter { item ->
            if (showCompleted) {
                true
            } else {
                !item.isCompleted
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        updateAll()
        subscribeToGroup()
        subscribeToLists()
    }

    fun changeShowArchived() {
        Log.d("GroupViewModel", "Lists before change: ${filteredLists.value}")
        showArchived.value = !showArchived.value
        Log.d("GroupViewModel", "changeShowArchived: ${showArchived.value}")
        Log.d("GroupViewModel", "Lists after change: ${filteredLists.value}")
    }

    private fun subscribeToGroup() {
        pusherService.addListener(
            channelName = "group-changed.$groupId",
            eventName = "group-changed",
            listenerName = className + "GroupChanged",
            onEvent = { onWsGroupChanged(it) },
            onError = { message, e -> onWsError(message, e) }
        )
    }


    private fun onWsGroupChanged(event: PusherEvent) {
        Log.d("GroupViewModel", "onWsEvent: $event")
        val eventData: GroupChangedEvent
        try {
            eventData = GroupChangedEvent.fromJson(event.data)
            Log.d(className, "Got group from ws: $eventData")
            when (eventData.type) {
                EventType.Update -> this.group.value = eventData.data
                EventType.Delete -> navHostController.navigate(Route.Groups)
                EventType.Create -> {}
            }
        } catch (_: SerializationException) {
            Log.e(
                "GroupViewModel",
                "json from ws event is not serializable to GroupChangedEvent: $event"
            )
            return
        }
    }

    private fun subscribeToLists() {
        pusherService.addListener(
            channelName = "list-changed.$groupId",
            eventName = "list-changed",
            listenerName = className + "ListChanged",
            onEvent = { onWsListsChanged(it) },
            onError = { message, e -> onWsError(message, e) }
        )
    }

    private fun onWsListsChanged(event: PusherEvent) {
        Log.d("GroupViewModel", "onWsEvent: $event")
        val eventData: ListChangedEvent
        try {
            eventData = ListChangedEvent.fromJson(event.data)
            Log.d(className, "Got list from ws: $eventData")
            when (eventData.type) {
                EventType.Update -> {
                    val curLists = _lists.value.toMutableList()
                    val listIndex = curLists.indexOfFirst { it.id == eventData.data.id }
                    if (listIndex != -1) {
                        curLists[listIndex] = eventData.data
                        _lists.value = curLists
                    }
                }

                EventType.Delete -> {
                    val curLists = _lists.value.toMutableList()
                    curLists.removeIf { it.id == eventData.data.id }
                    _lists.value = curLists
                }

                EventType.Create -> {
                    val curLists = _lists.value.toMutableList()
                    if (curLists.find { list -> list.id == eventData.data.id } == null) {
                        curLists.add(eventData.data)
                    }
                    _lists.value = curLists
                }
            }
        } catch (_: SerializationException) {
            Log.e(
                "GroupViewModel",
                "json from ws event is not serializable to ListChangedEvent: $event"
            )
            return
        }
    }

    private fun onWsError(message: String?, e: Exception?) {
        Log.e("GroupViewModel", "onWsError: $message", e)
    }


    private fun getGroup() {
        isGroupLoading.value = true

        viewModelScope.launch {
            GroupRequester.getGroupById(groupId).handle(
                onSuccess = { response ->
                    group.value = response.data
                    Log.d("GroupViewModel", "getGroup: $response")
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "getGroup: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "getGroup: $it")
                },
                finally = {
                    isGroupLoading.value = false
                }
            )
        }
    }

    private fun getGroupImage() {

        viewModelScope.launch {
            ImageRequester.getGroupImage(groupId).handle(
                onSuccess = { response ->
                    group.value.avaUrl = response.data.avaUrl
                    Log.d("GroupViewModel", "getGroupImage: $response")
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "getGroupImage: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "getGroupImage: $it")
                }
            )
        }
    }

    private fun getLists() {
        viewModelScope.launch {
            ListRequester.getLists(groupId).handle(
                onSuccess = { response ->
                    _lists.value = response.data
                    Log.d("GroupViewModel", "getLists: $response")
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "getLists: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "getLists: $it")
                }
            )
        }
    }

    fun onRefresh() {
        updateAll()
    }

    private fun updateAll() {
        viewModelScope.launch {
            isRefreshing.value = true
            val jobs = listOf(
                launch { getGroup() },
                launch { getLists() }
            )
            jobs.joinAll()
            isRefreshing.value = false
        }
    }

    fun onImageSelected(context: Context, imageUri: Uri) {
        val picData = context.getMultipartImageFromUri(imageUri)
        Log.d("GroupViewModel", "onImageSelected: $picData")
        viewModelScope.launch {
            picData?.let {
                ImageRequester.uploadGroupImage(groupId, picData).handle(
                    onSuccess = {
                        Log.d("GroupViewModel", "onImageUploaded: $it")
                        getGroupImage()
                    },
                    onServerError = { parsedError ->
                        Log.w("GroupViewModel", "onImageUploaded: $parsedError")
                        Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                            .show()
                    },
                    onOtherError = {
                        Log.w("GroupViewModel", "onImageUploaded: $it")
                    }
                )
            }
        }
    }

    fun onKickUser(user: UserData) {

        viewModelScope.launch {
            GroupRequester.kickFromGroup(
                KickUserRequest(groupId, user.id)
            ).handle(
                onSuccess = {
                    Log.d("GroupViewModel", "onKickUser: $it")
                    val curUsers = group.value.members.toMutableList()
                    curUsers.removeIf { u -> u.id == user.id }
                    group.value.members = curUsers
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "onKickUser: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "onKickUser: $it")
                }
            )
        }
    }

    fun onNameChanged(name: String) {
        group.value.name = name

        viewModelScope.launch {
            val result = GroupRequester.updateGroup(
                groupId,
                CreateUpdateGroupRequest(name)
            )

            result.handle(
                onSuccess = {
                    Log.d("GroupViewModel", "onNameChanged: $it")
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "onNameChanged: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "onNameChanged: $it")
                }
            )
        }
    }

    fun onAddList(listName: String) {

        viewModelScope.launch {
            ListRequester.createList(
                CreateListRequest(listName, groupId)
            ).handle(
                onSuccess = { response ->
                    Log.d("GroupViewModel", "onAddList: $response")
                    val curLists = _lists.value.toMutableList()
                    if (curLists.find { list -> list.id == response.data.id } == null) {
                        curLists.add(response.data)
                    }
                    _lists.value = curLists
                },
                onServerError = { parsedError ->
                    Log.w("GroupViewModel", "onAddList: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "onAddList: $it")
                }
            )
        }
    }

    fun getInviteLink() {
        isInviteLinkLoading.value = true
        viewModelScope.launch {

            val result = MiscRequester.getInviteToken(groupId)

            result.handle(
                onSuccess = { response ->
                    group.value.inviteLink = response.token
                    isInviteLinkLoading.value = false
                    Log.d("GroupViewModel", "getInviteLink: $response")
                },
                onServerError = { parsedError ->
                    isInviteLinkLoading.value = false
                    Log.w("GroupViewModel", "getInviteLink: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                        .show()
                },
                onOtherError = {
                    Log.w("GroupViewModel", "getInviteLink: $it")
                },
                finally = {
                    isInviteLinkLoading.value = false
                }
            )
        }
    }

    fun onDeleteList(toDelListId: Int) {

        viewModelScope.launch {
            ListRequester.deleteList(toDelListId)
                .handle(
                    onSuccess = {
                        Log.d("GroupViewModel", "onDeleteList: $it")
                        val curLists = _lists.value.toMutableList()
                        curLists.removeIf { list -> list.id == toDelListId }
                        _lists.value = curLists
                    },
                    onServerError = { parsedError ->
                        Log.w("GroupViewModel", "onDeleteList: $parsedError")
                        Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                            .show()
                    },
                    onOtherError = {
                        Log.w("GroupViewModel", "onDeleteList: $it")
                    }
                )
        }
    }

    fun shareQr(qrBitmap: ImageBitmap, context: Context) {
        val shareIntent: Intent = context.makeShareQrIntent(qrBitmap, group.value.name)
        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }

    override fun onCleared() {
        super.onCleared()
        pusherService.removeListeners(
            className + "GroupChanged",
            className + "ListChanged"
        )
    }

    fun onArchiveList(listId: Int) {
        Log.d("GroupViewModel", "onArchiveList: $listId")
        val list = _lists.value.find { it.id == listId }
        list?.hidden = true
        updateListArchiveState(list)
    }

    fun onUnarchiveList(listId: Int) {
        Log.d("GroupViewModel", "onUnarchiveList: $listId")
        val list = _lists.value.find { it.id == listId }
        list?.hidden = false
        updateListArchiveState(list)
    }

    private fun updateListArchiveState(list: ListData?) {
        list?.let { list ->
            val logMsg = if (list.hidden) {
                "archived"
            } else {
                "unarchived"
            }

            viewModelScope.launch {
                ListRequester.updateList(
                    list.id,
                    UpdateListRequest(
                        list.name,
                        list.hidden
                    )
                ).handle(
                    onSuccess = {
                        Log.d("GroupViewModel", "$logMsg list: $it")
                    },
                    onServerError = { parsedError ->
                        Log.w("GroupViewModel", "onUpdateListArchiveState: $parsedError")
                        Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT)
                            .show()
                    },
                    onOtherError = {
                        Log.w("GroupViewModel", "onUpdateListArchiveState: $it")
                    }
                )
            }
        }
    }

}