package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.auth.LoginRequest
import ru.hihit.cobuy.api.auth.LoginResponse
import ru.hihit.cobuy.api.auth.RegisterRequest
import ru.hihit.cobuy.api.requesters.AuthRequester
import ru.hihit.cobuy.api.requesters.RequestLauncher
import ru.hihit.cobuy.api.requesters.handle
import ru.hihit.cobuy.utils.parseJson
import ru.hihit.cobuy.utils.saveToPreferences
import ru.hihit.cobuy.utils.saveUserDataToPreferences

class AuthViewModel : ViewModel() {

    var apiResponse: String by mutableStateOf("")
    var isLoading: Boolean by mutableStateOf(false)

    var loginError: String by mutableStateOf("")
    var emailError: String by mutableStateOf("")
    var passwordError: String by mutableStateOf("")

    fun register(login: String, email: String, password: String) {
        isLoading = true

        viewModelScope.launch {
            val result = AuthRequester.register(RegisterRequest(login, email, password))

            result.handle(
                onSuccess = { response ->
                    apiResponse = "OK"
                    Log.d("AuthViewModel", "Response: $response")
                    resetErrors()
                },
                onServerError = { parsedError ->
                    apiResponse = "FAIL"
                    setUpErrors(parsedError)
                },
                onOtherError = {
                    apiResponse = "FAIL"
                },
                finally = {
                    isLoading = false
                }
            )
        }
    }

    fun login(email: String, password: String) {
        isLoading = true

        viewModelScope.launch {
            val result = AuthRequester.login(LoginRequest(email, password))

            result.handle(
                onSuccess = { response ->
                    apiResponse = "OK"
                    resetErrors()
                    setUpUser(response)
                },
                onServerError = { parsedError ->
                    apiResponse = "FAIL"
                    setUpErrors(parsedError)
                },
                onOtherError = {
                    apiResponse = "FAIL"
                },
                finally = {
                    isLoading = false
                }
            )
        }
    }




    private fun setUpUser(loginResponse: LoginResponse) {
        val context = App.getContext()
        context.saveToPreferences("auth_token", loginResponse.token)
        context.saveUserDataToPreferences(loginResponse.data)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setUpErrors(rawError: Any?) {
        resetErrors()

        when (rawError) {
            is Map<*, *> -> {
                val map = rawError as? Map<String, Any>
                if (map?.containsKey("errors") == true) {
                    val errors = map["errors"] as? Map<String, List<String>> ?: return
                    loginError = errors["name"]?.firstOrNull() ?: ""
                    emailError = errors["email"]?.firstOrNull() ?: ""
                    passwordError = errors["password"]?.firstOrNull() ?: ""
                }
            }
            is String -> {
                // Если это просто строка — просто показываем общее сообщение
                loginError = rawError
            }
            else -> {
                loginError = "Произошла неизвестная ошибка"
            }
        }
    }


    fun resetErrors() {
        emailError = ""
        passwordError = ""
        loginError = ""
    }

}