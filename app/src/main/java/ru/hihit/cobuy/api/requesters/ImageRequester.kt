package ru.hihit.cobuy.api.requesters

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.images.GetGroupImageResponse
import ru.hihit.cobuy.api.images.GetProductImageResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

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