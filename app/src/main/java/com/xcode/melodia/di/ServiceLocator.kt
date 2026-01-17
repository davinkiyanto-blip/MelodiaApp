package com.xcode.melodia.di

import com.xcode.melodia.data.api.SunoApiService
import com.xcode.melodia.data.repository.FirebaseRepository
import com.xcode.melodia.data.repository.SunoRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {



    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer ${com.xcode.melodia.BuildConfig.SUNO_API_KEY}")
            .header("Content-Type", "application/json")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS) // Increased timeout for generation
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(com.xcode.melodia.BuildConfig.SUNO_API_BASE_URL.trimEnd('/') + "/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val sunoApiService: SunoApiService by lazy {
        retrofit.create(SunoApiService::class.java)
    }

    val sunoRepository by lazy {
        SunoRepository(sunoApiService)
    }

    val firebaseRepository by lazy {
        FirebaseRepository()
    }
}
