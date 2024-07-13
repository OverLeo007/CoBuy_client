package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.AuthRequester
import ru.hihit.cobuy.api.auth.LoginRequest
import ru.hihit.cobuy.api.auth.LoginResponse
import ru.hihit.cobuy.api.auth.RegisterRequest
import ru.hihit.cobuy.utils.parseJson
import ru.hihit.cobuy.utils.saveManyToPreferences

class AuthViewModel() : ViewModel() {

    var apiResponse: String by mutableStateOf("")
    var isLoading: Boolean by mutableStateOf(false)

    var loginError: String by mutableStateOf("")
    var emailError: String by mutableStateOf("")
    var passwordError: String by mutableStateOf("")

    fun register(login: String, email: String, password: String) {
        isLoading = true
        AuthRequester.register(
            request = RegisterRequest(login, email, password),
            callback = { response ->
                apiResponse = "OK"
                Log.d("AuthViewModel", "Response: $response")
                isLoading = false
                resetErrors()
            },
            onError = { code, body ->
                apiResponse = "FAIL"
                val parsedError: Map<String, Any>? = body?.let {
                    parseJson(it.string())
                }
                Log.d(
                    "AuthViewModel",
                    "Error: $code body: $parsedError"
                )
                setUpErrors(parsedError)
                isLoading = false
            }
        )
    }

    fun login(email: String, password: String) {
        isLoading = true
        AuthRequester.login(
            request = LoginRequest(email, password),
            callback = { response ->
                apiResponse = "OK"
                Log.d("AuthViewModel", "Response: $response")
                isLoading = false
                resetErrors()
                setUpUser(response!!)
            },
            onError = { code, body ->
                apiResponse = "FAIL"
                val parsedError: Map<String, Any>? = body?.let {
                    parseJson(it.string())
                }
                Log.d(
                    "AuthViewModel",
                    "Error: $code body: $parsedError"
                )
                setUpErrors(parsedError)
                isLoading = false
            }
        )
    }

    private fun setUpUser(loginResponse: LoginResponse) {
        saveManyToPreferences(App.getContext(),
            mapOf(
                "auth_token" to loginResponse.token,
                "user_id" to loginResponse.data.id,
                "user_name" to loginResponse.data.name,
                "user_email" to loginResponse.data.email
            )
        )
    }

    private fun setUpErrors(it: Map<String, Any>?) {
        it?.let {
            if (it.containsKey("errors")) {
                val errors = it["errors"] as Map<String, List<String>>
                if (errors.containsKey("name")) {
                    loginError = errors["name"]?.get(0) ?: ""
                }
                if (errors.containsKey("email")) {
                    emailError = errors["email"]?.get(0) ?: ""
                }
                if (errors.containsKey("password")) {
                    passwordError = errors["password"]?.get(0) ?: ""
                }
            }

        }
    }

    fun resetErrors() {
        emailError = ""
        passwordError = ""
        loginError = ""
    }

}