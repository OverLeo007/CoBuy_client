@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)


package ru.hihit.cobuy.api.products

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.models.ProductStatusSerializer

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Int?,
    val count: Int?
)

@Serializable
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    @Serializable(with = ProductStatusSerializer::class)
    val status: ProductStatus?,
    val price: Int? = null,
    val buyerId: Int? = null,
    val count: Int? = null
)