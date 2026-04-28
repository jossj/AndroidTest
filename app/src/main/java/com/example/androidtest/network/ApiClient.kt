package com.example.androidtest.network

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 is the Android emulator alias for the host machine's localhost.
    // Change to your machine's LAN IP (e.g. 192.168.x.x) when running on a real device.
    const val BASE_URL = "http://10.0.2.2:8080/"

    private var authHeader = ""

    fun setCredentials(username: String, password: String) {
        authHeader = if (username.isNotEmpty()) Credentials.basic(username, password) else ""
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .apply { if (authHeader.isNotEmpty()) header("Authorization", authHeader) }
                .build()
            chain.proceed(request)
        }
        .build()

    val service: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
