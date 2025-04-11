package com.usoftware.challenge.data.database.database_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RateChangeDBModel(
    @PrimaryKey
    val code: String,
    val timestamp: Long = System.currentTimeMillis(),
    val change: Double,
    val changePct: Double,
    val endRate: Double,
    val startRate: Double
)
