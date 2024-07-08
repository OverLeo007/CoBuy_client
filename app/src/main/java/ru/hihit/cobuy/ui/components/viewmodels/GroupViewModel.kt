package ru.hihit.cobuy.ui.components.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.models.User

class GroupViewModel() : ViewModel() {
    var groupId: Int = 0
    var groupIconUrl: String = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1"

    fun onImageSelected(imageUri: Uri) {
        Log.d("GroupViewModel", "onImageSelected: $imageUri")
    }

    fun onUserRemoved(user: User) {
        Log.d("GroupViewModel", "onUserDeleted: $user")
    }

    fun onNameChanged(name: String) {
        Log.d("GroupViewModel", "onNameChanged: $name")
    }

}