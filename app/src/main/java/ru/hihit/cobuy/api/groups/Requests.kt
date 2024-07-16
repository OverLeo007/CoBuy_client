package ru.hihit.cobuy.api.groups

import kotlinx.serialization.Serializable

@Serializable
data class CreateUpdateGroupRequest(
    val name: String,
)

@Serializable
data class KickUserRequest(
    val groupId: Int,
    val userId: Int
)