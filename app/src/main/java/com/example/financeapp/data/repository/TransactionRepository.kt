package com.example.financeapp.data.repository

import com.example.financeapp.data.local.TransactionDao
import com.example.financeapp.data.model.TransactionEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val expenseTransactions: Flow<List<TransactionEntity>> = transactionDao.getExpenseTransactions()
    val incomeTransactions: Flow<List<TransactionEntity>> = transactionDao.getIncomeTransactions()
    
    private val firestore = FirebaseFirestore.getInstance()
    private val transactionsCollection = firestore.collection("transactions")

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        
        // Sync to Firestore (Simple implementation: just add)
        val transactionMap = hashMapOf(
            "amount" to transaction.amount,
            "category" to transaction.category,
            "date" to transaction.date,
            "note" to transaction.note,
            "type" to transaction.type,
            "paymentMethod" to transaction.paymentMethod
        )
        try {
            transactionsCollection.add(transactionMap).await()
        } catch (e: Exception) {
            // Handle error or store for later sync
            e.printStackTrace()
        }
    }
}
