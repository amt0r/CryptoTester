package com.example.cryptotester.ui.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotester.domain.repository.CandleRepository
import com.example.cryptotester.domain.simulation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StartUiState(
    val balanceInput: String = "200",
    val isLoading: Boolean = false,
    val navigateToSimulator: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StartViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val candleRepository: CandleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartUiState())
    val uiState: StateFlow<StartUiState> = _uiState.asStateFlow()

    fun onBalanceChanged(value: String) {
        // Allow only valid numeric input
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(balanceInput = value, error = null) }
        }
    }

    fun onStartSimulation() {
        val balance = _uiState.value.balanceInput.toDoubleOrNull()
        if (balance == null || balance <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid balance") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val timeRange = candleRepository.getTimeRange()
                if (timeRange.first == 0L && timeRange.second == 0L) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "No historical data available")
                    }
                    return@launch
                }
                sessionManager.startSession(balance, timeRange)
                _uiState.update { it.copy(isLoading = false, navigateToSimulator = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to start: ${e.message}")
                }
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToSimulator = false) }
    }
}
