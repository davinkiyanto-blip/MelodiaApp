package com.xcode.melodia.ui.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.xcode.melodia.ui.components.ComingSoonDialog

@Composable
fun ToolsScreen() {
    var showComingSoon by remember { mutableStateOf(false) }

    if (showComingSoon) {
        ComingSoonDialog(onDismissRequest = { showComingSoon = false })
    }

    val tools = listOf(
        ToolItem("AI Lyrics", Icons.Filled.Mic),
        ToolItem("Stem Splitter", Icons.AutoMirrored.Filled.CallSplit),
        ToolItem("Voice Changer", Icons.Filled.RecordVoiceOver),
        ToolItem("Audio Merger", Icons.AutoMirrored.Filled.MergeType),
        ToolItem("Converter", Icons.Filled.Transform),
        ToolItem("Equalizer", Icons.Filled.GraphicEq)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.xcode.melodia.ui.theme.MelodiaBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Studio Tools", 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), 
                modifier = Modifier.padding(bottom = 24.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools) { tool ->
                    ToolCard(tool) {
                        showComingSoon = true
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom padding
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class ToolItem(val title: String, val icon: ImageVector)
