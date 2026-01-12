package com.example.financeapp.data.repository

import com.example.financeapp.data.local.FinancialToolsDao
import com.example.financeapp.data.model.DebtEntity
import com.example.financeapp.data.model.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

class FinancialToolsRepository(private val financialToolsDao: FinancialToolsDao) {
    val allDebts: Flow<List<DebtEntity>> = financialToolsDao.getAllDebts()
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = financialToolsDao.getAllSavingsGoals()

    suspend fun insertDebt(debt: DebtEntity) {
        financialToolsDao.insertDebt(debt)
    }

    suspend fun insertSavingsGoal(goal: SavingsGoalEntity) {
        financialToolsDao.insertSavingsGoal(goal)
    }
    
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) {
        financialToolsDao.updateSavingsGoal(goal)
    }
}
