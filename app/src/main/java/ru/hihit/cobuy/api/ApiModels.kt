package ru.hihit.cobuy.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupData(
    val id: Int,
    val name: String,
    val avaUrl: String?,
    val inviteLink: String?,
    @SerialName(value = "owner")
    val ownerId: Int,
    val membersCount: Int,
    val listsCount: Int,
    val members: List<UserData>
)

@Serializable
data class UserData(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class ListData(
    val id: Int,
    val name: String,
    @SerialName(value = "group_id")
    val groupId: Int,
)

@Serializable
data class ProductData(
    val id: Int,
    val name: String,
    val description: String,
    val status: Int,
    @SerialName(value = "shopping_list_id")
    val listId: Int
)