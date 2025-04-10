package com.example.moneymind.feature.repository

import com.example.moneymind.feature.local.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.*

interface FinanceRepository {
    // exp
    suspend fun addExpense(expense: Expense): Result<Long>
    fun getExpenses(userId: String): Flow<List<Expense>>
    suspend fun getExpense(id: Long, userId: String): Result<Expense>
    suspend fun updateExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expense: Expense): Result<Unit>
    fun getExpenseCategories(userId: String): Flow<List<String>>

    // dash cons
    suspend fun getTotalExpenses(userId: String, start: Date, end: Date): Result<Double>

}