package com.usoftware.challenge.data.network.network_models

import kotlinx.serialization.Serializable

@Serializable
data class ListResponseModel(
    val currencies: Map<String, String>,
    val privacy: String,
    val success: Boolean,
    val terms: String
)