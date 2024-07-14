package ru.hihit.cobuy.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupData(
    val id: Int,
    var name: String,
    var avaUrl: String?,
    var inviteLink: String?,
    @SerialName(value = "owner")
    val ownerId: Int,
    var membersCount: Int,
    var listsCount: Int,
    var members: List<UserData>
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
    val groupId: Int,
    val productsCount: Int,
    val checkedProductsCount: Int
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