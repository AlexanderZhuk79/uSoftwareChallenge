package com.usoftware.challenge.presentation.select_currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usoftware.challenge.domain.CurrenciesRepository
import com.usoftware.challenge.presentation.select_currencies.AddAssetEvent.GotCurrenciesList
import com.usoftware.challenge.presentation.select_currencies.AddAssetEvent.HideError
import com.usoftware.challenge.presentation.select_currencies.AddAssetEvent.SetCurrencyFavorite
import com.usoftware.challenge.presentation.select_currencies.AddAssetEvent.ShowError
import com.usoftware.challenge.presentation.select_currencies.AddAssetEvent.UnSetCurrencyFavorite
import com.usoftware.challenge.presentation.utils.StateReducerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val currenciesRepository: CurrenciesRepository
): ViewModel(

) {
    val state = StateReducerFlow(
        initialState = AddAssetState(emptyList()),
        reduceState = ::reduceState,
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currenciesRepository.getAllCurrencies()
                .collect { currenciesList ->
                    state.handleEvent(
                        GotCurrenciesList(currenciesList)
                    )
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            currenciesRepository.requestCurrencies()?.let{
                state.handleEvent(ShowError(it.message?:"Unknown error"))
            }
        }
    }


    private fun reduceState(
        currentState: AddAssetState,
        event: AddAssetEvent
    ): AddAssetState {
        return when (event) {
            is SetCurrencyFavorite -> {
                viewModelScope.launch(Dispatchers.IO) {
                    currenciesRepository.markCurrencyAsFavourite(event.code)
                }
                currentState
            }
            is UnSetCurrencyFavorite -> {
                viewModelScope.launch(Dispatchers.IO)  {
                    currenciesRepository.unMarkCurrencyAsFavourite(event.code)
                }
                currentState
            }

            is GotCurrenciesList -> {
                currentState.copy(model = event.currencies)
            }

            is HideError -> {
                currentState.copy(errorMsg = null)
            }
            is ShowError -> {
                currentState.copy(errorMsg = event.errorMsg)
            }
        }
    }
}