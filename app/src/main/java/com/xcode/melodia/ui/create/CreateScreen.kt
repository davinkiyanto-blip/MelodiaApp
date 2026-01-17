package com.xcode.melodia.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xcode.melodia.ui.components.MelodiaButton
import com.xcode.melodia.ui.components.MelodiaHeader
import com.xcode.melodia.ui.components.MelodiaTextField
import com.xcode.melodia.ui.theme.MelodiaBackgroundGradient
import com.xcode.melodia.ui.theme.MelodiaPrimary

@Composable
fun CreateScreen() {
    val viewModel: CreateViewModel = viewModel()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Generate", "Custom", "Upload")
    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Main Container with Gradient
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
                    .weight(1f) // Fill remaining space
            ) {
                when (selectedTab) {
                    0 -> GenerateTab(viewModel)
                    1 -> CustomTab()
                    2 -> UploadTab()
                }
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
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
    var prompt by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isInstrumental by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    Column {
        Text(
            "Describe your song", 
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.height(12.dp))
        MelodiaTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = "Enter prompt (e.g. 'A cyberpunk synthwave track')",
            minLines = 4
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Instrumental", 
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Generate music without vocals", 
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
        Spacer(modifier = Modifier.height(24.dp))
        
        MelodiaTextField(value = tags, onValueChange = { tags = it }, label = "Tags (Optional, e.g. 'upbeat, jazz')")
        Spacer(modifier = Modifier.height(16.dp))
        MelodiaTextField(value = title, onValueChange = { title = it }, label = "Title (Optional)")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        MelodiaButton(
            text = "Generate Music",
            onClick = {
                viewModel.generateMusic(prompt, isInstrumental, tags, title)
            },
            isLoading = isLoading,
            enabled = prompt.isNotBlank()
        )
    }
}

@Composable
fun CustomTab() {
    var lyrics by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Column {
        Text("Custom Generation", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        MelodiaTextField(
            value = lyrics,
            onValueChange = { lyrics = it },
            label = "Enter Lyrics",
            minLines = 6
        )
        Spacer(modifier = Modifier.height(20.dp))
        MelodiaTextField(value = style, onValueChange = { style = it }, label = "Style of Music (e.g. Pop, Rock)")
        Spacer(modifier = Modifier.height(16.dp))
        MelodiaTextField(value = title, onValueChange = { title = it }, label = "Title")
        
        Spacer(modifier = Modifier.height(32.dp))
        MelodiaButton(
            text = "Generate Song",
            onClick = { /* ViewModel logic */ },
            enabled = lyrics.isNotBlank()
        )
    }
}

@Composable
fun UploadTab() {
    var prompt by remember { mutableStateOf("") }
    
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
        
        MelodiaTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = "Instructions (e.g. 'Make it jazz')",
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        MelodiaButton(
            text = "Upload & Generate",
            onClick = { /* Upload Logic */ },
            enabled = prompt.isNotBlank()
        )
    }
}
