package com.xcode.melodia.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xcode.melodia.di.ServiceLocator
import com.xcode.melodia.ui.components.ComingSoonDialog
import com.xcode.melodia.ui.components.MelodiaHeader
import com.xcode.melodia.ui.create.CreateScreen
import com.xcode.melodia.ui.library.LibraryScreen
import com.xcode.melodia.ui.profile.ProfileScreen
import com.xcode.melodia.ui.tools.ToolsScreen
import com.xcode.melodia.ui.home.HomeScreen

@Composable
fun MainScreen(
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(2) } 
    var showComingSoon by remember { mutableStateOf(false) }

    val tabs = listOf(
        NavigationItem("Home", Icons.Filled.Home),
        NavigationItem("Create", Icons.Filled.Add),
        NavigationItem("Library", Icons.Filled.LibraryMusic),
        NavigationItem("Tools", Icons.Filled.Settings),
        NavigationItem("Profile", Icons.Filled.Person)
    )

    if (showComingSoon) {
        ComingSoonDialog(onDismissRequest = { showComingSoon = false })
    }

    Scaffold(
        topBar = { MelodiaHeader() },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface // Dark surface
            ) {
                tabs.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                             selectedIndex = index
                        },
                        icon = { 
                            Icon(
                                item.icon, 
                                contentDescription = item.label,
                                tint = if (isSelected) Color(0xFF6C63FF) else Color.Gray // Melodia Primary
                            ) 
                        },
                        label = { 
                            Text(
                                item.label,
                                color = if (isSelected) Color(0xFF6C63FF) else Color.Gray
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6C63FF),
                            selectedTextColor = Color(0xFF6C63FF),
                            indicatorColor = Color(0xFF2A2A2A), // Dark indicator
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> CreateScreen()
                2 -> LibraryScreen()
                3 -> ToolsScreen()
                4 -> ProfileScreen(onNavigateToTerms, onNavigateToPrivacy, onNavigateToAbout, onLogout)
            }
        }
    }
}



data class NavigationItem(val label: String, val icon: ImageVector)
