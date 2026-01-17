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
fun TermsScreen() {
    Scaffold(
        topBar = { MelodiaHeader() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Terms of Service\n\n1. Usage\nUse this app responsibly.\n\n2. Content\nYou own your creations.\n\n[Add full terms here]")
        }
    }
}
