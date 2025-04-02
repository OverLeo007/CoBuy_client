@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.groups

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.models.GroupData

@Serializable
data class CreateGroupResponse(
    val data: GroupData
)

@Serializable
data class GetGroupsResponse(
    val data: List<GroupData>
)

@Serializable
data class GetUpdateGroupResponse(
    val data: GroupData
)