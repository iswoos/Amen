package com.example.amen.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.amen.presentation.ui.home.HomeScreen

@Composable
fun AmenNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRoutine = { navController.navigate(Screen.DailyRoutine.route) },
                onNavigateToRelax = { navController.navigate(Screen.Relax.route) },
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
            )
        }
        
        composable(Screen.DailyRoutine.route) {
            com.example.amen.presentation.ui.routine.DailyRoutineScreen()
        }
        
        composable(Screen.Relax.route) {
            com.example.amen.presentation.ui.relax.RelaxScreen()
        }
        
        composable(Screen.Journal.route) {
            com.example.amen.presentation.ui.journal.JournalScreen()
        }
        
        composable(Screen.Tracker.route) {
            com.example.amen.presentation.ui.tracker.TrackerScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DailyRoutine : Screen("daily_routine")
    object Relax : Screen("relax")
    object Journal : Screen("journal")
    object Tracker : Screen("tracker")
}
