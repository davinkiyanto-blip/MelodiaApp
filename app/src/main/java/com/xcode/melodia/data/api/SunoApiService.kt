package com.xcode.melodia.data.api

import com.xcode.melodia.data.model.GenerateRequest
import com.xcode.melodia.data.model.TaskResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface SunoApiService {

    @POST("ai-music/suno-music")
    suspend fun generateMusic(@Body request: GenerateRequest): TaskResponse

    @GET
    suspend fun pollTask(@Url url: String): TaskResponse
}
