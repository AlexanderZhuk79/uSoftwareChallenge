package com.usoftware.challenge.data.database.database_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyDBModel(
    @PrimaryKey
    val code: String,
    val name: String,
    val favorite: Boolean)