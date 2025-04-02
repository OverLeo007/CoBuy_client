package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.api.lists.CreateListResponse
import ru.hihit.cobuy.api.lists.GetListsResponse
import ru.hihit.cobuy.api.lists.GetUpdateListResponse
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object ListRequester {
    fun createList(
        request: CreateListRequest,
        callback: (CreateListResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.lists.createList(request) },
            callback = callback,
            errorMessage = "Create List Error",
            onError = onError
        )
    }

    fun getLists(
        groupId: Int?,
        callback: (GetListsResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.lists.getLists(groupId) },
            callback = callback,
            errorMessage = "Get Lists Error",
            onError = onError
        )
    }

    fun getListById(
        id: Int,
        callback: (GetUpdateListResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.lists.getListById(id) },
            callback = callback,
            errorMessage = "Get List By Id Error",
            onError = onError
        )
    }

    fun updateList(
        id: Int,
        request: UpdateListRequest,
        callback: (GetUpdateListResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.lists.updateList(id, request) },
            callback = callback,
            errorMessage = "Update List Error",
            onError = onError
        )
    }

    fun deleteList(id: Int, callback: (Boolean) -> Unit, onError: (Int, ResponseBody?) -> Unit) {
        launchRequest(
            request = { Api.lists.deleteList(id) },
            callback = { response -> callback(response != null) },
            errorMessage = "Delete List Error",
            onError = onError
        )
    }
}