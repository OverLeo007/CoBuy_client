package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.models.Group

class GroupsViewModel() : ViewModel() {
    fun addGroup(group: Group) {
        Log.d("GroupsViewModel", "addGroup: $group")
    }

    fun deleteGroup(group: Group) {
        Log.d("GroupsViewModel", "deleteGroup: $group")
    }

}