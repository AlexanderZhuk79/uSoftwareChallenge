package com.usoftware.challenge.presentation.rate_tracker

import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel

data class RateTrackerState(
    val model: List<RateChangeNameDBModel>,
    val errorMsg: String? = null
)
