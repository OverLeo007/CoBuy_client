package ru.hihit.cobuy.api.models

import kotlinx.serialization.Serializable
import ru.hihit.cobuy.serializers.IntAsBooleanSerializer
import ru.hihit.cobuy.serializers.ListDataSerializer

@Serializable(with = ListDataSerializer::class)
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
data class ListData(
    val id: Int = 0,
    var name: String = "",
    val groupId: Int = 0,
    var productsCount: Int = 0,
    var checkedProductsCount: Int = 0,
    @Serializable(with = IntAsBooleanSerializer::class)
    var hidden: Boolean = false,
    @Transient
    val isCompleted: Boolean = false
)
