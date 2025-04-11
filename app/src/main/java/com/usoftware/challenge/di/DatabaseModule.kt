package com.usoftware.challenge.di

import android.app.Application
import androidx.room.Room
import com.usoftware.challenge.BuildConfig
import com.usoftware.challenge.data.database.AppDatabase
import com.usoftware.challenge.data.database.CurrenciesDBDataSource
import com.usoftware.challenge.data.database.CurrenciesDBDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomCoreDatabase(
        application: Application
    ): AppDatabase {
        var builder = Room
            .databaseBuilder(application, AppDatabase::class.java, "AppDatabase.db")
            .fallbackToDestructiveMigration()

        if (BuildConfig.DEBUG) builder = builder.setQueryCallback({ sqlQuery, bindArgs ->
            println("SQL Query: $sqlQuery SQL Args: $bindArgs")
        }, Executors.newSingleThreadExecutor())

        return builder.build()
    }

    @Provides
    @Singleton
    fun getCurrenciesDBDataSource(appDatabase: AppDatabase): CurrenciesDBDataSource =
        CurrenciesDBDataSourceImpl(appDatabase)

}