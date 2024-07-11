package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

class AuthViewModel() : ViewModel() {
    fun register(login: String, email: String, password: String): String {
        Log.d("AuthViewModel", "register: $login, $email, $password")
        return "OK"
    }

    fun login(email: String, password: String): String {
        Log.d("AuthViewModel", "login: $email, $password")
        return "OK"
    }

}