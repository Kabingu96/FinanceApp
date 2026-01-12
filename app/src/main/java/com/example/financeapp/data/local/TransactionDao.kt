package com.example.financeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.financeapp.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE'")
    fun getExpenseTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'INCOME'")
    fun getIncomeTransactions(): Flow<List<TransactionEntity>>
}
