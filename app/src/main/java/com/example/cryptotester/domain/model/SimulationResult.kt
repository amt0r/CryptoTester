package com.example.cryptotester.domain.model

data class SimulationResult(
    val id: Long = 0,
    val startBalance: Double,
    val finalBalance: Double,
    val daysElapsed: Int,
    val pnlPercent: Double,
    val timestamp: Long,
    val startDate: Long = 0L,
    val endDate: Long = 0L
)
