package com.example.cryptotester.data.local.entity

import androidx.room.Entity

@Entity(tableName = "candle_15m", primaryKeys = ["symbol", "timestamp"])
data class Candle15mEntity(
    val symbol: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)

@Entity(tableName = "candle_1h", primaryKeys = ["symbol", "timestamp"])
data class Candle1hEntity(
    val symbol: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)

@Entity(tableName = "candle_1d", primaryKeys = ["symbol", "timestamp"])
data class Candle1dEntity(
    val symbol: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)
