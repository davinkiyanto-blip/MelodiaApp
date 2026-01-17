package com.xcode.melodia.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xcode.melodia.ui.components.MelodiaHeader

@Composable
fun PrivacyScreen() {
    Scaffold(
        topBar = { MelodiaHeader() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Privacy Policy\n\nWe value your privacy.\n\nData Collection:\nWe collect email and generated content.\n\n[Add full privacy policy here]")
        }
    }
}
