# Crypto Trading Simulator — Full Implementation Plan

## Overview

Offline-first Crypto Trading Simulator for Android. Randomly selects a historical period, obfuscates asset prices, and allows the user to simulate spot trading.

## Tech Stack
- Kotlin, KSP, Gradle Version Catalogs
- Jetpack Compose, Jetpack Navigation (type-safe routes)
- Hilt Navigation Compose
- Clean Architecture + MVVM + UDF
- Coroutines + Flow
- Room (pre-populated `history.db`)
- Hilt DI
- Material 3 (always-dark theme)
- Custom Canvas for Candlesticks + RSI

## Build System

| Dependency | Version | Notes |
|------------|---------|-------|
| AGP | 9.2.1 | Built-in Kotlin support |
| Kotlin | 2.2.10 | Via AGP, no standalone plugin needed |
| Compose BOM | 2026.02.01 | |
| Hilt | 2.59.2 | AGP 9 compatible |
| Room | 2.7.1 | |
| KSP | 2.2.10-2.0.2 | |
| Navigation | 2.9.0 | Type-safe routes via kotlinx-serialization |
| kotlinx-serialization | 1.8.1 | |
| minSdk | 36 | User preference |

### Key Build Fixes
- `android.disallowKotlinSourceSets=false` in gradle.properties (KSP + AGP 9 compat)
- Removed `kotlin-android` plugin (AGP 9 built-in Kotlin handles it)
- kotlin-serialization plugin for type-safe Navigation routes

---

## Architecture

```
app/src/main/java/com/example/cryptotester/
├── domain/
│   ├── model/          (9 files: CryptoSymbol, Timeframe, Candle, TradeSide, Portfolio, TradeOrder, TradeResult, SimulationSession, SimulationResult)
│   ├── repository/     (2 files: CandleRepository, SimulationResultRepository)
│   ├── usecase/        (2 files: CalculateRsiUseCase, ExecuteTradeUseCase)
│   └── simulation/     (2 files: SimulationTimeManager, SessionManager)
├── data/
│   ├── local/
│   │   ├── entity/     (2 files: CandleEntity ×3 tables, SimulationResultEntity)
│   │   ├── dao/        (2 files: CandleDao, SimulationResultDao)
│   │   └── AppDatabase.kt
│   ├── mapper/         (EntityMappers.kt)
│   └── repository/     (2 files: CandleRepositoryImpl, SimulationResultRepositoryImpl)
├── di/                 (DatabaseModule.kt, RepositoryModule.kt)
├── ui/
│   ├── navigation/     (Routes.kt, AppNavGraph.kt)
│   ├── start/          (StartViewModel.kt, StartScreen.kt)
│   ├── simulator/
│   │   ├── chart/      (CandlestickChart.kt, RsiChart.kt)
│   │   ├── SimulatorViewModel.kt
│   │   ├── SimulatorScreen.kt
│   │   └── ResultDialog.kt
│   ├── stats/          (StatsViewModel.kt, StatsScreen.kt)
│   └── theme/          (Color.kt, Theme.kt, Type.kt)
├── CryptoTesterApp.kt
└── MainActivity.kt
```

---

## Key Design Decisions

1. **Three separate Room entities** per candle table (`Candle15mEntity`, `Candle1hEntity`, `Candle1dEntity`) — compile-time SQL validation
2. **`PrepackagedDatabaseCallback`** — creates `simulation_results` table inside pre-populated asset DB before Room validates schema
3. **Price obfuscation** — `sessionMultiplier` (0.1–10.0) applied only at UI layer in `SimulatorViewModel`
4. **Execution price** — always from latest 15m candle's close, regardless of viewed timeframe
5. **RSI on original prices** — `CalculateRsiUseCase` uses unobfuscated data, computed on `Dispatchers.Default`
6. **Nested `combine()`** — avoids 10-parameter combine (Kotlin 2.1.20+ only), uses 2×5 nested pattern
7. **No future peeking** — all DAO queries enforce `WHERE timestamp <= :upToTimestamp`

---

## Screens

### Start Screen
- Balance input (default 200 USDT)
- "Start Simulation" → validates input, loads DB time range, starts session with random start point
- "My Stats" → navigates to Stats

### Simulator Screen
- **Top bar**: USDT balance, crypto balance, current price, Finish button
- **Asset selector**: BTC / ETH / SOL (color-coded)
- **Timeframe selector**: 15m / 1h / 1D
- **Candlestick chart**: Custom Canvas with pinch-zoom + horizontal pan
- **RSI chart**: Line chart with 30/50/70 reference lines
- **Trading controls**: Amount slider (0–100%), BUY (green) / SELL (red) buttons
- **Next Candle**: Advances virtual time by selected timeframe

### Stats Screen
- LazyColumn of past simulation results
- Shows start→final balance, days elapsed, PnL%
- Empty state with helpful message

### Result Dialog
- Shows PnL%, days traded, start/final balance
- "Back to Home" dismisses and navigates back

---

## Status: ✅ COMPLETE
- BUILD SUCCESSFUL (0 errors, 0 warnings)
- Ready for device deployment and testing
