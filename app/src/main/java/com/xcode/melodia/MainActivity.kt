package com.xcode.melodia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xcode.melodia.ui.theme.MelodiaTheme
import com.xcode.melodia.ui.login.LoginScreen
import com.xcode.melodia.ui.splash.SplashScreen
import com.xcode.melodia.ui.main.MainScreen
import com.xcode.melodia.ui.profile.TermsScreen
import com.xcode.melodia.ui.profile.PrivacyScreen
import com.xcode.melodia.ui.profile.AboutScreen
import com.xcode.melodia.di.ServiceLocator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MelodiaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MelodiaApp()
                }
            }
        }
    }
}

@Composable
fun MelodiaApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToTerms = { navController.navigate("terms") },
                onNavigateToPrivacy = { navController.navigate("privacy") }
            )
        }
        composable("main") {
            MainScreen(
                onNavigateToTerms = { navController.navigate("terms") },
                onNavigateToPrivacy = { navController.navigate("privacy") },
                onNavigateToAbout = { navController.navigate("about") },
                onLogout = {
                    ServiceLocator.firebaseRepository.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("terms") { TermsScreen() }
        composable("privacy") { PrivacyScreen() }
        composable("about") { AboutScreen() }
    }
}