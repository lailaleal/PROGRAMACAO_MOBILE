package com.example.financas.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("dashboard") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("dashboard") {
            TelaFinancas()
        }
    }
}