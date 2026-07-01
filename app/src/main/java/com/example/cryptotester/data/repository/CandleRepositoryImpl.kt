package com.example.cryptotester.data.repository

import com.example.cryptotester.data.local.dao.CandleDao
import com.example.cryptotester.data.mapper.toDomain
import com.example.cryptotester.domain.model.Candle
import com.example.cryptotester.domain.model.CryptoSymbol
import com.example.cryptotester.domain.model.Timeframe
import com.example.cryptotester.domain.repository.CandleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CandleRepositoryImpl @Inject constructor(
    private val candleDao: CandleDao
) : CandleRepository {

    override fun getCandles(
        symbol: CryptoSymbol,
        timeframe: Timeframe,
        upToTimestamp: Long,
        limit: Int
    ): Flow<List<Candle>> {
        return when (timeframe) {
            Timeframe.M15 -> candleDao.getCandles15m(symbol.raw, upToTimestamp, limit)
                .map { entities -> entities.map { it.toDomain() }.reversed() }
            Timeframe.H1 -> candleDao.getCandles1h(symbol.raw, upToTimestamp, limit)
                .map { entities -> entities.map { it.toDomain() }.reversed() }
            Timeframe.D1 -> candleDao.getCandles1d(symbol.raw, upToTimestamp, limit)
                .map { entities -> entities.map { it.toDomain() }.reversed() }
        }
    }

    override suspend fun getLatest15mCandle(
        symbol: CryptoSymbol,
        upToTimestamp: Long
    ): Candle? {
        return candleDao.getLatest15mCandle(symbol.raw, upToTimestamp)?.toDomain()
    }

    override suspend fun getTimeRange(): Pair<Long, Long> {
        val min = candleDao.getMinTimestamp() ?: 0L
        val max = candleDao.getMaxTimestamp() ?: 0L
        return Pair(min, max)
    }
}
