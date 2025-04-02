package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.auth.CheckLoginResponse
import ru.hihit.cobuy.api.auth.LoginRequest
import ru.hihit.cobuy.api.auth.LoginResponse
import ru.hihit.cobuy.api.auth.RegisterRequest
import ru.hihit.cobuy.api.auth.RegisterResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object AuthRequester {
    fun login(
        request: LoginRequest,
        callback: (LoginResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.auth.login(request) },
            callback = callback,
            errorMessage = "Login Error",
            onError = onError
        )
    }

    fun register(
        request: RegisterRequest,
        callback: (RegisterResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.auth.register(request) },
            callback = callback,
            errorMessage = "Register Error",
            onError = onError
        )
    }

    fun checkLogin(callback: (CheckLoginResponse?) -> Unit, onError: (Int, ResponseBody?) -> Unit) {
        launchRequest(
            request = { Api.auth.checkLogin() },
            callback = callback,
            errorMessage = "Check Login Error",
            onError = onError
        )
    }
}