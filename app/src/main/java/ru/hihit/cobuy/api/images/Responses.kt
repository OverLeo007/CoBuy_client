@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.images

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.models.GroupImageData
import ru.hihit.cobuy.api.models.ProductImageData

@Serializable
data class GetGroupImageResponse(
    val data: GroupImageData
)

@Serializable
data class GetProductImageResponse(
    val data: ProductImageData
)