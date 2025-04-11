package com.usoftware.challenge.domain

import com.usoftware.challenge.data.database.CurrenciesDBDataSource
import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import com.usoftware.challenge.data.network.ApiResponse.Failure
import com.usoftware.challenge.data.network.ApiResponse.Success
import com.usoftware.challenge.data.network.CurrenciesRemoteDataSource
import com.usoftware.challenge.domain.parsers.toCurrencyDBModel
import com.usoftware.challenge.domain.parsers.toRateChangeDBModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrenciesRepositoryImpl @Inject constructor(
    private val currenciesRemoteDataSource: CurrenciesRemoteDataSource,
    private val currenciesDBDataSource: CurrenciesDBDataSource
) : CurrenciesRepository {

    override suspend fun requestCurrencies() : Exception?{
        if(currenciesDBDataSource.isCurrenciesEmpty()) {
            val response = currenciesRemoteDataSource.getListCurrencies()
            when (response) {
                is Success -> {
                    currenciesDBDataSource.updateCurrencies(response.data.toCurrencyDBModel())
                }

                is Failure -> {
                    return response.exception
                }
            }
        }
        return null
    }

    override suspend fun requestChanges() : Exception?{
        val response = currenciesRemoteDataSource.getChanges()
        when (response) {
            is Success -> {
                currenciesDBDataSource.updateRates(response.data.toRateChangeDBModel())
            }

            is Failure -> {
                return response.exception
            }
        }
        return null
    }

    override suspend fun markCurrencyAsFavourite(code: String) =
        currenciesDBDataSource.updateFavorite(code, true)

    override suspend fun unMarkCurrencyAsFavourite(code: String) =
        currenciesDBDataSource.updateFavorite(code, false)

    override fun getAllCurrencies(): Flow<List<CurrencyDBModel>> =
        currenciesDBDataSource.getAllCurrencies()

    override fun getFavoriteRates(): Flow<List<RateChangeNameDBModel>> =
        currenciesDBDataSource.getFavoriteRates()


}