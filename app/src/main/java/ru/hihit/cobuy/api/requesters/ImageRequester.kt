package ru.hihit.cobuy.api.requesters

import okhttp3.MultipartBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.images.GetGroupImageResponse
import ru.hihit.cobuy.api.images.GetProductImageResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object ImageRequester {
    suspend fun uploadGroupImage(
        groupId: Int,
        image: MultipartBody.Part,
    ): Result<GetGroupImageResponse> {
        return launchRequest(
            request = { Api.images.setGroupImage(groupId, image) },
            errorMessage = "Upload Group Image Error",
        )
    }

    suspend fun getGroupImage(
        groupId: Int,
    ): Result<GetGroupImageResponse> {
        return launchRequest(
            request = { Api.images.getGroupImage(groupId) },
            errorMessage = "Get Group Image Error",
        )
    }

    suspend fun uploadProductImage(
        listId: Int,
        productId: Int,
        image: MultipartBody.Part,
    ): Result<GetProductImageResponse> {
        return launchRequest(
            request = { Api.images.setProductImage(listId, productId, image) },
            errorMessage = "Upload Product Image Error",
        )
    }

    suspend fun getProductImage(
        listId: Int,
        productId: Int,
    ): Result<GetProductImageResponse> {
        return launchRequest(
            request = { Api.images.getProductImage(listId, productId) },
            errorMessage = "Get Product Image Error",
        )
    }

}