package ru.hihit.cobuy.api.models

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.serializers.UriSerializer

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
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

