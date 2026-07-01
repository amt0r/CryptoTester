package com.example.cryptotester.data.mapper

import com.example.cryptotester.data.local.entity.Candle15mEntity
import com.example.cryptotester.data.local.entity.Candle1hEntity
import com.example.cryptotester.data.local.entity.Candle1dEntity
import com.example.cryptotester.data.local.entity.SimulationResultEntity
import com.example.cryptotester.domain.model.Candle
import com.example.cryptotester.domain.model.CryptoSymbol
import com.example.cryptotester.domain.model.SimulationResult

private fun String.toCryptoSymbol(): CryptoSymbol =
    CryptoSymbol.entries.first { it.raw == this }

fun Candle15mEntity.toDomain(): Candle = Candle(
    symbol = symbol.toCryptoSymbol(),
    timestamp = timestamp,
    open = open,
    high = high,
    low = low,
    close = close
)

fun Candle1hEntity.toDomain(): Candle = Candle(
    symbol = symbol.toCryptoSymbol(),
    timestamp = timestamp,
    open = open,
    high = high,
    low = low,
    close = close
)

fun Candle1dEntity.toDomain(): Candle = Candle(
    symbol = symbol.toCryptoSymbol(),
    timestamp = timestamp,
    open = open,
    high = high,
    low = low,
    close = close
)

fun SimulationResultEntity.toDomain(): SimulationResult = SimulationResult(
    id = id,
    startBalance = startBalance,
    finalBalance = finalBalance,
    daysElapsed = daysElapsed,
    pnlPercent = pnlPercent,
    timestamp = timestamp,
    startDate = startDate,
    endDate = endDate
)

fun SimulationResult.toEntity(): SimulationResultEntity = SimulationResultEntity(
    id = if (id == 0L) 0 else id,
    startBalance = startBalance,
    finalBalance = finalBalance,
    daysElapsed = daysElapsed,
    pnlPercent = pnlPercent,
    timestamp = timestamp,
    startDate = startDate,
    endDate = endDate
)
