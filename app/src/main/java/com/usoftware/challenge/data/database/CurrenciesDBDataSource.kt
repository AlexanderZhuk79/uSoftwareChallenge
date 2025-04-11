package com.usoftware.challenge.data.database

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import kotlinx.coroutines.flow.Flow

interface CurrenciesDBDataSource {

    fun isCurrenciesEmpty(): Boolean
    fun getAllCurrencies(): Flow<List<CurrencyDBModel>>
    fun getFavoriteRates(): Flow<List<RateChangeNameDBModel>>
    fun updateFavorite(code: String, favorite: Boolean)
    fun updateRates(rates: List<RateChangeDBModel>)
    fun updateCurrencies(currencies: List<CurrencyDBModel>)

}