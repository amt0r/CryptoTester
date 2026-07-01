package com.example.cryptotester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cryptotester.ui.simulator.SimulatorScreen
import com.example.cryptotester.ui.start.StartScreen
import com.example.cryptotester.ui.stats.StatsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = StartRoute
    ) {
        composable<StartRoute> {
            StartScreen(
                onNavigateToSimulator = {
                    navController.navigate(SimulatorRoute) {
                        popUpTo(StartRoute) { inclusive = false }
                    }
                },
                onNavigateToStats = {
                    navController.navigate(StatsRoute)
                }
            )
        }

        composable<SimulatorRoute> {
            SimulatorScreen(
                onNavigateBack = {
                    navController.popBackStack(StartRoute, inclusive = false)
                }
            )
        }

        composable<StatsRoute> {
            StatsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
