package com.example.moneymind.feature.repository


import com.example.moneymind.feature.local.dao.FinanceDao
import com.example.moneymind.feature.local.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val localDao: FinanceDao
) : FinanceRepository {

    // exp
    override suspend fun addExpense(expense: Expense): Result<Long> {
        return try {
            val id = localDao.insertExpense(expense)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getExpenses(userId: String): Flow<List<Expense>> {
        return localDao.getExpensesByUser(userId)
    }

    override suspend fun getExpense(id: Long, userId: String): Result<Expense> {
        return try {
            val expense = localDao.getExpenseById(id, userId)
            if (expense != null) {
                Result.success(expense)
            } else {
                Result.failure(NoSuchElementException("Expense not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            localDao.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expense: Expense): Result<Unit> {
        return try {
            localDao.deleteExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getExpenseCategories(userId: String): Flow<List<String>> {
        return localDao.getExpenseCategories(userId)
    }

    // dash consult
    override suspend fun getTotalExpenses(userId: String, start: Date, end: Date): Result<Double> {
        return try {
            val total = localDao.getTotalExpenses(userId, start, end) ?: 0.0
            Result.success(total)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}