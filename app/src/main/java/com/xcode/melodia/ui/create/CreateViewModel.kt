package com.xcode.melodia.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcode.melodia.data.repository.FirebaseRepository
import com.xcode.melodia.data.repository.SongEntity
import com.xcode.melodia.data.repository.SunoRepository
import com.xcode.melodia.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateViewModel : ViewModel() {

    private val sunoRepository: SunoRepository = ServiceLocator.sunoRepository
    private val firebaseRepository: FirebaseRepository = ServiceLocator.firebaseRepository

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun generateMusic(prompt: String, isInstrumental: Boolean, tags: String?, title: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            val result = sunoRepository.generateMusic(prompt, isInstrumental, tags, title)
            
            result.onSuccess { tasks ->
                // API returns tasks immediately. We should poll or just save 'pending' to Firestore
                tasks.forEach { task ->
                    val song = SongEntity(
                        id = task.id,
                        title = task.title ?: title ?: "Untitled",
                        status = task.status, // Likely 'submitted' or 'queued'
                        type = "generate",
                        createdAt = System.currentTimeMillis()
                    )
                    firebaseRepository.saveSongToHistory(song)
                }
                _message.value = "Generation started! Check Library."
            }.onFailure { e ->
                _message.value = "Error: ${e.localizedMessage}"
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}
