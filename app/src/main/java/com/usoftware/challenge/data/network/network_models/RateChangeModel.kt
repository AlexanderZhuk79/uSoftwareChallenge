package com.usoftware.challenge.data.network.network_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RateChangeModel(
    val change: Double,
    @SerialName("change_pct")
    val changePct: Double,
    @SerialName("end_rate")
    val endRate: Double,
    @SerialName("start_rate")
    val startRate: Double
)