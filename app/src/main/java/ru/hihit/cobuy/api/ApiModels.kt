package ru.hihit.cobuy.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.models.ProductStatusSerializer

@Serializable
data class GroupData(
    val id: Int = 0,
    var name: String = "",
    var avaUrl: String?,
    var inviteLink: String?,
    @SerialName(value = "owner")
    val ownerId: Int = 0,
    var membersCount: Int = 0,
    var listsCount: Int = 0,
    var members: List<UserData> = emptyList()
)

@Serializable
data class UserData(
    val id: Int = 0,
    val name: String = "",
    val email: String = ""
)

@Serializable
data class ListData(
    val id: Int = 0,
    var name: String = "",
    val groupId: Int = 0,
    var productsCount: Int = 0,
    var checkedProductsCount: Int = 0
)

@Serializable
data class ProductData(
    val id: Int = 0,
    var name: String = "",
    var description: String = "",
    @Serializable(with = ProductStatusSerializer::class)
    var status: ProductStatus = ProductStatus.NONE,
    @SerialName(value = "shoppingListId")
    val listId: Int = 0
)