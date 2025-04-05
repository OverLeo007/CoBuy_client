package ru.hihit.cobuy.api.requesters

import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.CreateProductResponse
import ru.hihit.cobuy.api.products.GetProductsResponse
import ru.hihit.cobuy.api.products.GetUpdateProductResponse
import ru.hihit.cobuy.api.products.UpdateProductRequest
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object ProductRequester {
    suspend fun createProduct(
        listId: Int,
        request: CreateProductRequest
    ): Result<CreateProductResponse> {
        return launchRequest(
            request = { Api.products.createProduct(listId, request) },
            errorMessage = "Create Product Error"
        )
    }

    suspend fun getProducts(
        listId: Int
    ): Result<GetProductsResponse> {
        return launchRequest(
            request = { Api.products.getProducts(listId) },
            errorMessage = "Get Products Error"
        )
    }

    suspend fun getProductById(
        listId: Int,
        id: Int
    ): Result<GetUpdateProductResponse> {
        return launchRequest(
            request = { Api.products.getProductById(listId, id) },
            errorMessage = "Get Product By Id Error"
        )
    }

    suspend fun updateProduct(
        listId: Int,
        id: Int,
        request: UpdateProductRequest
    ): Result<GetUpdateProductResponse> {
        return launchRequest(
            request = { Api.products.updateProduct(listId, id, request) },
            errorMessage = "Update Product Error"
        )
    }

    suspend fun deleteProduct(
        listId: Int,
        id: Int
    ): Result<Unit> {
        return launchRequest(
            request = { Api.products.deleteProduct(listId, id) },
            errorMessage = "Delete Product Error"
        )
    }
}