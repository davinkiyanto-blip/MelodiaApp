package com.xcode.melodia.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xcode.melodia.ui.components.MelodiaHeader
import com.xcode.melodia.ui.components.SectionTitle
import com.xcode.melodia.ui.components.SectionBody
import com.xcode.melodia.ui.theme.MelodiaBackgroundGradient

@Composable
fun PrivacyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MelodiaBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Last updated: January 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("1. Data Collection")
                SectionBody("We collect information you provide directly to us, such as when you create an account, request customer support, or communicate with us. This includes your email address and profile information.")

                SectionTitle("2. Usage Data")
                SectionBody("We automatically collect certain information when you access the Services, including your IP address, device type, and usage patterns to improve our service.")

                SectionTitle("3. Music Generation Data")
                SectionBody("Prompts and lyrics you enter are processed by our AI models to generate music. These inputs are stored to provide you with your history.")

                SectionTitle("4. Data Security")
                SectionBody("We implement reasonable security measures to protect your personal information. However, no method of transmission over the Internet is 100% secure.")

                Spacer(modifier = Modifier.height(32.dp))
            }
    }
}


