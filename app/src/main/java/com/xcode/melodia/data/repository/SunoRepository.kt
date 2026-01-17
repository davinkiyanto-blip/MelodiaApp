package com.xcode.melodia.data.repository

import com.xcode.melodia.data.api.GenerateCustomMusicRequest
import com.xcode.melodia.data.api.GenerateMusicRequest
import com.xcode.melodia.data.api.SunoApiService
import com.xcode.melodia.data.api.SunoTaskResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SunoRepository(private val apiService: SunoApiService) {

    suspend fun generateMusic(prompt: String, isInstrumental: Boolean, tags: String?, title: String?): Result<List<SunoTaskResponse>> {
        return try {
            val response = apiService.generateMusic(
                GenerateMusicRequest(
                    prompt = prompt,
                    makeInstrumental = isInstrumental,
                    tags = tags,
                    title = title
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateCustom(lyrics: String, style: String?, title: String?): Result<List<SunoTaskResponse>> {
        return try {
            val response = apiService.generateCustomMusic(
                GenerateCustomMusicRequest(
                    prompt = lyrics,
                    tags = style,
                    title = title
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Polling logic
    fun pollStatus(ids: String): Flow<List<SunoTaskResponse>> = flow {
        while (true) {
            try {
                val status = apiService.getMusicStatus(ids)
                emit(status)
                // Check if all complete, else delay
                val allFinished = status.all { it.status == "completed" || it.status == "error" } 
                if (allFinished) break
            } catch (e: Exception) {
                // emit failure or retry?
                // For now, simple retry logic or log
            }
            delay(5000) // 5 seconds delay
        }
    }
}
