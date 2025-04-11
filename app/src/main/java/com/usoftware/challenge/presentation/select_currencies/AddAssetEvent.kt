package com.usoftware.challenge.presentation.select_currencies

import com.usoftware.challenge.data.database.database_models.CurrencyDBModel

sealed class AddAssetEvent {
    data class SetCurrencyFavorite(val code: String): AddAssetEvent()
    data class UnSetCurrencyFavorite(val code: String): AddAssetEvent()
    data class GotCurrenciesList(val currencies: List<CurrencyDBModel>): AddAssetEvent()
    data class ShowError(val errorMsg: String): AddAssetEvent()
    object HideError: AddAssetEvent()
}