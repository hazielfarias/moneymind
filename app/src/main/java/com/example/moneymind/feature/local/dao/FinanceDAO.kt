package com.example.moneymind.feature.local.dao

import androidx.room.*
import com.example.moneymind.feature.local.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FinanceDao {

    //ex

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesByUser(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id AND userId = :userId")
    suspend fun getExpenseById(id: Long, userId: String): Expense?

    @Query("SELECT DISTINCT category FROM expenses WHERE userId = :userId")
    fun getExpenseCategories(userId: String): Flow<List<String>>

    // dash

    @Query("SELECT SUM(value) FROM expenses WHERE userId = :userId AND date BETWEEN :start AND :end")
    suspend fun getTotalExpenses(userId: String, start: Date, end: Date): Double?
}