package com.example.cryptotester.ui.simulator.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.cryptotester.domain.model.Candle
import com.example.cryptotester.ui.theme.ChartGridLine
import com.example.cryptotester.ui.theme.CryptoGreen
import com.example.cryptotester.ui.theme.CryptoRed
import com.example.cryptotester.ui.theme.TextSecondary

@Composable
fun CandlestickChart(
    candles: List<Candle>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) return

    // Visible candle range — controlled by pan & zoom
    var visibleCount by remember { mutableIntStateOf(60.coerceAtMost(candles.size)) }
    var scrollOffset by remember { mutableIntStateOf(0) }

    // Clamp values whenever candles change
    val clampedVisibleCount = visibleCount.coerceIn(10, candles.size)
    val maxOffset = (candles.size - clampedVisibleCount).coerceAtLeast(0)
    val clampedOffset = scrollOffset.coerceIn(0, maxOffset)

    val visibleCandles = candles.subList(
        (candles.size - clampedVisibleCount - clampedOffset).coerceAtLeast(0),
        (candles.size - clampedOffset).coerceAtMost(candles.size)
    )

    val priceAxisWidth = 70f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .pointerInput(candles.size) {
                detectTransformGestures { _, _, zoom, _ ->
                    val newCount = (visibleCount / zoom).toInt().coerceIn(10, candles.size)
                    visibleCount = newCount
                    // Re-clamp offset
                    val newMax = (candles.size - newCount).coerceAtLeast(0)
                    scrollOffset = scrollOffset.coerceIn(0, newMax)
                }
            }
            .pointerInput(candles.size) {
                detectHorizontalDragGestures { _, dragAmount ->
                    val candleWidth = (size.width - priceAxisWidth) / clampedVisibleCount
                    val candleShift = (-dragAmount / candleWidth).toInt()
                    val newOffset = (scrollOffset + candleShift).coerceIn(
                        0,
                        (candles.size - clampedVisibleCount).coerceAtLeast(0)
                    )
                    scrollOffset = newOffset
                }
            }
    ) {
        if (visibleCandles.isEmpty()) return@Canvas

        val chartWidth = size.width - priceAxisWidth
        val chartHeight = size.height

        val highestPrice = visibleCandles.maxOf { it.high }
        val lowestPrice = visibleCandles.minOf { it.low }
        val priceRange = (highestPrice - lowestPrice).let { if (it == 0.0) 1.0 else it }
        val padding = priceRange * 0.05
        val adjustedHigh = highestPrice + padding
        val adjustedLow = lowestPrice - padding
        val adjustedRange = adjustedHigh - adjustedLow

        // Draw grid lines
        drawPriceGrid(chartWidth, chartHeight, adjustedHigh, adjustedLow, adjustedRange, priceAxisWidth)

        // Draw candles
        val candleWidth = chartWidth / visibleCandles.size
        val bodyWidthRatio = 0.6f

        visibleCandles.forEachIndexed { index, candle ->
            val centerX = index * candleWidth + candleWidth / 2f
            val isGreen = candle.close >= candle.open

            val color = if (isGreen) CryptoGreen else CryptoRed

            val wickTopY = ((adjustedHigh - candle.high) / adjustedRange * chartHeight).toFloat()
            val wickBottomY = ((adjustedHigh - candle.low) / adjustedRange * chartHeight).toFloat()

            // Wick (thin line)
            drawLine(
                color = color,
                start = Offset(centerX, wickTopY),
                end = Offset(centerX, wickBottomY),
                strokeWidth = 1.5f
            )

            // Body (rectangle)
            val bodyTop = ((adjustedHigh - maxOf(candle.open, candle.close)) / adjustedRange * chartHeight).toFloat()
            val bodyBottom = ((adjustedHigh - minOf(candle.open, candle.close)) / adjustedRange * chartHeight).toFloat()
            val bodyHeight = (bodyBottom - bodyTop).coerceAtLeast(1f)
            val bodyWidth = candleWidth * bodyWidthRatio

            drawRect(
                color = color,
                topLeft = Offset(centerX - bodyWidth / 2f, bodyTop),
                size = Size(bodyWidth, bodyHeight)
            )
        }
    }
}

private fun DrawScope.drawPriceGrid(
    chartWidth: Float,
    chartHeight: Float,
    highPrice: Double,
    lowPrice: Double,
    priceRange: Double,
    priceAxisWidth: Float
) {
    val gridLines = 5
    val textPaint = android.graphics.Paint().apply {
        color = 0xFF8B949E.toInt()
        textSize = 28f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.LEFT
    }

    for (i in 0..gridLines) {
        val y = chartHeight * i / gridLines
        val price = highPrice - priceRange * i / gridLines

        drawLine(
            color = ChartGridLine,
            start = Offset(0f, y),
            end = Offset(chartWidth, y),
            strokeWidth = 0.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        )

        // Price label
        drawContext.canvas.nativeCanvas.drawText(
            formatPrice(price),
            chartWidth + 8f,
            y + 10f,
            textPaint
        )
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000 -> "%.0f".format(price)
        price >= 1 -> "%.2f".format(price)
        price >= 0.01 -> "%.4f".format(price)
        else -> "%.6f".format(price)
    }
}
