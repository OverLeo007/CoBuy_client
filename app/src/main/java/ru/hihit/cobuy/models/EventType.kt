package ru.hihit.cobuy.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EventTypeSerializer::class)
enum class EventType {
    Create,
    Update,
    Delete
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = EventType::class)
object EventTypeSerializer : KSerializer<EventType> {
    override fun serialize(encoder: Encoder, value: EventType) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): EventType {
        return when (val value = decoder.decodeString()) {
            "Create" -> EventType.Create
            "Update" -> EventType.Update
            "Delete" -> EventType.Delete
            else -> throw IllegalArgumentException("Unknown EventType: $value")
        }
    }
}