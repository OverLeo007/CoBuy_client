package ru.hihit.cobuy.api.requesters

import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.groups.CreateGroupResponse
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.groups.GetGroupsResponse
import ru.hihit.cobuy.api.groups.GetUpdateGroupResponse
import ru.hihit.cobuy.api.groups.KickUserRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object GroupRequester {

    suspend fun createGroup(
        request: CreateUpdateGroupRequest
    ): Result<CreateGroupResponse> {
        return launchRequest(
            request = { Api.groups.createGroup(request) },
            errorMessage = "Create Group Error"
        )
    }

    suspend fun getGroups(): Result<GetGroupsResponse> {
        return launchRequest(
            request = { Api.groups.getGroups() },
            errorMessage = "Get Groups Error"
        )
    }

    suspend fun getGroupById(id: Int): Result<GetUpdateGroupResponse> {
        return launchRequest(
            request = { Api.groups.getGroupById(id) },
            errorMessage = "Get Group By Id Error"
        )
    }

    suspend fun updateGroup(
        id: Int,
        request: CreateUpdateGroupRequest
    ): Result<GetUpdateGroupResponse> {
        return launchRequest(
            request = { Api.groups.updateGroup(id, request) },
            errorMessage = "Update Group Error"
        )
    }

    suspend fun deleteGroup(id: Int): Result<Unit> {
        return launchRequest(
            request = { Api.groups.deleteGroup(id) },
            errorMessage = "Delete Group Error"
        )
    }

    suspend fun leaveGroup(id: Int): Result<Unit> {
        return launchRequest(
            request = { Api.groups.leaveGroup(id) },
            errorMessage = "Leave Group Error"
        )
    }

    suspend fun kickFromGroup(
        request: KickUserRequest
    ): Result<Unit> {
        return launchRequest(
            request = { Api.groups.kickUser(request) },
            errorMessage = "Kick User Error"
        )
    }
}