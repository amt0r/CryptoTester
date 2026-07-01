package com.example.cryptotester.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cryptotester.data.local.dao.CandleDao
import com.example.cryptotester.data.local.dao.SimulationResultDao
import com.example.cryptotester.data.local.entity.Candle15mEntity
import com.example.cryptotester.data.local.entity.Candle1hEntity
import com.example.cryptotester.data.local.entity.Candle1dEntity
import com.example.cryptotester.data.local.entity.SimulationResultEntity

@Database(
    entities = [
        Candle15mEntity::class,
        Candle1hEntity::class,
        Candle1dEntity::class,
        SimulationResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun candleDao(): CandleDao
    abstract fun simulationResultDao(): SimulationResultDao

    companion object {
        const val DATABASE_NAME = "crypto_tester.db"
    }
}
