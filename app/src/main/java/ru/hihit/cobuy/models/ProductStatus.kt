package ru.hihit.cobuy.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder



@Serializable
enum class ProductStatus(val value: Int) {
    NONE(0),
    BOUGHT(1),
    PLANNED(2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: NONE
    }

    fun toInt() = value
}



@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ProductStatus::class)
object ProductStatusSerializer : KSerializer<ProductStatus> {
    override fun serialize(encoder: Encoder, value: ProductStatus) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): ProductStatus {
        val intValue = decoder.decodeNullableSerializableValue(Int.serializer().nullable)
        return if (intValue != null) {
            ProductStatus.fromInt(intValue)
        } else {
            ProductStatus.fromInt(0)
        }
    }
}
