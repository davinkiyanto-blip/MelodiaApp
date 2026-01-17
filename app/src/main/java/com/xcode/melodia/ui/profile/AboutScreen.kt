package com.xcode.melodia.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xcode.melodia.ui.components.MelodiaHeader

@Composable
fun AboutScreen() {
    Scaffold(
        topBar = { MelodiaHeader() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("About Melodia\n\nVersion: 1.0.0\n\nAI Music Generator powered by Suno AI.\n\nDeveloped by Xcode.")
        }
    }
}
