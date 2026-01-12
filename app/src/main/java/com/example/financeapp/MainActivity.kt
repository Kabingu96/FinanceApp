package com.example.financeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.local.UserPreferencesRepository
import com.example.financeapp.data.local.dataStore
import com.example.financeapp.data.repository.FinancialToolsRepository
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.ui.FinanceApp
import com.example.financeapp.ui.screens.AuthScreen
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.example.financeapp.ui.viewmodel.FinancialToolsViewModel
import com.example.financeapp.ui.viewmodel.FinancialToolsViewModelFactory
import com.example.financeapp.ui.viewmodel.SettingsViewModel
import com.example.financeapp.ui.viewmodel.SettingsViewModelFactory
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModelFactory

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = (application as FinanceApplication).database
        val transactionRepository = TransactionRepository(database.transactionDao())
        val financialToolsRepository = FinancialToolsRepository(database.financialToolsDao())
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            // Observe Settings
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(userPreferencesRepository)
            )
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val useDynamicColors by settingsViewModel.useDynamicColors.collectAsState()
            
            // Determine actual theme mode
            val darkTheme = isDarkMode // Simplification: In a real app, handle "System Default" vs "Force Dark"

            FinanceAppTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicColors
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isAuthenticated by remember { mutableStateOf(false) }

                    if (isAuthenticated) {
                        val transactionViewModel: TransactionViewModel = viewModel(
                            factory = TransactionViewModelFactory(transactionRepository)
                        )
                        val financialToolsViewModel: FinancialToolsViewModel = viewModel(
                            factory = FinancialToolsViewModelFactory(financialToolsRepository)
                        )
                        FinanceApp(transactionViewModel, financialToolsViewModel, settingsViewModel)
                    } else {
                        AuthScreen(onAuthSuccess = { isAuthenticated = true })
                    }
                }
            }
        }
    }
}
