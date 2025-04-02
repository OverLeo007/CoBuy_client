package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.CreateProductResponse
import ru.hihit.cobuy.api.products.GetProductsResponse
import ru.hihit.cobuy.api.products.GetUpdateProductResponse
import ru.hihit.cobuy.api.products.UpdateProductRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

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