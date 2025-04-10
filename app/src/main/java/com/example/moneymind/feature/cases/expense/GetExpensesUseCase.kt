package com.example.moneymind.feature.cases.expense

import com.example.moneymind.feature.local.model.Expense
import com.example.moneymind.feature.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(userId: String): Flow<List<Expense>> {
        return repository.getExpenses(userId)
    }
}