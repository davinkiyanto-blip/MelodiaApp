package com.xcode.melodia.ui.library
 
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xcode.melodia.ui.components.SongCard
import com.xcode.melodia.ui.theme.MelodiaBackgroundGradient
import androidx.compose.foundation.layout.width
import com.xcode.melodia.ui.components.MelodiaButton
 
@Composable
fun LibraryScreen(onNavigateToCreate: () -> Unit) {
    val viewModel: LibraryViewModel = viewModel()
    val songs by viewModel.songs.collectAsState()
    val playingSongUrl by viewModel.playingSongUrl.collectAsState()
    
    LaunchedEffect(true) {
        viewModel.fetchSongs()
    }
 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MelodiaBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            // Header removed as requested
            Spacer(modifier = Modifier.height(16.dp))
            
            if (songs.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.LibraryMusic,
                                contentDescription = "Empty Library",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No songs found",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start generating music to see it here!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        com.xcode.melodia.ui.components.MelodiaButton(
                            text = "Create First Song",
                            onClick = onNavigateToCreate,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                ) {
                    items(songs) { song ->
                        SongCard(
                            title = song.title,
                            imageUrl = song.imageUrl?.takeIf { it.isNotEmpty() },
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
}
