package com.usoftware.challenge.data.database.database_models

data class RateChangeNameDBModel(
        val code: String,
        val name: String,
        val timestamp: Long = System.currentTimeMillis(),
        val change: Double,
        val changePct: Double,
        val endRate: Double,
        val startRate: Double
    )

