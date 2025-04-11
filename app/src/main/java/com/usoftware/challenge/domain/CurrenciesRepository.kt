package com.usoftware.challenge.domain

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepository {

    suspend fun requestCurrencies() : Exception?

    suspend fun requestChanges() : Exception?

    suspend fun markCurrencyAsFavourite(code: String)

    suspend fun unMarkCurrencyAsFavourite(code: String)

    fun getAllCurrencies(): Flow<List<CurrencyDBModel>>

    fun getFavoriteRates(): Flow<List<RateChangeNameDBModel>>


}