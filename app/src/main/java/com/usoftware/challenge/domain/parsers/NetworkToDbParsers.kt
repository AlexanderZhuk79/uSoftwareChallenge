package com.usoftware.challenge.domain.parsers

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import com.usoftware.challenge.data.database.database_models.RateChangeDBModel
import com.usoftware.challenge.data.network.network_models.ChangeResponseModel
import com.usoftware.challenge.data.network.network_models.ListResponseModel

fun ListResponseModel.toCurrencyDBModel() : List<CurrencyDBModel> =
    this.currencies.entries.map {
        CurrencyDBModel(
            code = it.key,
            name = it.value,
            favorite = false
        )
    }


fun ChangeResponseModel.toRateChangeDBModel() : List<RateChangeDBModel> =
    this.quotes.entries.map {
        RateChangeDBModel(
            code = it.key,
            timestamp = System.currentTimeMillis(),
            change = it.value.change,
            changePct = it.value.changePct,
            endRate = it.value.endRate,
            startRate = it.value.startRate
        )
    }