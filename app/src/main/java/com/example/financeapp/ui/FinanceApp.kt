package com.example.financeapp.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.AddTransactionScreen
import com.example.financeapp.ui.screens.BudgetsScreen
import com.example.financeapp.ui.screens.HomeScreen
import com.example.financeapp.ui.screens.ReportsScreen
import com.example.financeapp.ui.screens.SettingsScreen
import com.example.financeapp.ui.screens.ToolsScreen
import com.example.financeapp.ui.viewmodel.FinancialToolsViewModel
import com.example.financeapp.ui.viewmodel.SettingsViewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Reports : Screen("reports", "Reports", Icons.Default.DateRange)
    object Budgets : Screen("budgets", "Budgets", Icons.Default.AccountCircle)
    object Tools : Screen("tools", "Tools", Icons.Default.Build)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AddTransaction : Screen("add_transaction", "Add Transaction", Icons.Default.Add)
}

@Composable
fun FinanceApp(
    viewModel: TransactionViewModel = viewModel(),
    financialToolsViewModel: FinancialToolsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Reports, Screen.Budgets, Screen.Tools, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel) }
            composable(Screen.Reports.route) { ReportsScreen(viewModel) }
            composable(Screen.Budgets.route) { BudgetsScreen(viewModel) }
            composable(Screen.Tools.route) { ToolsScreen(financialToolsViewModel) }
            composable(Screen.Settings.route) { SettingsScreen(settingsViewModel) }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        }
    }
}
