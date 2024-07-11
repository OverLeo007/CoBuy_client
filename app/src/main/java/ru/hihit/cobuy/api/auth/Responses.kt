package ru.hihit.cobuy.api.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.UserData

@Serializable
data class RegisterResponse(
    val data: UserData
)

@Serializable
data class LoginResponse(
    val data: UserData,
    @SerialName(value = "access_token")
    val token: String,
    @SerialName(value = "token_type")
    val tokenType: String
)