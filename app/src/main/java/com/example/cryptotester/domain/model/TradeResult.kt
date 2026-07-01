package com.example.cryptotester.domain.model

data class TradeResult(
    val executionPrice: Double,
    val amount: Double,
    val fee: Double,
    val side: TradeSide,
    val symbol: CryptoSymbol,
    val updatedPortfolio: Portfolio
)
