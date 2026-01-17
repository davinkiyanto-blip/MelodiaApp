package com.xcode.melodia.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcode.melodia.data.repository.FirebaseRepository
import com.xcode.melodia.data.repository.SongEntity
import com.xcode.melodia.di.ServiceLocator
import com.xcode.melodia.utils.AudioPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val firebaseRepository: FirebaseRepository = ServiceLocator.firebaseRepository

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs = _songs.asStateFlow()
    
    // Bind to AudioPlayerManager state
    val playingSongUrl = AudioPlayerManager.currentUrl

    fun fetchSongs() {
        viewModelScope.launch {
            _songs.value = firebaseRepository.getUserSongs()
        }
    }
    
    fun togglePlay(song: SongEntity) {
        if (!song.audioUrl.isNullOrEmpty()) {
            AudioPlayerManager.play(song.audioUrl)
        }
    }
}
