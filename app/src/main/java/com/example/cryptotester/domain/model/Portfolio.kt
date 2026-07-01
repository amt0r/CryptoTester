package com.example.cryptotester.domain.model

data class Portfolio(
    val usdtBalance: Double,
    val holdings: Map<CryptoSymbol, Double> = CryptoSymbol.entries.associateWith { 0.0 }
) {
    fun totalValue(currentPrices: Map<CryptoSymbol, Double>): Double {
        val holdingsValue = holdings.entries.sumOf { (symbol, amount) ->
            amount * (currentPrices[symbol] ?: 0.0)
        }
        return usdtBalance + holdingsValue
    }

    fun getHolding(symbol: CryptoSymbol): Double = holdings[symbol] ?: 0.0
}
