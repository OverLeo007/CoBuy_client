package ru.hihit.cobuy.api.groups

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.GroupData

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