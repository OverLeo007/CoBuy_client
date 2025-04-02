
package ru.hihit.cobuy.pusher.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.hihit.cobuy.api.models.GroupData
import ru.hihit.cobuy.models.EventType

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
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