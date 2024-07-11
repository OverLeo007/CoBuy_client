package ru.hihit.cobuy

import android.app.Application
import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit



class App : Application() {

    lateinit var retrofit: Retrofit
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        val authInterceptor = AuthInterceptor(this)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(this.getString(R.string.api_url))
            .client(okHttpClient)
            .build()
    }

    companion object {
        private lateinit var instance: App

        fun getRetrofit(): Retrofit {
            return instance.retrofit
        }

        fun getContext(): Context {
            return instance
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
        return sharedPreferences.getString("auth_token", "") ?: ""
    }
}