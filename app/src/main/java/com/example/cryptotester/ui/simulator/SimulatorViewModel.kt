package com.example.cryptotester.ui.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotester.domain.model.Candle
import com.example.cryptotester.domain.model.CryptoSymbol
import com.example.cryptotester.domain.model.SimulationResult
import com.example.cryptotester.domain.model.Timeframe
import com.example.cryptotester.domain.model.TradeOrder
import com.example.cryptotester.domain.model.TradeSide
import com.example.cryptotester.domain.repository.CandleRepository
import com.example.cryptotester.domain.repository.SimulationResultRepository
import com.example.cryptotester.domain.simulation.SessionManager
import com.example.cryptotester.domain.simulation.SimulationTimeManager
import com.example.cryptotester.domain.usecase.CalculateRsiUseCase
import com.example.cryptotester.domain.usecase.ExecuteTradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SimulatorUiState(
    val selectedSymbol: CryptoSymbol = CryptoSymbol.BTC,
    val selectedTimeframe: Timeframe = Timeframe.H1,
    val sliderPosition: Float = 0.5f,
    val candles: List<Candle> = emptyList(),
    val obfuscatedCandles: List<Candle> = emptyList(),
    val rsiValues: List<Double> = emptyList(),
    val usdtBalance: Double = 0.0,
    val cryptoBalance: Double = 0.0,
    val sessionMultiplier: Double = 1.0,
    val currentPrice: Double = 0.0,
    val obfuscatedPrice: Double = 0.0,
    val canSell: Boolean = false,
    val canBuy: Boolean = false,
    val showResult: Boolean = false,
    val simulationResult: SimulationResult? = null,
    val tradeMessage: String? = null,
    val isEndReached: Boolean = false
)

private data class UiInputs(
    val symbol: CryptoSymbol,
    val timeframe: Timeframe,
    val slider: Float,
    val candleList: List<Candle>,
    val rsi: List<Double>
)

