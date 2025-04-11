package com.usoftware.challenge.presentation.rate_tracker

import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel

sealed class RateTrackerEvent {
    object RequestRates : RateTrackerEvent()
    data class DismissCurrency(val code: String): RateTrackerEvent()
    data class ChangesCurrency(val model: List<RateChangeNameDBModel>): RateTrackerEvent()
    data class RestoreCurrency(val code: String): RateTrackerEvent()
    data class ShowError(val errorMsg: String): RateTrackerEvent()
    object HideError: RateTrackerEvent()
}