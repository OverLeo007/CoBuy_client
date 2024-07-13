package ru.hihit.cobuy.ui.components.viewmodels

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.GroupRequester
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.api.ListRequester
import ru.hihit.cobuy.models.ProductList
import ru.hihit.cobuy.models.User

class GroupViewModel(groupId: Int) : ViewModel() {



    var groupId: Int = 0
    var groupIconUrl: String = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1"
    var isGroupLoading = mutableStateOf(true)
    var isListsLoading = mutableStateOf(true)

    lateinit var group: MutableStateFlow<GroupData>
    var lists: MutableStateFlow<List<ListData>> = MutableStateFlow(emptyList())

    init {
        this.groupId = groupId
        getGroup()
    }


    private fun getGroup() {
        isGroupLoading.value = true
        GroupRequester.getGroupById(
            groupId,
            callback = { response ->
                response?.data?.let {
                    group.value = it
                }
                isGroupLoading.value = false
            },
            onError = {code, body ->
                Log.e("GroupViewModel", "getGroup: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                isGroupLoading.value = false
            }
        )
    }

    private fun getLists() {
        isListsLoading.value = true
        ListRequester.getLists(
            groupId,
            callback = { response ->
                response?.data?.let {
                    lists.value = it
                }
                isListsLoading.value = false
            },
            onError = {code, body ->
                Log.e("GroupViewModel", "getLists: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                isListsLoading.value = false
            }
        )
    }

    fun onImageSelected(imageUri: Uri) {
        Log.d("GroupViewModel", "onImageSelected: $imageUri")
    }

    fun onUserRemoved(user: User) {
        Log.d("GroupViewModel", "onUserDeleted: $user")
    }

    fun onNameChanged(name: String) {
        Log.d("GroupViewModel", "onNameChanged: $name")
    }

    fun onAddList(productList: ProductList) {
        Log.d("GroupViewModel", "onAddList: $productList")
    }

}