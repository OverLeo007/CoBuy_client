package ru.hihit.cobuy.pusher.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.hihit.cobuy.api.models.ListData
import ru.hihit.cobuy.models.EventType

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
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
