package com.example.cryptotester.domain.model

data class Candle(
    val symbol: CryptoSymbol,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)
