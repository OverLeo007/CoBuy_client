package ru.hihit.cobuy.ui.components.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel() : ViewModel() {

    var avatarUrl: String = "https://sun125-1.userapi.com/s/v1/ig2/bsKSb_3JolWpjJ5mez44ii5lzdgwXsl4fOtV685zcybEWn7h1TUhPaGwOvyCz-tZveB4XzU1tNT_SDnzxZzrAC07.jpg?quality=95&crop=212,824,1052,1052&as=32x32,48x48,72x72,108x108,160x160,240x240,360x360,480x480,540x540,640x640,720x720&ava=1&u=5_m3lbtS8y6Kw-IhyX0ct7f_g-PoWL8G1p9eSoFoBHM&cs=200x200"

    var imageUri by  mutableStateOf<Uri?>(Uri.parse(avatarUrl))

    fun onAvatarSelected(imageUri: Uri) {
        Log.d("SettingsViewModel", "onAvatarSelected: $imageUri")
        this.imageUri = imageUri
    }

}