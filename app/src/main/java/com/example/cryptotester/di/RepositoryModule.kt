package com.example.cryptotester.di

import com.example.cryptotester.data.repository.CandleRepositoryImpl
import com.example.cryptotester.data.repository.SimulationResultRepositoryImpl
import com.example.cryptotester.domain.repository.CandleRepository
import com.example.cryptotester.domain.repository.SimulationResultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCandleRepository(
        impl: CandleRepositoryImpl
    ): CandleRepository

    @Binds
    @Singleton
    abstract fun bindSimulationResultRepository(
        impl: SimulationResultRepositoryImpl
    ): SimulationResultRepository
}
