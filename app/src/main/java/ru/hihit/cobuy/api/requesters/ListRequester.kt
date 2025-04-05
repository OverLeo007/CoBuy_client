package ru.hihit.cobuy.api.requesters

import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.api.lists.CreateListResponse
import ru.hihit.cobuy.api.lists.GetListsResponse
import ru.hihit.cobuy.api.lists.GetUpdateListResponse
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest


object ListRequester {
    suspend fun createList(
        request: CreateListRequest,
    ): Result<CreateListResponse> {
        return launchRequest(
            request = { Api.lists.createList(request) },
            errorMessage = "Create List Error",
        )
    }

    suspend fun getLists(
        groupId: Int?,
    ): Result<GetListsResponse> {
        return launchRequest(
            request = { Api.lists.getLists(groupId) },
            errorMessage = "Get Lists Error",
        )
    }

    suspend fun getListById(
        id: Int,
    ): Result<GetUpdateListResponse> {
        return launchRequest(
            request = { Api.lists.getListById(id) },
            errorMessage = "Get List By Id Error",
        )
    }

    suspend fun updateList(
        id: Int,
        request: UpdateListRequest,
    ): Result<GetUpdateListResponse> {
        return launchRequest(
            request = { Api.lists.updateList(id, request) },
            errorMessage = "Update List Error",
        )
    }

    suspend fun deleteList(
        id: Int,
    ): Result<Unit> {
        return launchRequest(
            request = { Api.lists.deleteList(id) },
            errorMessage = "Delete List Error",
        )
    }
}