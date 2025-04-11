package com.usoftware.challenge.data.network.network_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeResponseModel(
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val change: Boolean,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
    val source: String,
    val quotes: Map<String, RateChangeModel>
)
