package ru.hihit.cobuy.api.models

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.models.ProductStatusSerializer
import ru.hihit.cobuy.serializers.UriSerializer

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
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
