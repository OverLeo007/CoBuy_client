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
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.GroupRequester
import ru.hihit.cobuy.api.MiscRequester
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
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

    init {
        getGroups()
    }


    fun onWsEditGroup(editedGroup: GroupData) {
        viewModelScope.launch {
            val currentGroups = groups.value.toMutableList()
            val index = currentGroups.indexOfFirst { it.id == editedGroup.id }
            if (index != -1) {
                currentGroups[index] = editedGroup
                groups.value = currentGroups
            }
        }
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
        val request = CreateUpdateGroupRequest(name = group.name)
        GroupRequester.createGroup(
            request = request,
            callback = { response ->
                Log.d("GroupsViewModel", "addGroup: $response")
                response?.data?.let { onGroupAdded(it) }
                openAddModal.value = false
            },
            onError = { code, body ->
                Log.d("GroupsViewModel", "Error: $code body: $body")
            }
        )
    }

    private fun onGroupAdded(group: GroupData) {
        val currentGroups = groups.value.toMutableList()
        currentGroups.add(group)
        groups.value = currentGroups
    }

    fun deleteGroup(group: GroupData) {
        Log.d("GroupsViewModel", "deleteGroup: $group")
    }

    fun joinGroup(token: String, navHostController: NavHostController): Unit {
        if (!isJwt(token)) {
            scanError = App.getContext().getString(R.string.invalid_token)
            return
        }
        MiscRequester.acceptInvitation(
            token = token,
            callback = { response ->
                Log.d("GroupsViewModel", "joinGroup: $response")
                updateGroups()
                navHostController.navigate(Route.Groups)
            },
            onError = { code, body ->
                Log.d("GroupsViewModel", "Error: $code body: $body")
                body?.let {
                    parseJson(it.string())
                }?.let {
                    if (it.containsKey("error")) {
                        val error = it["error"] as String
                        scanError = error
                    }
                    if (it.containsKey("message")) {
                        val error = it["message"] as String
                        scanError = error
                    }
                }
            }
        )
    }

    private fun getAndProcessGroups(
        onGroupsGet: (List<GroupData>) -> Unit
    ) {
        isLoading.value = true
        GroupRequester.getGroups(
            callback = { response ->
                response?.data?.let { newGroups ->
                    groups.value = newGroups
                    onGroupsGet(newGroups)
                }
                isLoading.value = false
            },
            onError = { code, body ->
                Log.d("GroupsViewModel", "Error: $code body: $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
        )
    }
}
