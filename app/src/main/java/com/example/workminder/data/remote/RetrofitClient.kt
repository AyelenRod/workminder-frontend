package com.example.workminder.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var currentService: ApiService? = null
    private var lastBaseUrl: String? = null

    // Interceptor to add JWT token
    private val authInterceptor = Interceptor { chain ->
        var request = chain.request()
        val token = AuthManager.token
        
        if (token != null) {
            request = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
            
        try {
            chain.proceed(request)
        } catch (e: java.io.IOException) {
            // Si hay error de red (como SocketTimeout), esto podría disparar un re-descubrimiento en el siguiente intento
            throw e
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
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
