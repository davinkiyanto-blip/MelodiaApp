package com.xcode.melodia.utils

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl = _currentUrl.asStateFlow()

    fun play(url: String) {
        if (_currentUrl.value == url && mediaPlayer?.isPlaying == true) {
            pause()
            return
        }
        
        stop() // Stop previous
        
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync() // Use async for network
                setOnPreparedListener { start() }
                setOnCompletionListener { 
                    _currentUrl.value = null 
                }
            }
            _currentUrl.value = url
        } catch (e: Exception) {
            e.printStackTrace()
            _currentUrl.value = null
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _currentUrl.value = null // Simplified state
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        _currentUrl.value = null
    }
}
