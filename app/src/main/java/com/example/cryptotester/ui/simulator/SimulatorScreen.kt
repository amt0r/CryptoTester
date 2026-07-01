package com.example.cryptotester.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptotester.domain.model.CryptoSymbol
import com.example.cryptotester.domain.model.Timeframe
import com.example.cryptotester.ui.simulator.chart.CandlestickChart
import com.example.cryptotester.ui.simulator.chart.RsiChart
import com.example.cryptotester.ui.theme.AccentBlue
import com.example.cryptotester.ui.theme.BtcColor
import com.example.cryptotester.ui.theme.CryptoGreen
import com.example.cryptotester.ui.theme.CryptoGreenSurface
import com.example.cryptotester.ui.theme.CryptoRed
import com.example.cryptotester.ui.theme.CryptoRedSurface
import com.example.cryptotester.ui.theme.DarkBackground
import com.example.cryptotester.ui.theme.DarkCard
import com.example.cryptotester.ui.theme.DarkSurface
import com.example.cryptotester.ui.theme.DarkSurfaceVariant
import com.example.cryptotester.ui.theme.EthColor
import com.example.cryptotester.ui.theme.SolColor
import com.example.cryptotester.ui.theme.TextPrimary
import com.example.cryptotester.ui.theme.TextSecondary
import com.example.cryptotester.ui.theme.TextTertiary

@Composable
fun SimulatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: SimulatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show result dialog
    if (uiState.showResult && uiState.simulationResult != null) {
        ResultDialog(
            result = uiState.simulationResult!!,
            onDismiss = {
                viewModel.onResultDismissed()
                onNavigateBack()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar — with status bar inset
            TopBar(
                usdtBalance = uiState.usdtBalance,
                cryptoBalance = uiState.cryptoBalance,
                cryptoSymbol = uiState.selectedSymbol,
                obfuscatedPrice = uiState.obfuscatedPrice,
                onFinish = viewModel::onFinish
            )

            // Asset Selector
            AssetSelector(
                selected = uiState.selectedSymbol,
                onSelect = viewModel::onSymbolSelected
            )

            // Timeframe Selector
            TimeframeSelector(
                selected = uiState.selectedTimeframe,
                onSelect = viewModel::onTimeframeSelected
            )

            // Candlestick Chart
            CandlestickChart(
                candles = uiState.obfuscatedCandles,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // RSI Chart
            Text(
                text = "RSI (14)",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
            RsiChart(
                rsiValues = uiState.rsiValues,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Trade message
            if (uiState.tradeMessage != null) {
                Text(
                    text = uiState.tradeMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Trading Controls
            TradingControls(
                sliderPosition = uiState.sliderPosition,
                onSliderChanged = viewModel::onSliderChanged,
                canBuy = uiState.canBuy,
                canSell = uiState.canSell,
                onBuy = viewModel::onBuy,
                onSell = viewModel::onSell
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sticky bottom — Next Candle Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackground)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = viewModel::onNextCandle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = DarkBackground
                )
            ) {
                Text(
                    text = "Next Candle  ▶",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    usdtBalance: Double,
    cryptoBalance: Double,
    cryptoSymbol: CryptoSymbol,
    obfuscatedPrice: Double,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "%.2f USDT".format(usdtBalance),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "%.6f %s".format(cryptoBalance, cryptoSymbol.displayName),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Price: %.2f".format(obfuscatedPrice),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        TextButton(
            onClick = onFinish,
            colors = ButtonDefaults.textButtonColors(contentColor = CryptoRed)
        ) {
            Text(
                text = "Finish",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AssetSelector(
    selected: CryptoSymbol,
    onSelect: (CryptoSymbol) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val assets = listOf(
            CryptoSymbol.BTC to BtcColor,
            CryptoSymbol.ETH to EthColor,
            CryptoSymbol.SOL to SolColor
        )

        assets.forEach { (symbol, color) ->
            val isSelected = selected == symbol
            Button(
                onClick = { onSelect(symbol) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    contentColor = if (isSelected) color else TextSecondary
                )
            ) {
                Text(
                    text = symbol.displayName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun TimeframeSelector(
    selected: Timeframe,
    onSelect: (Timeframe) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Timeframe.entries.forEach { tf ->
            val isSelected = selected == tf
            Button(
                onClick = { onSelect(tf) },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) AccentBlue.copy(alpha = 0.15f) else DarkSurfaceVariant,
                    contentColor = if (isSelected) AccentBlue else TextSecondary
                )
            ) {
                Text(
                    text = tf.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun TradingControls(
    sliderPosition: Float,
    onSliderChanged: (Float) -> Unit,
    canBuy: Boolean,
    canSell: Boolean,
    onBuy: () -> Unit,
    onSell: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        // Slider with percentage label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Amount",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = "${(sliderPosition * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = AccentBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = sliderPosition,
            onValueChange = onSliderChanged,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = AccentBlue,
                activeTrackColor = AccentBlue,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Buy / Sell buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBuy,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = canBuy && sliderPosition > 0f,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptoGreen,
                    contentColor = DarkBackground,
                    disabledContainerColor = CryptoGreenSurface,
                    disabledContentColor = TextTertiary
                )
            ) {
                Text(
                    text = "BUY",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = onSell,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = canSell && sliderPosition > 0f,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptoRed,
                    contentColor = DarkBackground,
                    disabledContainerColor = CryptoRedSurface,
                    disabledContentColor = TextTertiary
                )
            ) {
                Text(
                    text = "SELL",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
