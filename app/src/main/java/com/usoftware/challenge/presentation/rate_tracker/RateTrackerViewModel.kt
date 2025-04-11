package com.usoftware.challenge.presentation.rate_tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usoftware.challenge.domain.CurrenciesRepository
import com.usoftware.challenge.presentation.rate_tracker.RateTrackerEvent.*
import com.usoftware.challenge.presentation.utils.StateReducerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateTrackerViewModel @Inject constructor(
    private val currenciesRepository: CurrenciesRepository
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currenciesRepository.getFavoriteRates()
                .collect { it-> state.handleEvent(ChangesCurrency(it))}
        }
    }

    val state = StateReducerFlow(
        initialState = RateTrackerState(emptyList()),
        reduceState = ::reduceState,
    )

    private fun reduceState(
        currentState: RateTrackerState,
        event: RateTrackerEvent
    ): RateTrackerState {
        return when (event) {
            is DismissCurrency -> {
                viewModelScope.launch(Dispatchers.IO) {
                    currenciesRepository.unMarkCurrencyAsFavourite(event.code)
                }
                currentState
            }

            is ChangesCurrency -> {
                currentState.copy(model = event.model)
            }

            is RequestRates -> {
                viewModelScope.launch(Dispatchers.IO) {
                    currenciesRepository.requestChanges()?.let{
                        state.handleEvent(ShowError(it.message?:"Unknown error"))
                    }
                }
                currentState
            }

            is RestoreCurrency -> {
                viewModelScope.launch(Dispatchers.IO) {
                    currenciesRepository.markCurrencyAsFavourite(event.code)
                }
                currentState
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