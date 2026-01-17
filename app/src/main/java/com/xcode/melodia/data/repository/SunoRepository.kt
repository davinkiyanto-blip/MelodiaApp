package com.xcode.melodia.data.repository
 
import com.xcode.melodia.data.api.SunoApiService
import com.xcode.melodia.data.model.GenerateRequest
import com.xcode.melodia.data.model.TaskResponse
 
class SunoRepository(private val apiService: SunoApiService) {
 
    suspend fun generate(
        customMode: Boolean,
        instrumental: Boolean,
        prompt: String,
        style: String = "",
        title: String = "",
        model: String = "V5"
    ): Result<String> {
        return try {
            val request = GenerateRequest(
                customMode = customMode,
                instrumental = instrumental,
                prompt = prompt,
                style = style,
                title = title,
                model = model
            )
            // Call MelodiAPI
            val responseBody = apiService.generateMusic("https://melodi-api.vercel.app/api/generate", request)
            val taskUrl = responseBody.string()
            
            if (taskUrl.startsWith("http")) {
                 Result.success(taskUrl)
            } else {
                 Result.failure(Exception("Invalid response from MelodiAPI: $taskUrl"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
 
    suspend fun pollTask(taskUrl: String): Result<TaskResponse> {
        return try {
            val response = apiService.pollTask(taskUrl)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
