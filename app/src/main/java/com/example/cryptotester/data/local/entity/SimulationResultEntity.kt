package com.example.cryptotester.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulation_results")
data class SimulationResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startBalance: Double,
    val finalBalance: Double,
    val daysElapsed: Int,
    val pnlPercent: Double,
    val timestamp: Long,
    val startDate: Long,
    val endDate: Long
)
