package com.usoftware.challenge.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeDBModel

@Database(entities = [CurrencyDBModel::class, RateChangeDBModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currenciesRatesDAO(): CurrenciesRatesDAO
}