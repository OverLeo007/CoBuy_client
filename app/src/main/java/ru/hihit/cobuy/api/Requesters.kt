package ru.hihit.cobuy.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import ru.hihit.cobuy.api.auth.CheckLoginResponse
import ru.hihit.cobuy.api.auth.LoginRequest
import ru.hihit.cobuy.api.auth.LoginResponse
import ru.hihit.cobuy.api.auth.RegisterRequest
import ru.hihit.cobuy.api.auth.RegisterResponse
import ru.hihit.cobuy.api.groups.CreateGroupResponse
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.groups.GetGroupsResponse
import ru.hihit.cobuy.api.groups.GetUpdateGroupResponse
import ru.hihit.cobuy.api.groups.KickUserRequest
import ru.hihit.cobuy.api.images.GetGroupImageResponse
import ru.hihit.cobuy.api.images.GetProductImageResponse
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.api.lists.CreateListResponse
import ru.hihit.cobuy.api.lists.GetListsResponse
import ru.hihit.cobuy.api.lists.GetUpdateListResponse
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.misc.InvitationStatusResponse
import ru.hihit.cobuy.api.misc.InviteTokenResponse
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.CreateProductResponse
import ru.hihit.cobuy.api.products.GetProductsResponse
import ru.hihit.cobuy.api.products.GetUpdateProductResponse
import ru.hihit.cobuy.api.products.UpdateProductRequest


object AuthRequester {
    fun login(
        request: LoginRequest,
        callback: (LoginResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.auth.login(request) },
            callback = callback,
            errorMessage = "Login Error",
            onError = onError
        )
    }

    fun register(
        request: RegisterRequest,
        callback: (RegisterResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.auth.register(request) },
            callback = callback,
            errorMessage = "Register Error",
            onError = onError
        )
    }

    fun checkLogin(callback: (CheckLoginResponse?) -> Unit, onError: (Int, ResponseBody?) -> Unit) {
        launchRequest(
            request = { Api.auth.checkLogin() },
            callback = callback,
            errorMessage = "Check Login Error",
            onError = onError
        )
    }
}


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


object ProductRequester {
    fun createProduct(
        listId: Int,
        request: CreateProductRequest,
        callback: (CreateProductResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.products.createProduct(listId, request) },
            callback = callback,
            errorMessage = "Create Product Error",
            onError = onError
        )
    }

    fun getProducts(
        listId: Int,
        callback: (GetProductsResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.products.getProducts(listId) },
            callback = callback,
            errorMessage = "Get Products Error",
            onError = onError
        )
    }

    fun getProductById(
        listId: Int,
        id: Int,
        callback: (GetUpdateProductResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.products.getProductById(listId, id) },
            callback = callback,
            errorMessage = "Get Product By Id Error",
            onError = onError
        )
    }

    fun updateProduct(
        listId: Int,
        id: Int,
        request: UpdateProductRequest,
        callback: (GetUpdateProductResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit

    ) {
        launchRequest(
            request = { Api.products.updateProduct(listId, id, request) },
            callback = callback,
            errorMessage = "Update Product Error",
            onError = onError
        )
    }

    fun deleteProduct(
        listId: Int,
        id: Int,
        callback: (Boolean) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.products.deleteProduct(listId, id) },
            callback = { response -> callback(response != null) },
            errorMessage = "Delete Product Error",
            onError = onError
        )
    }
}


object MiscRequester {
    fun getInviteToken(
        groupId: Int,
        callback: (InviteTokenResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.misc.getInviteToken(groupId) },
            callback = callback,
            errorMessage = "Get Invite Token Error",
            onError = onError
        )
    }

    fun acceptInvitation(
        token: String,
        callback: (InvitationStatusResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.misc.acceptInvitation(token) },
            callback = callback,
            errorMessage = "Accept Invitation Error",
            onError = onError
        )
    }

}


object ImageRequester {
    fun uploadGroupImage(
        groupId: Int,
        image: MultipartBody.Part,
        callback: (GetGroupImageResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.images.setGroupImage(groupId, image) },
            callback = callback,
            errorMessage = "Upload Group Image Error",
            onError = onError
        )
    }

    fun getGroupImage(
        groupId: Int,
        callback: (GetGroupImageResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.images.getGroupImage(groupId) },
            callback = callback,
            errorMessage = "Get Group Image Error",
            onError = onError
        )
    }

    fun uploadProductImage(
        listId: Int,
        productId: Int,
        image: MultipartBody.Part,
        callback: (GetProductImageResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.images.setProductImage(listId, productId, image) },
            callback = callback,
            errorMessage = "Upload Product Image Error",
            onError = onError
        )
    }

    fun getProductImage(
        listId: Int,
        productId: Int,
        callback: (GetProductImageResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.images.getProductImage(listId, productId) },
            callback = callback,
            errorMessage = "Get Product Image Error",
            onError = onError
        )
    }

}


private fun <T> launchRequest(
    request: suspend () -> Response<T>,
    callback: (T?) -> Unit,
    onError: (Int, ResponseBody?) -> Unit,
    errorMessage: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = request()
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                callback(response.body())
            } else {
                val body: ResponseBody? = response.errorBody()
                Log.e("Requester", "$errorMessage ${response.code()}: ${body?.string()}")
                onError(response.code(), body)
            }
        }
    }
}