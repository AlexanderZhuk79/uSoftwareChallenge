package com.usoftware.challenge.presentation.select_currencies

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel

data class AddAssetState(
    val model : List<CurrencyDBModel>,
    val errorMsg: String? = null
)
