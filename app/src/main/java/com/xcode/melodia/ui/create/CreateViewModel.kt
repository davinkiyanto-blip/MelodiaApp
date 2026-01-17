package com.xcode.melodia.ui.create
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcode.melodia.data.model.MusicRecord
import com.xcode.melodia.data.repository.FirebaseRepository
import com.xcode.melodia.data.repository.SongEntity
import com.xcode.melodia.data.repository.SunoRepository
import com.xcode.melodia.di.ServiceLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
 
class CreateViewModel : ViewModel() {
 
    private val sunoRepository: SunoRepository = ServiceLocator.sunoRepository
    private val firebaseRepository: FirebaseRepository = ServiceLocator.firebaseRepository
 
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
 
    private val _generationStatus = MutableStateFlow<String?>(null) // pending, processing, done, failed
    val generationStatus = _generationStatus.asStateFlow()
 
    private val _progressMessage = MutableStateFlow<String>("")
    val progressMessage = _progressMessage.asStateFlow()
 
    private val _generatedSong = MutableStateFlow<MusicRecord?>(null)
    val generatedSong = _generatedSong.asStateFlow()
 
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
 
    fun generateMusic(
        customMode: Boolean,
        instrumental: Boolean,
        prompt: String,
        style: String = "",
        title: String = "",
        model: String = "V5"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _generationStatus.value = "pending"
            _progressMessage.value = "Starting generation..."
            _error.value = null
            _generatedSong.value = null
 
            val result = sunoRepository.generate(customMode, instrumental, prompt, style, title, model)

            result.onSuccess { taskUrl ->
                pollTask(taskUrl, title, prompt, style, model)
            }.onFailure { e ->
                _isLoading.value = false
                _error.value = "Error: ${e.message}"
            }
        }
    }
 
    private suspend fun pollTask(taskUrl: String, title: String, prompt: String, tags: String, model: String) {
        var attempts = 0
        val maxAttempts = 60 // 5 minutes at 5s interval
 
        while (attempts < maxAttempts) {
            val pollResult = sunoRepository.pollTask(taskUrl)
            
            var shouldBreak = false
            
            pollResult.onSuccess { task ->
                _generationStatus.value = task.status
                
                // Update progress message
                _progressMessage.value = when (task.status) {
                    "pending" -> "Waiting in queue..."
                    "processing" -> task.progress ?: "Processing audio..."
                    else -> "Processing..."
                }
 
                if (task.status == "done" || task.status == "completed" || task.status == "succeeded") {
                    val records = task.records
                    if (!records.isNullOrEmpty()) {
                        val music = records[0]
                        _generatedSong.value = music
                        saveToFirestore(music, title, prompt, tags, model)
                        _progressMessage.value = "Generation complete!"
                        shouldBreak = true
                    }
                } else if (task.status == "failed" || task.status == "error") {
                     _error.value = "Generation failed: ${task.message}"
                     shouldBreak = true
                }
            }.onFailure {
                // Log error but continue polling? or fail?
                // For now continue, maybe transient network error
            }
 
            if (shouldBreak) break
 
            attempts++
            if (attempts >= maxAttempts) {
                _error.value = "Timeout: Generation took too long"
                break
            }
            delay(5000)
        }
        _isLoading.value = false
    }
 
    private fun saveToFirestore(music: MusicRecord, originalTitle: String, originalPrompt: String, originalTags: String, originalModel: String) {
        viewModelScope.launch {
            val song = SongEntity(
                id = music.id,
                title = music.title ?: originalTitle.ifEmpty { "Untitled" },
                status = "done",
                type = "generate",
                imageUrl = music.imageUrl,
                audioUrl = music.audioUrl,
                duration = music.duration?.toString(),
                prompt = music.prompt ?: originalPrompt,
                tags = music.tags ?: originalTags,
                model = music.model ?: originalModel,
                createdAt = System.currentTimeMillis()
            )
            firebaseRepository.saveSongToHistory(song)
        }
    }
 
    fun clearError() {
        _error.value = null
    }
}
