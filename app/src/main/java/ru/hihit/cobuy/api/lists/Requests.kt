package ru.hihit.cobuy.api.lists

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(
    val name: String,
    val groupId: Int
)

@Serializable
data class UpdateListRequest(
    val name: String
)