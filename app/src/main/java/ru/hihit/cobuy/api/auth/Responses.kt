@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ru.hihit.cobuy.api.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.hihit.cobuy.api.models.UserData

@Serializable
data class RegisterResponse(
    val data: UserData
)

@Serializable
data class LoginResponseData(
    @SerialName(value = "user")
    val data: UserData,
    @SerialName(value = "accessToken")
    val token: String,
    @SerialName(value = "tokenType")
    val tokenType: String
)

@Serializable
data class CheckLoginResponse(
    @SerialName("data")
    val userData: UserData
)

@Serializable
data class LoginResponse(
    @SerialName(value = "data")
    val loginResponseData: LoginResponseData
)