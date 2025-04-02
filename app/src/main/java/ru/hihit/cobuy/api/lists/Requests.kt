@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.lists

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.serializers.IntAsBooleanSerializer

@Serializable
data class CreateListRequest(
    val name: String,
    val groupId: Int
)

@Serializable
data class UpdateListRequest(
    var name: String? = null,
    @Serializable(with = IntAsBooleanSerializer::class)
    var hidden: Boolean? = null
)