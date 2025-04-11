package com.usoftware.challenge.data.database

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrenciesDBDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
): CurrenciesDBDataSource {
    override fun isCurrenciesEmpty(): Boolean =
        appDatabase.currenciesRatesDAO().getCurrenciesCount() == 0


    override fun getAllCurrencies(): Flow<List<CurrencyDBModel>> =
        appDatabase.currenciesRatesDAO().getAllCurrencies()


    override fun getFavoriteRates(): Flow<List<RateChangeNameDBModel>> =
        appDatabase.currenciesRatesDAO().getFavoriteRates()

    override fun updateFavorite(code: String, favorite: Boolean) =
        appDatabase.currenciesRatesDAO().updateFavorite(code, favorite)

    override fun updateRates(rates: List<RateChangeDBModel>) {
        appDatabase.currenciesRatesDAO().updateRates(rates)
    }

    override fun updateCurrencies(currencies: List<CurrencyDBModel>) {
        appDatabase.currenciesRatesDAO().updateCurrencies(currencies)
    }


}