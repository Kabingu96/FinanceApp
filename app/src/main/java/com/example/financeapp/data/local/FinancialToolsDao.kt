package com.example.financeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.data.model.DebtEntity
import com.example.financeapp.data.model.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialToolsDao {
    // Debt Tracker
    @Query("SELECT * FROM debts")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert
    suspend fun insertDebt(debt: DebtEntity)
    
    // Savings Goals
    @Query("SELECT * FROM savings_goals")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>
    
    @Insert
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity)
    
    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)
}
