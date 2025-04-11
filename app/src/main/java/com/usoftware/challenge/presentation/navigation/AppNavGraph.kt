package com.usoftware.challenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = RateTrackerRoute,
        modifier = Modifier
    ) {
        rateTracker(onAddAssetClick = {
            navController.navigate(AddAssetScreenRoute)
        })

        addAsset(navigateBack = {
            navController.popBackStack()
        })
    }
}