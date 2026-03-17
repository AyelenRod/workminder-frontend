package com.example.workminder.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var currentService: ApiService? = null
    private var lastBaseUrl: String? = null

    private val authInterceptor = Interceptor { chain ->
        var request = chain.request()
        val token = AuthManager.token
        
        if (!token.isNullOrBlank()) {
            val trimmedToken = token.trim()
            val finalToken = if (trimmedToken.startsWith("Bearer ", ignoreCase = true)) {
                trimmedToken
            } else {
                "Bearer $trimmedToken"
            }
            
            request = request.newBuilder()
                .header("Authorization", finalToken)
                .build()
        }
            
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService
        get() {
            val url = NetworkConfig.baseUrl
            if (currentService == null || url != lastBaseUrl) {
                lastBaseUrl = url
                currentService = Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return currentService!!
        }
}
