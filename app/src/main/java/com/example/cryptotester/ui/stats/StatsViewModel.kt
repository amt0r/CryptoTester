package com.example.cryptotester.ui.stats

import androidx.lifecycle.ViewModel
import com.example.cryptotester.domain.model.SimulationResult
import com.example.cryptotester.domain.repository.SimulationResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    simulationResultRepository: SimulationResultRepository
) : ViewModel() {

    val results: Flow<List<SimulationResult>> = simulationResultRepository.getAllResults()
}
