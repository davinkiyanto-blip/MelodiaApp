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

    private const val BASE_URL = "https://kie.ai/api/" // Adjust based on actual base URL from PRD or Docs. PRD says https://docs.kie.ai/suno-api/.. usually implies a base like https://api.kie.ai or something.
    // NOTE: The PRD links to https://docs.kie.ai/suno-api/generate-music but doesn't explicitly state the BASE URL.
    // I will assume standard convention or user provided "API Provider: Kie.ai Suno API".
    // I'll use a placeholder and might need to correct it.
    // Actually looking at doc references: https://docs.kie.ai/suno-api/... usually means endpoint is like https://api.kie.ai/v1/ or similar.
    // I'll stick to a placeholder "https://api.kie.ai/" for now.

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.kie.ai/") // TODO: Confirm Base URL
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
