package ru.hihit.cobuy.api.requesters

import android.content.Context
import android.widget.Toast
import retrofit2.Response
import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.errors.HttpException
import ru.hihit.cobuy.api.errors.NetworkException
import ru.hihit.cobuy.utils.parseJson
import java.io.IOException

internal object RequestLauncher {
    suspend inline fun <reified T> launchRequest(
        request: suspend () -> Response<T>,
        errorMessage: String = "Ошибка запроса"
    ): Result<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else if (T::class == Unit::class) {
                    Result.success(Unit as T)
                } else {
                    Result.failure(Exception("$errorMessage - Пустой ответ от сервера"))
                }
            } else {
                val errorText = response.errorBody()?.string()
                Result.failure(HttpException(response.code(), errorText))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("$errorMessage - Ошибка сети: ${e.localizedMessage}", e))
        } catch (e: Exception) {
            Result.failure(Exception("$errorMessage - Ошибка: ${e.localizedMessage}", e))
        }
    }
}

fun <T> Result<T>.handle(
    context: Context = App.getContext(),
    onSuccess: (T) -> Unit,
    onServerError: (Map<String, Any>?) -> Unit,
    onOtherError: (String) -> Unit = {},
    finally: () -> Unit = {}
) {
    this.onSuccess(onSuccess)
        .onFailure { error ->
            when (error) {
                is HttpException -> {
                    val parsed = error.body?.let { parseJson(it) }
                    if (parsed?.isEmpty() == true) {
                        Toast.makeText(context, error.localizedMessage ?: "Unknown error", Toast.LENGTH_LONG).show()
                        onOtherError(error.localizedMessage ?: "Unknown error")
                    } else {
                        onServerError(parsed)
                    }
                }
                is NetworkException -> {
                    Toast.makeText(context, error.localizedMessage ?: "Network error", Toast.LENGTH_LONG).show()
                    onOtherError(error.localizedMessage ?: "Network error")
                }
                else -> {
                    Toast.makeText(context, error.localizedMessage ?: "Unknown error", Toast.LENGTH_LONG).show()
                    onOtherError(error.localizedMessage ?: "Unknown error")
                }
            }
        }
    finally()
}

