package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.groups.CreateGroupResponse
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.groups.GetGroupsResponse
import ru.hihit.cobuy.api.groups.GetUpdateGroupResponse
import ru.hihit.cobuy.api.groups.KickUserRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object GroupRequester {
    fun createGroup(
        request: CreateUpdateGroupRequest,
        callback: (CreateGroupResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.groups.createGroup(request) },
            callback = callback,
            errorMessage = "Create Group Error",
            onError = onError
        )
    }

    fun getGroups(callback: (GetGroupsResponse?) -> Unit, onError: (Int, ResponseBody?) -> Unit) {
        launchRequest(
            request = { Api.groups.getGroups() },
            callback = callback,
            errorMessage = "Get Groups Error",
            onError = onError
        )
    }

    fun getGroupById(
        id: Int,
        callback: (GetUpdateGroupResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.groups.getGroupById(id) },
            callback = callback,
            errorMessage = "Get Group By Id Error",
            onError = onError
        )
    }

    fun updateGroup(
        id: Int,
        request: CreateUpdateGroupRequest,
        callback: (GetUpdateGroupResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.groups.updateGroup(id, request) },
            callback = callback,
            errorMessage = "Update Group Error",
            onError = onError
        )
    }

    fun deleteGroup(id: Int, callback: (Boolean) -> Unit, onError: (Int, ResponseBody?) -> Unit) {
        launchRequest(
            request = { Api.groups.deleteGroup(id) },
            callback = { response -> callback(response != null) },
            errorMessage = "Delete Group Error",
            onError = onError
        )
    }

    fun leaveGroup(
        groupId: Int,
        callback: (Boolean) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.groups.leaveGroup(groupId) },
            callback = { response -> callback(response != null) },
            errorMessage = "Leave Group Error",
            onError = onError
        )
    }

    fun kickFromGroup(
        request: KickUserRequest,
        callback: (Boolean) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.groups.kickUser(request) },
            callback = { response -> callback(response != null) },
            errorMessage = "Kick User Error",
            onError = onError
        )
    }
}