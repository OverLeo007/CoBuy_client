package ru.hihit.cobuy.api.requesters

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

internal object RequestLauncher {
    fun <T> launchRequest(
        request: suspend () -> Response<T>,
        callback: (T?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit,
        errorMessage: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = request()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    val body: ResponseBody? = response.errorBody()
                    Log.e("Requester", "$errorMessage ${response.code()}: ${body?.string()}")
                    onError(response.code(), body)
                }
            }
        }
    }
}