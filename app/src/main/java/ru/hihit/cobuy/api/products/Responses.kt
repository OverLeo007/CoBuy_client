package ru.hihit.cobuy.api.products

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.ProductData

@Serializable
data class CreateProductResponse(
    val data: ProductData
)

@Serializable
data class GetProductsResponse(
    val data: List<ProductData>
)

@Serializable
data class GetUpdateProductResponse(
    val data: ProductData
)