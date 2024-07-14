package ru.hihit.cobuy.ui.components.viewmodels

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.hihit.cobuy.App
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.GroupData
import ru.hihit.cobuy.api.GroupRequester
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.api.ListRequester
import ru.hihit.cobuy.api.MiscRequester
import ru.hihit.cobuy.api.UserData
import ru.hihit.cobuy.api.groups.CreateUpdateGroupRequest
import ru.hihit.cobuy.api.lists.CreateListRequest
import ru.hihit.cobuy.models.ProductList
import java.io.File
import java.io.FileOutputStream

class GroupViewModel(private val groupId: Int) : ViewModel() {

    var isGroupLoading = mutableStateOf(true)
    var isRefreshing = mutableStateOf(false)

    var group: MutableStateFlow<GroupData> =
        MutableStateFlow(GroupData(0, "", "", "", 0, 0, 0, emptyList()))
    var lists: MutableStateFlow<List<ListData>> = MutableStateFlow(emptyList())

    init {
        updateAll()
    }


    private fun getGroup() {
        isGroupLoading.value = true
        GroupRequester.getGroupById(
            groupId,
            callback = { response ->
                response?.data?.let {
                    group.value = it
                }
                Log.d("GroupViewModel", "getGroup: $response")
                isGroupLoading.value = false
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getGroup: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
                isGroupLoading.value = false
            }
        )
    }

    private fun getLists() {
        ListRequester.getLists(
            groupId,
            callback = { response ->
                response?.data?.let {
                    lists.value = it
                }
                Log.d("GroupViewModel", "getLists: $response")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getLists: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun onRefresh() {
        updateAll()
    }

    private fun updateAll() {
        viewModelScope.launch {
            isRefreshing.value = true
            val jobs = listOf(
                launch { getGroup() },
                launch { getLists() }
            )
            jobs.joinAll()
            isRefreshing.value = false
        }
    }

    fun onImageSelected(imageUri: Uri) {
        Log.d("GroupViewModel", "onImageSelected: $imageUri")
    }

    fun onUserRemoved(user: UserData) {
        Log.d("GroupViewModel", "onUserDeleted: $user")
    }

    fun onNameChanged(name: String) {
        group.value.name = name
        GroupRequester.updateGroup(
            groupId,
            CreateUpdateGroupRequest(group.value.name),
            callback = {
                Log.d("GroupViewModel", "onNameChanged: $it")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onNameChanged: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun onAddList(listName: String) {
        ListRequester.createList(
            CreateListRequest(listName, groupId),
            callback = {
                Log.d("GroupViewModel", "onAddList: $it")
                it?.let {
                    val curLists = lists.value.toMutableList()
                    curLists.add(it.data)
                    lists.value = curLists
                }
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onAddList: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun getInviteLink() {
        MiscRequester.getInviteToken(
            groupId,
            callback = { response ->
                group.value.inviteLink = response?.token
                Log.d("GroupViewModel", "getInviteLink: $response")
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "getInviteLink: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun onDeleteList(toDelListId: Int) {
        ListRequester.deleteList(
            toDelListId,
            callback = {
                Log.d("GroupViewModel", "onDeleteList: $it")
                val curLists = lists.value.toMutableList()
                curLists.removeIf { list -> list.id == toDelListId }
                lists.value = curLists
            },
            onError = { code, body ->
                Log.e("GroupViewModel", "onDeleteList: $code $body")
                Toast.makeText(App.getContext(), "Error: $code", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun shareQr(qrBitmap: ImageBitmap, context: Context) {
        val file = File(context.cacheDir, "qr_code.png")
        val fOut = FileOutputStream(file)

        // Преобразование ImageBitmap в android.graphics.Bitmap
        val androidBitmap = qrBitmap.asAndroidBitmap()

        // Создание нового Bitmap с белым фоном
        val whiteBmp = Bitmap.createBitmap(androidBitmap.width, androidBitmap.height, androidBitmap.config)
        val canvas = Canvas(whiteBmp)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(androidBitmap, 0f, 0f, null)

        // Сжатие Bitmap с белым фоном
        whiteBmp.compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_qr_text, group.value.name))
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // Установите ClipData для поддержки как текста, так и изображения
        val clipData = ClipData.newUri(context.contentResolver, "QR Code", uri)
        shareIntent.clipData = clipData

        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }


}