package com.xcode.melodia.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xcode.melodia.ui.components.MelodiaButton
import com.xcode.melodia.ui.components.MelodiaHeader
import com.xcode.melodia.ui.components.MelodiaTextField

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

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> GenerateTab(viewModel)
                    1 -> CustomTab()
                    2 -> UploadTab()
                }
            }

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
        Text("Describe your song", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        MelodiaTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = "Enter prompt (e.g. 'A cyberpunk synthwave track')",
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Instrumental", modifier = Modifier.weight(1f))
            Switch(
                checked = isInstrumental,
                onCheckedChange = { isInstrumental = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        MelodiaTextField(value = tags, onValueChange = { tags = it }, label = "Tags (Optional)")
        Spacer(modifier = Modifier.height(16.dp))
        MelodiaTextField(value = title, onValueChange = { title = it }, label = "Title (Optional)")
        
        Spacer(modifier = Modifier.height(24.dp))
        
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
        Spacer(modifier = Modifier.height(8.dp))
        MelodiaTextField(
            value = lyrics,
            onValueChange = { lyrics = it },
            label = "Enter Lyrics",
            minLines = 5
        )
        Spacer(modifier = Modifier.height(16.dp))
        MelodiaTextField(value = style, onValueChange = { style = it }, label = "Style of Music (e.g. Pop, Rock)")
        Spacer(modifier = Modifier.height(16.dp))
        MelodiaTextField(value = title, onValueChange = { title = it }, label = "Title")
        
        Spacer(modifier = Modifier.height(24.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        
        MelodiaButton(
            text = "Pick Audio File",
            onClick = { /* File Picker logic */ },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        MelodiaTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = "Instructions (e.g. 'Make it jazz')",
            minLines = 2
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        MelodiaButton(
            text = "Upload & Generate",
            onClick = { /* Upload Logic */ },
            enabled = prompt.isNotBlank()
        )
    }
}
