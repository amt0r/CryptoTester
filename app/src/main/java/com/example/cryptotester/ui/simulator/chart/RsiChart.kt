package com.example.cryptotester.ui.simulator.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.cryptotester.ui.theme.ChartGridLine
import com.example.cryptotester.ui.theme.RsiLine
import com.example.cryptotester.ui.theme.RsiOverbought
import com.example.cryptotester.ui.theme.RsiOversold
import com.example.cryptotester.ui.theme.RsiMiddle

@Composable
fun RsiChart(
    rsiValues: List<Double>,
    modifier: Modifier = Modifier
) {
    val priceAxisWidth = 70f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val chartWidth = size.width - priceAxisWidth
        val chartHeight = size.height

        // Filter to only valid (non-NaN) values for display
        val validRsi = rsiValues.filter { !it.isNaN() }
        if (validRsi.isEmpty()) return@Canvas

        val textPaint = android.graphics.Paint().apply {
            color = 0xFF8B949E.toInt()
            textSize = 24f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.LEFT
        }

        // Draw horizontal reference lines at 30, 50, 70
        val levels = listOf(
            30.0 to RsiOversold,
            50.0 to RsiMiddle,
            70.0 to RsiOverbought
        )

        for ((level, color) in levels) {
            val y = ((100.0 - level) / 100.0 * chartHeight).toFloat()

            drawLine(
                color = color.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                strokeWidth = 0.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
            )

            // Level label
            drawContext.canvas.nativeCanvas.drawText(
                level.toInt().toString(),
                chartWidth + 8f,
                y + 8f,
                textPaint
            )
        }

        // Draw RSI line
        if (validRsi.size < 2) return@Canvas

        val path = Path()
        val xStep = chartWidth / (validRsi.size - 1).coerceAtLeast(1)

        validRsi.forEachIndexed { index, rsi ->
            val x = index * xStep
            val y = ((100.0 - rsi.coerceIn(0.0, 100.0)) / 100.0 * chartHeight).toFloat()

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = RsiLine,
            style = Stroke(width = 2f)
        )

        // Draw overbought/oversold zones with subtle fill
        val lastRsi = validRsi.last()
        val lastY = ((100.0 - lastRsi.coerceIn(0.0, 100.0)) / 100.0 * chartHeight).toFloat()
        val lastX = (validRsi.size - 1) * xStep

        // Current RSI dot
        drawCircle(
            color = when {
                lastRsi >= 70 -> RsiOverbought
                lastRsi <= 30 -> RsiOversold
                else -> RsiLine
            },
            radius = 4f,
            center = Offset(lastX, lastY)
        )
    }
}
