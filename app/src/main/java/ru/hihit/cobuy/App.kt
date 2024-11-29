package ru.hihit.cobuy

import android.app.Application
import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import ru.hihit.cobuy.pusher.PusherService


class App : Application() {

    lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient
    lateinit var pusherService: PusherService

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        coerceInputValues = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    override fun onCreate() {
        super.onCreate()
        pusherService = PusherService()
        instance = this
        val authInterceptor = AuthInterceptor(this)
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(this.getString(R.string.api_url))
            .client(okHttpClient)
            .build()
    }

    override fun onTerminate() {
        super.onTerminate()
        pusherService.close()
    }

    companion object {
        private lateinit var instance: App

        fun getRetrofit(): Retrofit {
            return instance.retrofit
        }

        fun getContext(): Context {
            return instance
        }

        fun getPusherService(): PusherService {
            instance.pusherService.isPusherConnected()
            return instance.pusherService
        }
    }
}


class AuthInterceptor(context: Context) : Interceptor {

    private val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()
        return chain.proceed(request)
    }

    private fun getToken(): String {
        val token = sharedPreferences.getString("auth_token", "") ?: ""
//        Log.d("AuthInterceptor", "Got token: $token")
        return token
    }
}