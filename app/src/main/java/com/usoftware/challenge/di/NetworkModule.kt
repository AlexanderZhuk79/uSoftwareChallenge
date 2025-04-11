package com.usoftware.challenge.di

import android.util.Log
import com.usoftware.challenge.BuildConfig
import com.usoftware.challenge.data.network.CurrenciesRemoteDataSource
import com.usoftware.challenge.data.network.CurrenciesRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RestClient


    @Singleton
    @Provides
    @RestClient
    fun httpClient(): HttpClient =
        HttpClient(Android).config {
            expectSuccess = true

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.exchangerate.host"
                    parameters.append("access_key", BuildConfig.API_KEY)
                }
                contentType(Json)
                accept(Json)

            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("HttpClient", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    })
            }
        }

    @Singleton
    @Provides
    fun getCurrenciesDataSource(@RestClient httpClient: HttpClient): CurrenciesRemoteDataSource =
        CurrenciesRemoteDataSourceImpl(httpClient)

}