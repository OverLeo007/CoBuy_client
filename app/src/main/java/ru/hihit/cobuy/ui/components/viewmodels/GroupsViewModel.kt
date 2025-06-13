package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.hihit.cobuy.App
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.models.GroupData
import ru.hihit.cobuy.api.requesters.GroupRequester
import ru.hihit.cobuy.api.requesters.MiscRequester
import ru.hihit.cobuy.api.requesters.handle
import ru.hihit.cobuy.models.Group
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.utils.isJwt
import ru.hihit.cobuy.utils.parseJson

class GroupsViewModel : ViewModel() {

    var isLoading = MutableLiveData(false)
    var scanError: String by mutableStateOf("")

    val groups: MutableStateFlow<List<GroupData>> = MutableStateFlow(emptyList())

    val openAddModal = mutableStateOf(false)
    val isAddingGroup = mutableStateOf(false)

    val pusherService = App.getPusherService()


    init {
        getGroups()
    }


    private fun getGroups() {
        getAndProcessGroups { groups.value = it }
    }

    private fun updateGroups() {
        getAndProcessGroups { newGroups ->
            val currentGroups = groups.value.toMutableList()

            for (newGroup in newGroups) {
                if (!currentGroups.any { it.id == newGroup.id }) {
                    currentGroups.add(newGroup)
                }
            }

            currentGroups.removeIf { currentGroup ->
                !newGroups.any { it.id == currentGroup.id }
            }

            groups.value = currentGroups
        }
    }

    fun onRefresh() {
        getGroups()
    }


    fun addGroup(group: Group) {
        isAddingGroup.value = true

        viewModelScope.launch {
            val result = GroupRequester.createGroup(CreateUpdateGroupRequest(group.name))

            result.handle(
                onSuccess = { response ->
                    Log.d("GroupsViewModel", "addGroup: $response")
                    onGroupAdded(response.data)
                },
                onServerError = { parsedError ->
                    Log.w("GroupsViewModel", "addGroup: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT).show()
                },
                onOtherError = {
                    Log.w("GroupsViewModel", "addGroup: $it")
                },
                finally = {
                    openAddModal.value = false
                    isAddingGroup.value = false
                }
            )
        }
    }

    private fun onGroupAdded(group: GroupData) {
        val currentGroups = groups.value.toMutableList()
        currentGroups.add(group)
        groups.value = currentGroups
    }

    fun deleteGroup(group: GroupData) {
        groups.value = groups.value.filter { it.id != group.id }

        viewModelScope.launch {
            val result = GroupRequester.deleteGroup(group.id)

            result.handle(
                onSuccess = {
                    Log.d("GroupsViewModel", "deleteGroup: $it")
                },
                onServerError = { parsedError ->
                    Log.w("GroupsViewModel", "deleteGroup: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT).show()
                },
                onOtherError = {
                    Log.w("GroupsViewModel", "deleteGroup: $it")
                }
            )
        }
    }

    fun joinGroup(token: String, navHostController: NavHostController) {
        if (!isJwt(token)) {
            scanError = App.getContext().getString(R.string.invalid_token)
            return
        }

        viewModelScope.launch {
            val result = MiscRequester.acceptInvitation(token)

            result.handle(
                onSuccess = { response ->
                    Log.d("GroupsViewModel", "joinGroup: $response")
                    updateGroups()
                    navHostController.navigate(Route.Groups)
                },
                onServerError = { parsedError ->
                    Log.w("GroupsViewModel", "joinGroup: $parsedError")

                    parsedError?.get("error")?.let { value ->
                        val error = value as? String
                        if (error != null) {
                            Log.d("GroupsViewModel", "Body contains error - $error")
                            scanError = error
                        }
                    }

                    parsedError?.get("message")?.let { value ->
                        val error = value as? String
                        if (error != null) {
                            Log.d("GroupsViewModel", "Body contains message - $error")
                            scanError = error
                        }
                    }
                },
                onOtherError = {
                    Log.w("GroupsViewModel", "joinGroup: $it")
                }
            )
        }
    }

    private fun getAndProcessGroups(
        onGroupsGet: (List<GroupData>) -> Unit
    ) {
        isLoading.value = true

        viewModelScope.launch {
            val result = GroupRequester.getGroups()

            result.handle(
                onSuccess = { response ->
                    Log.d("GroupsViewModel", "getAndProcessGroups: $response")
                    groups.value = response.data
                    onGroupsGet(response.data)
                },
                onServerError = { parsedError ->
                    Log.w("GroupsViewModel", "getAndProcessGroups: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT).show()
                },
                onOtherError = {
                    Log.w("GroupsViewModel", "getAndProcessGroups: $it")
                },
                finally = {
                    isLoading.value = false
                }
            )
        }
    }

    fun leaveGroup(group: GroupData) {
        groups.value = groups.value.filter { it.id != group.id }

        viewModelScope.launch {
            val result = GroupRequester.leaveGroup(group.id)

            result.handle(
                onSuccess = {
                    Log.d("GroupsViewModel", "leaveGroup: $it")
                },
                onServerError = { parsedError ->
                    Log.w("GroupsViewModel", "leaveGroup: $parsedError")
                    Toast.makeText(App.getContext(), parsedError.toString(), Toast.LENGTH_SHORT).show()
                },
                onOtherError = {
                    Log.w("GroupsViewModel", "leaveGroup: $it")
                }
            )
        }
    }
}
