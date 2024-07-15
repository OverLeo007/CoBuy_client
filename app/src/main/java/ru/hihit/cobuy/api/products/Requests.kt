package ru.hihit.cobuy.api.products

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.models.ProductStatusSerializer

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String
)

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String?,
    @Serializable(with = ProductStatusSerializer::class)
    val status: ProductStatus
)