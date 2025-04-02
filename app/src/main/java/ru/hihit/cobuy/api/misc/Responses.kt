@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.misc

import kotlinx.serialization.Serializable

@Serializable
data class InviteTokenResponse(
    val token: String
)

@Serializable
data class InvitationStatusResponse(
    val message: String
)


