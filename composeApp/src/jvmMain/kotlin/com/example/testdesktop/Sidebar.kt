package com.example.testdesktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Sidebar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Navigation",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            NavigationItem(
                screen = Screen.Home,
                label = "Home",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
            
            NavigationItem(
                screen = Screen.Table,
                label = "Quản Lý Thuốc",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
            
            NavigationItem(
                screen = Screen.Profile,
                label = "Profile",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
            
            NavigationItem(
                screen = Screen.Settings,
                label = "Settings",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
            
            NavigationItem(
                screen = Screen.About,
                label = "About",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun NavigationItem(
    screen: Screen,
    label: String,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val isSelected = currentScreen == screen
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(screen) },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

