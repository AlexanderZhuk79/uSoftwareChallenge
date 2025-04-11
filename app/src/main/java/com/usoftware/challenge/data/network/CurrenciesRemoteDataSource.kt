package com.usoftware.challenge.data.network

import com.usoftware.challenge.data.network.network_models.ChangeResponseModel
import com.usoftware.challenge.data.network.network_models.ListResponseModel

interface CurrenciesRemoteDataSource {
    suspend fun getListCurrencies(): ApiResponse<ListResponseModel>


    suspend fun getChanges(): ApiResponse<ChangeResponseModel>


}