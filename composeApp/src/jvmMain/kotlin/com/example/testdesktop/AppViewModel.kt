package com.example.testdesktop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    Home,
    Table,
    Profile,
    Settings,
    About
}

data class AppUiState(
    val isContentVisible: Boolean = false,
    val greeting: String = "",
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val currentScreen: Screen = Screen.Home,
    val isSidebarVisible: Boolean = true
)

sealed interface AppUiEvent {
    data object ToggleContent : AppUiEvent
    data object LoadData : AppUiEvent
    data class NavigateTo(val screen: Screen) : AppUiEvent
    data object ToggleSidebar : AppUiEvent
}

class AppViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<AppUiState> = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: AppUiEvent): Unit {
        when (event) {
            is AppUiEvent.ToggleContent -> toggleContentVisibility()
            is AppUiEvent.LoadData -> loadInitialData()
            is AppUiEvent.NavigateTo -> navigateTo(event.screen)
            is AppUiEvent.ToggleSidebar -> toggleSidebar()
        }
    }

    private fun navigateTo(screen: Screen): Unit {
        _uiState.update { currentState ->
            currentState.copy(currentScreen = screen)
        }
    }

    private fun toggleContentVisibility(): Unit {
        _uiState.update { currentState ->
            currentState.copy(isContentVisible = !currentState.isContentVisible)
        }
    }

    private fun toggleSidebar(): Unit {
        _uiState.update { currentState ->
            currentState.copy(isSidebarVisible = !currentState.isSidebarVisible)
        }
    }

    private fun loadInitialData(): Unit {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val greeting: String = Greeting().greet()
            val items: List<String> = generateItems()
            _uiState.update { currentState ->
                currentState.copy(
                    greeting = greeting,
                    items = items,
                    isLoading = false
                )
            }
        }
    }

    private fun generateItems(): List<String> {
        return List(50) { index -> "Item ${index + 1}" }
    }
}
