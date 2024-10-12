package ru.hihit.cobuy.api.images

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.GroupImageData
import ru.hihit.cobuy.api.ProductImageData

@Serializable
data class GetGroupImageResponse(
    val data: GroupImageData
)

@Serializable
data class GetProductImageResponse(
    val data: ProductImageData
)