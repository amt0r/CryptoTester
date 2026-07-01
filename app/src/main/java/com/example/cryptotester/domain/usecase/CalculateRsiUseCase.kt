package com.example.cryptotester.domain.usecase

import com.example.cryptotester.domain.model.Candle
import javax.inject.Inject

class CalculateRsiUseCase @Inject constructor() {

    companion object {
        private const val PERIOD = 14
    }

    operator fun invoke(candles: List<Candle>): List<Double> {
        val size = candles.size
        if (size <= PERIOD) {
            return List(size) { Double.NaN }
        }

        val result = MutableList(size) { Double.NaN }

        // Step 1: Calculate price changes
        val changes = DoubleArray(size)
        for (i in 1 until size) {
            changes[i] = candles[i].close - candles[i - 1].close
        }

        // Step 2: Separate gains and losses
        val gains = DoubleArray(size)
        val losses = DoubleArray(size)
        for (i in 1 until size) {
            gains[i] = maxOf(changes[i], 0.0)
            losses[i] = maxOf(-changes[i], 0.0)
        }

        // Step 3: First average gain and loss (simple average of first PERIOD values)
        var avgGain = 0.0
        var avgLoss = 0.0
        for (i in 1..PERIOD) {
            avgGain += gains[i]
            avgLoss += losses[i]
        }
        avgGain /= PERIOD
        avgLoss /= PERIOD

        // Step 4: RSI at index PERIOD
        result[PERIOD] = if (avgLoss == 0.0) {
            100.0
        } else {
            100.0 - 100.0 / (1.0 + avgGain / avgLoss)
        }

        // Step 5: Wilder's smoothing for subsequent values
        for (i in (PERIOD + 1) until size) {
            avgGain = (avgGain * (PERIOD - 1) + gains[i]) / PERIOD
            avgLoss = (avgLoss * (PERIOD - 1) + losses[i]) / PERIOD

            result[i] = if (avgLoss == 0.0) {
                100.0
            } else {
                100.0 - 100.0 / (1.0 + avgGain / avgLoss)
            }
        }

        return result
    }
}
