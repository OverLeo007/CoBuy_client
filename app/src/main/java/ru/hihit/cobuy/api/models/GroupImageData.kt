package ru.hihit.cobuy.api.models

import android.net.Uri
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.serializers.UriSerializer

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
data class GroupImageData(
    @Serializable(with = UriSerializer::class)
    val avaUrl: Uri?
)
