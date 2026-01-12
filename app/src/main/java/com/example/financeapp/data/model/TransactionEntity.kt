package com.example.financeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val date: Long,
    val note: String,
    val type: String, // "INCOME" or "EXPENSE"
    val paymentMethod: String,
    val tags: String = "",
    val isRecurring: Boolean = false,
    val recurringFrequency: String = "" // "WEEKLY", "MONTHLY"
)
