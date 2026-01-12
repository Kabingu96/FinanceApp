package com.example.financeapp

import android.app.Application
import androidx.room.Room
import com.example.financeapp.data.local.AppDatabase

class FinanceApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "finance_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        .build()
    }
}
