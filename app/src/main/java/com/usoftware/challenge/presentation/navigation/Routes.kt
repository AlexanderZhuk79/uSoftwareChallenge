package com.usoftware.challenge.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.usoftware.challenge.presentation.rate_tracker.RateTrackerScreen
import com.usoftware.challenge.presentation.select_currencies.AddAssetScreen
import kotlinx.serialization.Serializable

@Serializable
object RateTrackerRoute

fun NavGraphBuilder.rateTracker(onAddAssetClick: () -> Unit) {
    composable<RateTrackerRoute> {
        RateTrackerScreen(onAddAssetClick = onAddAssetClick)
    }
}

@Serializable
object AddAssetScreenRoute

fun NavGraphBuilder.addAsset(navigateBack: () -> Unit
) {
    composable<AddAssetScreenRoute> {
        AddAssetScreen(navigateBack = navigateBack)
    }
}
