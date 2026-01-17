package com.xcode.melodia.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.xcode.melodia.ui.components.SongCard
import androidx.compose.material.icons.filled.LibraryMusic

@Composable
fun LibraryScreen() {
    val viewModel: LibraryViewModel = viewModel()
    val songs by viewModel.songs.collectAsState()
    val playingSongUrl by viewModel.playingSongUrl.collectAsState()
    
    LaunchedEffect(true) {
        viewModel.fetchSongs()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (songs.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.LibraryMusic,
                    contentDescription = "Empty Library",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No songs found",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Start generating music to see it here!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(songs) { song ->
                    SongCard(
                        title = song.title,
                        imageUrl = song.imageUrl.takeIf { it.isNotEmpty() },
                        status = song.status,
                        isPlaying = playingSongUrl == song.audioUrl,
                        onPlayClick = { viewModel.togglePlay(song) },
                        onMoreClick = { /* Show BottomSheet */ }
                    )
                }
            }
        }
    }
}
