package ru.hihit.cobuy.api.requesters

import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.auth.CheckLoginResponse
import ru.hihit.cobuy.api.auth.LoginRequest
import ru.hihit.cobuy.api.auth.LoginResponse
import ru.hihit.cobuy.api.auth.RegisterRequest
import ru.hihit.cobuy.api.auth.RegisterResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object AuthRequester {

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return launchRequest(
            request = { Api.auth.login(request) },
            errorMessage = "Ошибка при входе"
        )
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return launchRequest(
            request = { Api.auth.register(request) },
            errorMessage = "Ошибка при регистрации"
        )
    }

    suspend fun checkLogin(): Result<CheckLoginResponse> {
        return launchRequest(
            request = { Api.auth.checkLogin() },
            errorMessage = "Ошибка проверки логина"
        )
    }
}
