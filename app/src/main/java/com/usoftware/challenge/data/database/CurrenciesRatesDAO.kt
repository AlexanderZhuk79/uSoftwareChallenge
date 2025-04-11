package com.usoftware.challenge.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrenciesRatesDAO {

    @Query("SELECT * FROM CurrencyDBModel")
    fun getAllCurrencies(): Flow<List<CurrencyDBModel>>

    @Query("SELECT count(*) FROM CurrencyDBModel")
    fun getCurrenciesCount(): Int


    @Query("SELECT substr(r.code, 4,3) code, r.timestamp, r.change, r.changePct, " +
            "r.endRate, r.startRate, c.name " +
            "FROM RateChangeDBModel r, CurrencyDBModel c " +
            "WHERE substr(r.code, 4,3) == c.code AND c.favorite = 1"
    )
    fun getFavoriteRates(): Flow<List<RateChangeNameDBModel>>


    @Query("UPDATE CurrencyDBModel SET favorite = :favorite WHERE code = :code")
    fun updateFavorite(code: String, favorite: Boolean)

    @Insert
    fun insertRates(rates: List<RateChangeDBModel>)

    @Query("DELETE FROM RateChangeDBModel")
    fun deleteRates()

    @Transaction
    fun updateRates(rates: List<RateChangeDBModel>){
        deleteRates()
        insertRates(rates)
    }

    @Upsert
    fun updateCurrencies(currencies: List<CurrencyDBModel>)

}