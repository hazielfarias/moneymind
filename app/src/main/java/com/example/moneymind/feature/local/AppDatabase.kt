package com.example.moneymind.feature.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneymind.feature.local.dao.FinanceDao
import com.example.moneymind.feature.local.model.Expense


@Database(
    entities = [Expense::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao
}