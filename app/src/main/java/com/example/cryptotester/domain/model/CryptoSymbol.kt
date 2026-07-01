package com.example.cryptotester.domain.model

enum class CryptoSymbol(val raw: String, val displayName: String) {
    BTC("BTCUSDT", "BTC"),
    ETH("ETHUSDT", "ETH"),
    SOL("SOLUSDT", "SOL")
}
