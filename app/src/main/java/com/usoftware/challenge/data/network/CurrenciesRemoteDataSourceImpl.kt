package com.usoftware.challenge.data.network

import com.usoftware.challenge.data.network.network_models.ChangeResponseModel
import com.usoftware.challenge.data.network.network_models.ListResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class CurrenciesRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient
) : CurrenciesRemoteDataSource {

    override suspend fun getListCurrencies(): ApiResponse<ListResponseModel> =
        apiCall {
            httpClient.get("list").body()
        }


    override suspend fun getChanges(): ApiResponse<ChangeResponseModel> =
        apiCall {
            httpClient.get("change").body()
        }

}