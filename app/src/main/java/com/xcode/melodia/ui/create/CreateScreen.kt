package com.xcode.melodia.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
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
    
    // Hoisted State for Generate
    var mode by remember { mutableStateOf("simple") }
    var prompt by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isInstrumental by remember { mutableStateOf(false) }
    var lyrics by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progressMessage by viewModel.progressMessage.collectAsState()
    val generatedSong by viewModel.generatedSong.collectAsState()

    var showSuccessDialog by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    
    // Listen for effects
    LaunchedEffect(message) {
        message?.let {
            showErrorDialog = it
            viewModel.clearError()
        }
    }

    LaunchedEffect(generatedSong) {
        generatedSong?.let { 
             showSuccessDialog = "Music Generated: ${it.title}"
        }
    }
    
    if (showSuccessDialog != null) {
        com.xcode.melodia.ui.components.MelodiaSuccessDialog(
            message = showSuccessDialog!!,
            onDismissRequest = { showSuccessDialog = null }
        )
    }
    
    if (showErrorDialog != null) {
        com.xcode.melodia.ui.components.MelodiaAlertDialog(
            title = "Error",
            message = showErrorDialog!!,
            confirmText = "OK",
            onConfirm = { showErrorDialog = null },
            onDismissRequest = { showErrorDialog = null }
        )
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

            // Main content with sticky button
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    when (selectedTab) {
                        0 -> GenerateTab(
                            mode = mode, onModeChange = { mode = it },
                            prompt = prompt, onPromptChange = { prompt = it },
                            title = title, onTitleChange = { title = it },
                            isInstrumental = isInstrumental, onInstrumentalChange = { isInstrumental = it },
                            lyrics = lyrics, onLyricsChange = { lyrics = it },
                            style = style, onStyleChange = { style = it }
                        )
                        1 -> CoverTab(viewModel)
                    }
                    Spacer(modifier = Modifier.height(120.dp)) // Increased padding to prevent button overlap
                }

                // Sticky Floating Button for Generate Tab
                if (selectedTab == 0) {
                     Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF16161E))
                                )
                            )
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp) // Adjusted for visual balance
                            .zIndex(10f) // Ensure it is on top
                    ) {
                        MelodiaButton(
                            text = "Generate Music (5 Credits)",
                            onClick = {
                                if (mode == "custom") {
                                    viewModel.generateMusic(
                                        customMode = true,
                                        instrumental = isInstrumental,
                                        prompt = if (isInstrumental) "" else lyrics,
                                        style = style,
                                        title = title
                                    )
                                } else {
                                    viewModel.generateMusic(
                                        customMode = false,
                                        instrumental = false,
                                        prompt = prompt,
                                        style = "", 
                                        title = title
                                    )
                                }
                            },
                            enabled = (mode == "simple" && prompt.isNotBlank()) ||
                                      (mode == "custom" && isInstrumental && style.isNotBlank()) ||
                                      (mode == "custom" && !isInstrumental && lyrics.isNotBlank() && style.isNotBlank()),
                            isLoading = isLoading
                        )
                    }
                }
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
fun GenerateTab(
    mode: String, onModeChange: (String) -> Unit,
    prompt: String, onPromptChange: (String) -> Unit,
    title: String, onTitleChange: (String) -> Unit,
    isInstrumental: Boolean, onInstrumentalChange: (Boolean) -> Unit,
    lyrics: String, onLyricsChange: (String) -> Unit,
    style: String, onStyleChange: (String) -> Unit
) {

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
                    onClick = { onModeChange("simple") },
                    modifier = Modifier.weight(1f)
                )
                ModeSelectionCard(
                    title = "Custom",
                    subtitle = "Create with lyrics",
                    icon = Icons.Default.Edit,
                    isSelected = mode == "custom",
                    onClick = { onModeChange("custom") },
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
                onValueChange = onTitleChange,
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
                        onCheckedChange = onInstrumentalChange,
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
                onValueChange = onPromptChange,
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
                    onValueChange = onLyricsChange,
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
                    onValueChange = onStyleChange,
                    label = "Genre (e.g. Pop, Jazz)",
                    placeholder = "City Pop, Funk, 80s..."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Button moved to parent
        Spacer(modifier = Modifier.height(60.dp))
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
