package com.example.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.DebtEntity
import com.example.financeapp.data.model.SavingsGoalEntity
import com.example.financeapp.data.repository.FinancialToolsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinancialToolsViewModel(private val repository: FinancialToolsRepository) : ViewModel() {

    val debts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allSavingsGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.insertDebt(debt)
        }
    }

    fun addSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.insertSavingsGoal(goal)
        }
    }
}

class FinancialToolsViewModelFactory(private val repository: FinancialToolsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinancialToolsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinancialToolsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
