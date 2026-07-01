package com.example.cryptotester.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cryptotester.data.local.AppDatabase
import com.example.cryptotester.data.local.dao.CandleDao
import com.example.cryptotester.data.local.dao.SimulationResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .createFromAsset(
            "history.db",
            object : RoomDatabase.PrepackagedDatabaseCallback() {
                override fun onOpenPrepackagedDatabase(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """CREATE TABLE IF NOT EXISTS `simulation_results` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `startBalance` REAL NOT NULL,
                            `finalBalance` REAL NOT NULL,
                            `daysElapsed` INTEGER NOT NULL,
                            `pnlPercent` REAL NOT NULL,
                            `timestamp` INTEGER NOT NULL,
                            `startDate` INTEGER NOT NULL,
                            `endDate` INTEGER NOT NULL
                        )"""
                    )
                }
            }
        )
        .build()
    }

    @Provides
    fun provideCandleDao(database: AppDatabase): CandleDao = database.candleDao()

    @Provides
    fun provideSimulationResultDao(database: AppDatabase): SimulationResultDao = database.simulationResultDao()
}