private data class UiSessionState(
    val session: com.example.cryptotester.domain.model.SimulationSession?,
    val tradeMsg: String?,
    val showRes: Boolean,
    val simResult: SimulationResult?,
    val endReached: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SimulatorViewModel @Inject constructor(
    private val candleRepository: CandleRepository,
    private val simulationResultRepository: SimulationResultRepository,
    private val sessionManager: SessionManager,
    private val timeManager: SimulationTimeManager,
    private val calculateRsiUseCase: CalculateRsiUseCase,
    private val executeTradeUseCase: ExecuteTradeUseCase
) : ViewModel() {

    private val _selectedSymbol = MutableStateFlow(CryptoSymbol.BTC)
    private val _selectedTimeframe = MutableStateFlow(Timeframe.H1)
    private val _sliderPosition = MutableStateFlow(0.5f)
    private val _tradeMessage = MutableStateFlow<String?>(null)
    private val _showResult = MutableStateFlow(false)
    private val _simulationResult = MutableStateFlow<SimulationResult?>(null)
    private val _isEndReached = MutableStateFlow(false)

    // Reactive candle loading: re-fetches when symbol, timeframe, or time changes
    private val candles: StateFlow<List<Candle>> = combine(
        _selectedSymbol,
        _selectedTimeframe,
        timeManager.currentTime
    ) { symbol, timeframe, time ->
        Triple(symbol, timeframe, time)
    }.flatMapLatest { (symbol, timeframe, time) ->
        candleRepository.getCandles(symbol, timeframe, time, 200)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // RSI calculated on background thread using original prices
    private val rsiValues: StateFlow<List<Double>> = candles
        .map { candleList ->
            if (candleList.size > 14) calculateRsiUseCase(candleList) else emptyList()
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<SimulatorUiState> = combine(
        combine(_selectedSymbol, _selectedTimeframe, _sliderPosition, candles, rsiValues) { symbol, timeframe, slider, candleList, rsi ->
            UiInputs(symbol, timeframe, slider, candleList, rsi)
        },
        combine(sessionManager.session, _tradeMessage, _showResult, _simulationResult, _isEndReached) { session, tradeMsg, showRes, simResult, endReached ->
            UiSessionState(session, tradeMsg, showRes, simResult, endReached)
        }
    ) { inputs, sessionState ->
        val multiplier = sessionState.session?.sessionMultiplier ?: 1.0
        val portfolio = sessionState.session?.portfolio
        val usdtBalance = portfolio?.usdtBalance ?: 0.0
        val cryptoBalance = portfolio?.getHolding(inputs.symbol) ?: 0.0

        // Latest real price from 15m candles
        val latestPrice = inputs.candleList.lastOrNull()?.close ?: 0.0
        val obfuscatedPrice = latestPrice * multiplier

        // Obfuscate candle prices for display
        val obfuscatedCandles = inputs.candleList.map { candle ->
            candle.copy(
                open = candle.open * multiplier,
                high = candle.high * multiplier,
                low = candle.low * multiplier,
                close = candle.close * multiplier
            )
        }

        SimulatorUiState(
            selectedSymbol = inputs.symbol,
            selectedTimeframe = inputs.timeframe,
            sliderPosition = inputs.slider,
            candles = inputs.candleList,
            obfuscatedCandles = obfuscatedCandles,
            rsiValues = inputs.rsi,
            usdtBalance = usdtBalance,
            cryptoBalance = cryptoBalance,
            sessionMultiplier = multiplier,
            currentPrice = latestPrice,
            obfuscatedPrice = obfuscatedPrice,
            canSell = cryptoBalance > 0.0,
            canBuy = usdtBalance > 0.0,
            showResult = sessionState.showRes,
            simulationResult = sessionState.simResult,
            tradeMessage = sessionState.tradeMsg,
            isEndReached = sessionState.endReached
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SimulatorUiState())

    fun onSymbolSelected(symbol: CryptoSymbol) {
        _selectedSymbol.value = symbol
    }

    fun onTimeframeSelected(timeframe: Timeframe) {
        _selectedTimeframe.value = timeframe
    }

    fun onSliderChanged(value: Float) {
        _sliderPosition.value = value
    }

    fun onNextCandle() {
        val session = sessionManager.session.value ?: return
        val timeframe = _selectedTimeframe.value

        timeManager.advance(timeframe)

        if (timeManager.hasReachedEnd(session.endTimestamp)) {
            _isEndReached.value = true
            onFinish()
        }
    }

    fun onBuy() {
        viewModelScope.launch {
            val session = sessionManager.session.value ?: return@launch
            val symbol = _selectedSymbol.value
            val percentage = _sliderPosition.value.toDouble()
            if (percentage <= 0.0) return@launch

            // Get execution price from latest 15m candle
            val currentTime = timeManager.currentTime.value
            val latest15m = candleRepository.getLatest15mCandle(symbol, currentTime)
                ?: return@launch
            val executionPrice = latest15m.close

            val order = TradeOrder(symbol, TradeSide.BUY, percentage)
            val result = executeTradeUseCase(order, session.portfolio, executionPrice)

            sessionManager.updatePortfolio(result.updatedPortfolio)

            val obfuscatedExecPrice = executionPrice * session.sessionMultiplier
            _tradeMessage.value = "Bought %.6f %s @ %.2f".format(
                result.amount, symbol.displayName, obfuscatedExecPrice
            )

            // Clear message after a delay
            launch {
                kotlinx.coroutines.delay(3000)
                _tradeMessage.value = null
            }
        }
    }

    fun onSell() {
        viewModelScope.launch {
            val session = sessionManager.session.value ?: return@launch
            val symbol = _selectedSymbol.value
            val percentage = _sliderPosition.value.toDouble()
            if (percentage <= 0.0) return@launch

            val currentTime = timeManager.currentTime.value
            val latest15m = candleRepository.getLatest15mCandle(symbol, currentTime)
                ?: return@launch
            val executionPrice = latest15m.close

            val order = TradeOrder(symbol, TradeSide.SELL, percentage)
            val result = executeTradeUseCase(order, session.portfolio, executionPrice)

            sessionManager.updatePortfolio(result.updatedPortfolio)

            val obfuscatedExecPrice = executionPrice * session.sessionMultiplier
            _tradeMessage.value = "Sold %.6f %s @ %.2f".format(
                result.amount, symbol.displayName, obfuscatedExecPrice
            )

            launch {
                kotlinx.coroutines.delay(3000)
                _tradeMessage.value = null
            }
        }
    }

    fun onFinish() {
        viewModelScope.launch {
            val session = sessionManager.session.value ?: return@launch
            val currentTime = timeManager.currentTime.value

            // Get current prices for all assets to calculate total portfolio value
            val currentPrices = CryptoSymbol.entries.associate { symbol ->
                val candle = candleRepository.getLatest15mCandle(symbol, currentTime)
                symbol to (candle?.close ?: 0.0)
            }

            val result = sessionManager.finishSession(currentPrices) ?: return@launch
            simulationResultRepository.saveResult(result)

            _simulationResult.value = result
            _showResult.value = true
        }
    }

    fun onResultDismissed() {
        _showResult.value = false
        _simulationResult.value = null
    }

    fun clearTradeMessage() {
        _tradeMessage.value = null
    }
}
