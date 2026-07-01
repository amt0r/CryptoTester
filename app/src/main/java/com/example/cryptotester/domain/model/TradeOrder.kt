package com.example.cryptotester.domain.model

data class TradeOrder(
    val symbol: CryptoSymbol,
    val side: TradeSide,
    val percentage: Double // 0.0 to 1.0
)
