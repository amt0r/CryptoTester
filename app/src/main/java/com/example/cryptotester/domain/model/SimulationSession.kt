package com.example.cryptotester.domain.model

data class SimulationSession(
    val startBalance: Double,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val sessionMultiplier: Double,
    val portfolio: Portfolio
)
