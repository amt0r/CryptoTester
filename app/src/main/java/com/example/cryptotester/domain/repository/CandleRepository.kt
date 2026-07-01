package com.example.cryptotester.domain.repository

import com.example.cryptotester.domain.model.Candle
import com.example.cryptotester.domain.model.CryptoSymbol
import com.example.cryptotester.domain.model.Timeframe
import kotlinx.coroutines.flow.Flow

interface CandleRepository {
    fun getCandles(
        symbol: CryptoSymbol,
        timeframe: Timeframe,
        upToTimestamp: Long,
        limit: Int = 200
    ): Flow<List<Candle>>

    suspend fun getLatest15mCandle(
        symbol: CryptoSymbol,
        upToTimestamp: Long
    ): Candle?

    suspend fun getTimeRange(): Pair<Long, Long>
}
