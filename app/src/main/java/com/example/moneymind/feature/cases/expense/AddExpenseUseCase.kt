package com.example.moneymind.feature.cases.expense

import com.example.moneymind.feature.local.model.Expense
import com.example.moneymind.feature.repository.FinanceRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        return repository.addExpense(expense)
    }
}