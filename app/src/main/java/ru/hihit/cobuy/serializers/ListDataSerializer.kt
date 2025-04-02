package ru.hihit.cobuy.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import ru.hihit.cobuy.api.models.ListData

object ListDataSerializer : KSerializer<ListData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ListData") {
        element<Int>("id")
        element<String>("name")
        element<Int>("groupId")
        element<Int>("productsCount")
        element<Int>("checkedProductsCount")
        element<Boolean>("hidden")
    }

    override fun serialize(encoder: Encoder, value: ListData) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.id)
            encodeStringElement(descriptor, 1, value.name)
            encodeIntElement(descriptor, 2, value.groupId)
            encodeIntElement(descriptor, 3, value.productsCount)
            encodeIntElement(descriptor, 4, value.checkedProductsCount)
            encodeSerializableElement(descriptor, 5, IntAsBooleanSerializer, value.hidden)
        }
    }

    override fun deserialize(decoder: Decoder): ListData {
        return decoder.decodeStructure(descriptor) {
            var id = 0
            var name = ""
            var groupId = 0
            var productsCount = 0
            var checkedProductsCount = 0
            var hidden = false

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> id = decodeIntElement(descriptor, index)
                    1 -> name = decodeStringElement(descriptor, index)
                    2 -> groupId = decodeIntElement(descriptor, index)
                    3 -> productsCount = decodeIntElement(descriptor, index)
                    4 -> checkedProductsCount = decodeIntElement(descriptor, index)
                    5 -> hidden = decodeSerializableElement(descriptor, index, IntAsBooleanSerializer)
                    else -> throw SerializationException("Unknown index $index")
                }
            }

            ListData(
                id = id,
                name = name,
                groupId = groupId,
                productsCount = productsCount,
                checkedProductsCount = checkedProductsCount,
                hidden = hidden,
                isCompleted = productsCount > 0 && productsCount == checkedProductsCount
            )
        }
    }
}