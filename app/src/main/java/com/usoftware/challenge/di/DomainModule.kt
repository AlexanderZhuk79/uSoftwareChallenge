package com.usoftware.challenge.di

import com.usoftware.challenge.data.database.CurrenciesDBDataSource
import com.usoftware.challenge.data.network.CurrenciesRemoteDataSource
import com.usoftware.challenge.domain.CurrenciesRepository
import com.usoftware.challenge.domain.CurrenciesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun getCurrenciesRepository(
        currenciesRemoteDataSource: CurrenciesRemoteDataSource,
        currenciesDBDataSource: CurrenciesDBDataSource
    ): CurrenciesRepository = CurrenciesRepositoryImpl(
        currenciesRemoteDataSource, currenciesDBDataSource
    )


}

