package ru.hihit.cobuy.api

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.hihit.cobuy.models.EventType
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.models.ProductStatusSerializer
import ru.hihit.cobuy.utils.UriSerializer

@Serializable
data class GroupData(
    val id: Int = 0,
    var name: String = "",
    @Serializable(with = UriSerializer::class)
    var avaUrl: Uri?,
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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserData) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}
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
    val listId: Int = 0,
    @Serializable(with = UriSerializer::class)
    var productImgUrl: Uri? = null,
    val price: Int? = 0,
    @SerialName(value = "count")
    val quantity: Int? = 0,
    @SerialName(value = "buyer")
    val buyer: UserData? = null,

)

@Serializable
data class GroupImageData(
    @Serializable(with = UriSerializer::class)
    val avaUrl: Uri?
)

@Serializable
data class ProductImageData(
    @Serializable(with = UriSerializer::class)
    val productImgUrl: Uri?
)


@Serializable
data class GroupChangedEvent(
    val type: EventType,
    val data: GroupData
) {
    companion object {
        fun fromJson(json: String): GroupChangedEvent {
            return Json.decodeFromString(json)
        }
    }
}

@Serializable
data class ListChangedEvent(
    val type: EventType,
    val data: ListData
) {
    companion object {
        fun fromJson(json: String): ListChangedEvent {
            return Json.decodeFromString(json)
        }
    }
}

@Serializable
data class ProductChangedEvent(
    val type: EventType,
    val data: ProductData
) {
    companion object {
        fun fromJson(json: String): ProductChangedEvent {
            return Json.decodeFromString(json)
        }
    }
}