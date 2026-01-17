package com.xcode.melodia.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import com.xcode.melodia.R
import com.xcode.melodia.di.ServiceLocator
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Dark Background
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Melodia Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
        )
    }

    LaunchedEffect(true) {
        delay(2000) // Fake splash delay
        val user = ServiceLocator.firebaseRepository.currentUser
        if (user != null) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }
}
