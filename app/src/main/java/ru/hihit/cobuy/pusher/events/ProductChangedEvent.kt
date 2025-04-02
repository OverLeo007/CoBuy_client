package ru.hihit.cobuy.pusher.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.hihit.cobuy.api.models.ProductData
import ru.hihit.cobuy.models.EventType

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
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
