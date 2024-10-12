package ru.hihit.cobuy.ui.components.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pusher.client.channel.PusherEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.GroupRequester
import ru.hihit.cobuy.api.ImageRequester
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.api.ListRequester
import ru.hihit.cobuy.api.MiscRequester
import ru.hihit.cobuy.api.UserData
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.groups.KickUserRequest
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.utils.getMultipartImageFromUri
import ru.hihit.cobuy.utils.makeShareQrIntent
import ru.hihit.cobuy.utils.toUri

class GroupViewModel(private val groupId: Int) : ViewModel() {

    var isGroupLoading = mutableStateOf(true)
    var isRefreshing = mutableStateOf(false)
    var isInviteLinkLoading = mutableStateOf(false)

    var group: MutableStateFlow<GroupData> =
        MutableStateFlow(GroupData(0, "", "".toUri(), "", 0, 0, 0, emptyList()))
    var lists: MutableStateFlow<List<ListData>> = MutableStateFlow(emptyList())

    private val pusherService = App.getPusherService()

    init {
        updateAll()
        subscribeToGroup()
    }



    private fun subscribeToGroup() {
        pusherService.subscribeToChannel(
            channelName = "group-changed.$groupId",
            eventName = "group-changed",
            onEvent = { onWsEvent(it) },
            onError = { message, e -> onWsError(message, e) }
        )
    }


    private fun onWsEvent(event: PusherEvent) {
        Log.d("GroupViewModel", "onWsEvent: $event")
    }

    private fun onWsError(message: String?, e: Exception?) {
        Log.e("GroupViewModel", "onWsError: $message", e)
    }


    private fun getGroup() {
        isGroupLoading.value = true
        GroupRequester.getGroupById(
            groupId,
            callback = { response ->
                response?.data?.let {
                    group.value = it
                    group.value.avaUrl?.let {
                        group.value.avaUrl = group.value.avaUrl.toString().replace("public", "http://hihit.sytes.net/storage").toUri()  //FIXME remove this line
                }
                }
                Log.d("GroupViewModel", "getGroup: $response")
                isGroupLoading.value = false
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getGroup: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                isGroupLoading.value = false
            }
        )
    }

    private fun getGroupImage() {
        ImageRequester.getGroupImage(groupId,
            callback = { response ->
                response?.data?.let {
                    group.value.avaUrl = it.avaUrl
                }
                Log.d("GroupViewModel", "getGroupImage: $response")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getGroupImage: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun getLists() {
        ListRequester.getLists(
            groupId,
            callback = { response ->
                response?.data?.let {
                    lists.value = it
                }
                Log.d("GroupViewModel", "getLists: $response")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getLists: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
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
        picData?.let {
            ImageRequester.uploadGroupImage(
                groupId,
                picData,
                callback = {
                    Log.d("GroupViewModel", "onImageSelected: $it")
                    getGroupImage()
                },
                onError = { code, body ->
                    Log.e("GroupViewModel", "onImageSelected: $code $body")
                    Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                }
            )
        }
        Log.d("GroupViewModel", "onImageSelected: $picData")
    }

    fun onKickUser(user: UserData) {
        GroupRequester.kickFromGroup(
            KickUserRequest(groupId, user.id),
            callback = {
                Log.d("GroupViewModel", "onKickUser: $it")
                val curUsers = group.value.members.toMutableList()
                curUsers.removeIf { u -> u.id == user.id }
                group.value.members = curUsers
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onKickUser: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun onNameChanged(name: String) {
        group.value.name = name
        GroupRequester.updateGroup(
            groupId,
            CreateUpdateGroupRequest(group.value.name),
            callback = {
                Log.d("GroupViewModel", "onNameChanged: $it")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onNameChanged: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun onAddList(listName: String) {
        ListRequester.createList(
            CreateListRequest(listName, groupId),
            callback = {
                Log.d("GroupViewModel", "onAddList: $it")
                it?.let {
                    val curLists = lists.value.toMutableList()
                    curLists.add(it.data)
                    lists.value = curLists
                }
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onAddList: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun getInviteLink() {
        isInviteLinkLoading.value = true
        viewModelScope.launch {
            MiscRequester.getInviteToken(
                groupId,
                callback = { response ->
                    group.value.inviteLink = response?.token
                    isInviteLinkLoading.value = false
                    Log.d("GroupViewModel", "getInviteLink: $response")
                },
                onError = { code, body ->
                    isInviteLinkLoading.value = false
                    Log.e("GroupViewModel", "getInviteLink: $code $body")
                    Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    fun onDeleteList(toDelListId: Int) {
        ListRequester.deleteList(
            toDelListId,
            callback = {
                Log.d("GroupViewModel", "onDeleteList: $it")
                val curLists = lists.value.toMutableList()
                curLists.removeIf { list -> list.id == toDelListId }
                lists.value = curLists
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onDeleteList: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun shareQr(qrBitmap: ImageBitmap, context: Context) {
        val shareIntent: Intent = context.makeShareQrIntent(qrBitmap, group.value.name)
        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }

    override fun onCleared() {
        super.onCleared()
        pusherService.unsubscribeFromChannel("group-changed.$groupId")
    }

}