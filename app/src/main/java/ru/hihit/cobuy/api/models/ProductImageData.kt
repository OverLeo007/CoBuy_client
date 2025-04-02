package ru.hihit.cobuy.api.models

import android.net.Uri
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.serializers.UriSerializer

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
data class ProductImageData(
    @Serializable(with = UriSerializer::class)
    val productImgUrl: Uri?
)
