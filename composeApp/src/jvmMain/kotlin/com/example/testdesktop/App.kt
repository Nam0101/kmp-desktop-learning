package com.example.testdesktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(): Unit {
    val viewModel: AppViewModel = remember { AppViewModel() }
    val uiState: AppUiState by viewModel.uiState.collectAsState()
    MaterialTheme {
        AppContent(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    uiState: AppUiState,
    onEvent: (AppUiEvent) -> Unit,
    modifier: Modifier = Modifier
): Unit {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = uiState.isSidebarVisible,
            enter = expandHorizontally(expandFrom = Alignment.Start),
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
        ) {
            Sidebar(
                currentScreen = uiState.currentScreen,
                onNavigate = { screen -> onEvent(AppUiEvent.NavigateTo(screen)) },
                modifier = Modifier.fillMaxHeight()
            )
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Top bar with toggle button
            TopAppBar(
                title = { Text(getScreenTitle(uiState.currentScreen)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AppUiEvent.ToggleSidebar) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Sidebar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            
            // Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.currentScreen) {
                    Screen.Home -> HomeScreen(
                        greeting = uiState.greeting,
                        items = uiState.items,
                        isContentVisible = uiState.isContentVisible,
                        onToggleContent = { onEvent(AppUiEvent.ToggleContent) }
                    )
                    Screen.Table -> TableScreen()
                    Screen.Profile -> ProfileScreen()
                    Screen.Settings -> SettingsScreen()
                    Screen.About -> AboutScreen()
                }
            }
        }
    }
}

@Composable
private fun getScreenTitle(screen: Screen): String {
    return when (screen) {
        Screen.Home -> "Home"
        Screen.Table -> "Quản Lý Thuốc"
        Screen.Profile -> "Profile"
        Screen.Settings -> "Settings"
        Screen.About -> "About"
    }
}
