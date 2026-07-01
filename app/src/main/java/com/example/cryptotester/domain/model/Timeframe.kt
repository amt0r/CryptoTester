package com.example.cryptotester.domain.model

enum class Timeframe(
    val durationMs: Long,
    val label: String,
    val tableName: String
) {
    M15(15 * 60 * 1000L, "15m", "candle_15m"),
    H1(60 * 60 * 1000L, "1h", "candle_1h"),
    D1(24 * 60 * 60 * 1000L, "1D", "candle_1d")
}
