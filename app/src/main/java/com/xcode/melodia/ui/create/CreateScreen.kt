package com.xcode.melodia.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xcode.melodia.ui.components.MelodiaButton
import com.xcode.melodia.ui.components.MelodiaTextField
import com.xcode.melodia.ui.theme.MelodiaBackgroundGradient
import com.xcode.melodia.ui.theme.MelodiaPrimary

@Composable
fun CreateScreen() {
    val viewModel: CreateViewModel = viewModel()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Generate", "Cover")
    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progressMessage by viewModel.progressMessage.collectAsState()
    val generatedSong by viewModel.generatedSong.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(generatedSong) {
        generatedSong?.let { 
             snackbarHostState.showSnackbar("Music Generated: ${it.title}")
             // Navigation to Library could happen here or in ViewModel
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MelodiaBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Custom Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MelodiaPrimary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                            color = MelodiaPrimary,
                            height = 3.dp
                        )
                    }
                },
                divider = { }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MelodiaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> GenerateTab(viewModel)
                    1 -> CoverTab(viewModel)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        
        // Progress Overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .zIndex(20f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    CircularProgressIndicator(
                        color = MelodiaPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Creating Music",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = progressMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f)
        )
    }
}

@Composable
fun GenerateTab(viewModel: CreateViewModel) {
    var mode by remember { mutableStateOf("simple") } // "simple" or "custom"
    var prompt by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isInstrumental by remember { mutableStateOf(false) }
    var lyrics by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        
        // Section 1: Mode Selection
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MelodiaPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = MelodiaPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Select Mode", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModeSelectionCard(
                    title = "Simple",
                    subtitle = "Create from description",
                    icon = Icons.Default.MusicNote,
                    isSelected = mode == "simple",
                    onClick = { mode = "simple" },
                    modifier = Modifier.weight(1f)
                )
                ModeSelectionCard(
                    title = "Custom",
                    subtitle = "Create with lyrics",
                    icon = Icons.Default.Edit,
                    isSelected = mode == "custom",
                    onClick = { mode = "custom" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 2: Title & Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MelodiaPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = MelodiaPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Title & Options", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }

            MelodiaTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title (Optional)",
                placeholder = "Give your song a title..."
            )

            // Instrumental Toggle - Only in Custom Mode
            if (mode == "custom") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Instrumental",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "No vocals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isInstrumental,
                        onCheckedChange = { isInstrumental = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MelodiaPrimary,
                            checkedTrackColor = MelodiaPrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        // Section 3: Description / Prompt
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MelodiaPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", color = MelodiaPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    if (mode == "simple") "Music Description" else "Lyrics Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            
            MelodiaTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = if (mode == "simple") "Describe your song" else "Describe lyrics topic",
                placeholder = if (mode == "simple") "e.g. A cyberpunk synthwave track..." else "e.g. A song about lost love...",
                minLines = 4
            )
            Text(
                "${prompt.length}/${if (mode == "simple") 500 else 5000}", 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.End
            )
        }

        // Section 4: Lyrics (Custom Mode Only & Not Instrumental)
        if (mode == "custom" && !isInstrumental) {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MelodiaPrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("4", color = MelodiaPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Lyrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                MelodiaTextField(
                    value = lyrics,
                    onValueChange = { lyrics = it },
                    label = "Enter Lyrics",
                    placeholder = "Verse 1: ...",
                    minLines = 6
                )
            }
        }

        // Section 5: Style (Custom Mode Only)
        if (mode == "custom") {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MelodiaPrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("5", color = MelodiaPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Genre / Style", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                MelodiaTextField(
                    value = style,
                    onValueChange = { style = it },
                    label = "Genre (e.g. Pop, Jazz)",
                    placeholder = "City Pop, Funk, 80s..."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        MelodiaButton(
            text = "Generate Music",
            onClick = {
                if (mode == "custom") {
                     viewModel.generateMusic(
                        customMode = true,
                        instrumental = isInstrumental,
                        prompt = if (isInstrumental) "" else lyrics, // If instrumental, prompt ignored by backend logic assumption, but here passing lyrics as description for custom? Wait.
                        // Re-reading logic: 
                        // Custom Mode Vocal: prompt = lyrics, style = style
                        // Custom Mode Instrumental: prompt = "", style = style. (The "Lyrics Description" field above in UI is effectively ignored for Instrumental or maybe used for generation? Reference says "Prompt is ignored in instrumental mode". But "Lyrics Description" is confusing if not used. 
                        // In Reference UI: "Section 3: Description" is ALWAYS there.
                        // But wait, line 116 in route.ts: "Instrumental mode - clear prompt".
                        // Line 120 vocals: "requestBody.prompt = prompt || ''".
                        // In Frontend Reference: formData.prompt is used for both modes.
                        // If Custom Mode: payload prompt is formData.lyrics! 
                        // Wait, reference frontend line 289: `prompt: formData.lyrics` for Custom Mode. 
                        // And formData.prompt (Description) is used for generating lyrics? Yes, `handleGenerateLyrics` uses `formData.prompt`.
                        // SO: For Custom Mode, `prompt` param sent to API is the LYRICS.
                        // The "Music Description" field in Custom Mode is seemingly just for generating lyrics or maybe unused for actual generation call?
                        // Actually, let's look at reference line 289 again.
                        // `prompt: formData.lyrics`.
                        // So the `formData.prompt` (Description) is NOT sent in Custom Mode Vocal generation. Only `formData.lyrics` is sent as `prompt`.
                        // For Custom Mode Instrumental: `prompt` is ignored anyway.
                        // So in my UI: 
                        // If Custom Mode Vocal: Pass `lyrics` as `prompt`. 
                        // IF Custom Mode Instrumental: Pass empty string.
                        style = style,
                        title = title
                    )
                } else {
                     viewModel.generateMusic(
                        customMode = false,
                        instrumental = false,
                        prompt = prompt, // Simple mode uses description
                        style = "", 
                        title = title
                    )
                }
            },
            enabled = (mode == "simple" && prompt.isNotBlank()) ||
                      (mode == "custom" && isInstrumental && style.isNotBlank()) ||
                      (mode == "custom" && !isInstrumental && lyrics.isNotBlank() && style.isNotBlank())
        )
    }
}

@Composable
fun ModeSelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MelodiaPrimary.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f))
            .border(2.dp, if (isSelected) MelodiaPrimary else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (isSelected) MelodiaPrimary else MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun CoverTab(viewModel: CreateViewModel) {
    var prompt by remember { mutableStateOf("") }
    var audioId by remember { mutableStateOf("") }
    
    Column {
        Text("Upload & Cover", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            MelodiaButton(
                text = "Pick Audio File",
                onClick = { /* File Picker logic */ },
                modifier = Modifier.width(200.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
           Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha=0.1f))) 
           Text(" OR ", modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
           Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha=0.1f))) 
        }

        Spacer(modifier = Modifier.height(24.dp))

        MelodiaTextField(
            value = audioId,
            onValueChange = { audioId = it },
            label = "Audio ID",
            placeholder = "Enter ID from library..."
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        MelodiaTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = "Cover Instructions",
            placeholder = "e.g. Make it jazz, slower tempo...",
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        MelodiaButton(
            text = "Generate Cover",
            onClick = { /* Upload Logic */ },
            enabled = prompt.isNotBlank() && audioId.isNotEmpty() // Simplified
        )
    }
}
