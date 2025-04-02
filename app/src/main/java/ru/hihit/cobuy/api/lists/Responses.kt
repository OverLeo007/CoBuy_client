@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.lists

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.models.ListData

@Serializable
data class CreateListResponse(
    val data: ListData
)

@Serializable
data class GetListsResponse(
    val data: List<ListData>
)

@Serializable
data class GetUpdateListResponse(
    val data: ListData
)