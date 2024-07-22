package ru.hihit.cobuy.ui.components.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.UserData
import ru.hihit.cobuy.models.User
import ru.hihit.cobuy.utils.clearPreferences
import ru.hihit.cobuy.utils.getUserDataFromPreferences

class SettingsViewModel() : ViewModel() {


    val user: UserData = App.getContext().getUserDataFromPreferences()


    fun onLogout() {
        App.getContext().clearPreferences()
    }

}