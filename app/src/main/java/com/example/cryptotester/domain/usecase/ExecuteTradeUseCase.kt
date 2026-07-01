package com.example.cryptotester.domain.usecase

import com.example.cryptotester.domain.model.Portfolio
import com.example.cryptotester.domain.model.TradeOrder
import com.example.cryptotester.domain.model.TradeResult
import com.example.cryptotester.domain.model.TradeSide
import javax.inject.Inject

class ExecuteTradeUseCase @Inject constructor() {

    companion object {
        private const val FEE_RATE = 0.001 // 0.1%
    }

    operator fun invoke(
        order: TradeOrder,
        portfolio: Portfolio,
        executionPrice: Double
    ): TradeResult {
        return when (order.side) {
            TradeSide.BUY -> executeBuy(order, portfolio, executionPrice)
            TradeSide.SELL -> executeSell(order, portfolio, executionPrice)
        }
    }

    private fun executeBuy(
        order: TradeOrder,
        portfolio: Portfolio,
        executionPrice: Double
    ): TradeResult {
        val spendableUsdt = portfolio.usdtBalance * order.percentage
        val fee = spendableUsdt * FEE_RATE
        val netUsdt = spendableUsdt - fee
        val cryptoAmount = netUsdt / executionPrice

        val updatedHoldings = portfolio.holdings.toMutableMap()
        updatedHoldings[order.symbol] = (updatedHoldings[order.symbol] ?: 0.0) + cryptoAmount

        val updatedPortfolio = portfolio.copy(
            usdtBalance = portfolio.usdtBalance - spendableUsdt,
            holdings = updatedHoldings
        )

        return TradeResult(
            executionPrice = executionPrice,
            amount = cryptoAmount,
            fee = fee,
            side = TradeSide.BUY,
            symbol = order.symbol,
            updatedPortfolio = updatedPortfolio
        )
    }

    private fun executeSell(
        order: TradeOrder,
        portfolio: Portfolio,
        executionPrice: Double
    ): TradeResult {
        val sellableAmount = portfolio.getHolding(order.symbol) * order.percentage
        val grossUsdt = sellableAmount * executionPrice
        val fee = grossUsdt * FEE_RATE
        val netUsdt = grossUsdt - fee

        val updatedHoldings = portfolio.holdings.toMutableMap()
        updatedHoldings[order.symbol] = (updatedHoldings[order.symbol] ?: 0.0) - sellableAmount

        val updatedPortfolio = portfolio.copy(
            usdtBalance = portfolio.usdtBalance + netUsdt,
            holdings = updatedHoldings
        )

        return TradeResult(
            executionPrice = executionPrice,
            amount = sellableAmount,
            fee = fee,
            side = TradeSide.SELL,
            symbol = order.symbol,
            updatedPortfolio = updatedPortfolio
        )
    }
}
