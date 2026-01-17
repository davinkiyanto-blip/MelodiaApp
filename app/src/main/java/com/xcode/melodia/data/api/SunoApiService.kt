package com.xcode.melodia.data.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SunoApiService {

    @POST("suno/generate-music")
    suspend fun generateMusic(@Body request: GenerateMusicRequest): List<SunoTaskResponse>

    @POST("suno/custom-generate-music") // Assuming endpoint for custom/lyrics
    suspend fun generateCustomMusic(@Body request: GenerateCustomMusicRequest): List<SunoTaskResponse>

    @GET("suno/get/{ids}")
    suspend fun getMusicStatus(@Path("ids") ids: String): List<SunoTaskResponse>

    @POST("suno/upload-and-cover-audio")
    @Multipart
    suspend fun uploadAndCover(
        @Part file: MultipartBody.Part,
        @Part("prompt") prompt: String
    ): SunoTaskResponse // Response type might vary, checking doc assumption
    
    // Additional endpoints as needed
}

data class GenerateMusicRequest(
    val prompt: String,
    val tags: String? = null,
    val mv: String = "chirp-v3-0",
    val title: String? = null,
    @SerializedName("make_instrumental") val makeInstrumental: Boolean = false,
    @SerializedName("wait_audio") val waitAudio: Boolean = false
)

data class GenerateCustomMusicRequest(
    val prompt: String, // Lyrics
    val tags: String? = null, // Style
    val title: String? = null,
    val mv: String = "chirp-v3-0",
    @SerializedName("wait_audio") val waitAudio: Boolean = false
)

data class SunoTaskResponse(
    val id: String,
    val title: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("audio_url") val audioUrl: String?,
    @SerializedName("video_url") val videoUrl: String?,
    val status: String, // "streaming", "completed", "submitted"
    val duration: Double?,
    val tags: String?
)
