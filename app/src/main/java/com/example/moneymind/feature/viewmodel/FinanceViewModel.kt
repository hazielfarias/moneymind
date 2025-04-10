package com.example.moneymind.feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymind.feature.local.model.Expense
import com.example.moneymind.feature.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    // state flow
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()


    private val _expenseCategories = MutableStateFlow<List<String>>(emptyList())
    val expenseCategories: StateFlow<List<String>> = _expenseCategories.asStateFlow()

    private val _investmentTypes = MutableStateFlow<List<String>>(emptyList())
    val investmentTypes: StateFlow<List<String>> = _investmentTypes.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _uiState = MutableStateFlow<FinanceUiState>(FinanceUiState.Idle)
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()


    // exp
    fun loadExpenses(userId: String) {
        viewModelScope.launch {
            repository.getExpenses(userId).collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    suspend fun getExpense(id: Long, userId: String): Result<Expense> {
        return repository.getExpense(id, userId)
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            repository.addExpense(expense).fold(
                onSuccess = { id ->
                    loadExpenses(expense.userId)
                    _uiState.value = FinanceUiState.Success("Despesa adicionada com sucesso (ID: $id)")
                },
                onFailure = { exception ->
                    _uiState.value = FinanceUiState.Error(exception.message ?: "Erro ao adicionar despesa")
                }
            )
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            repository.updateExpense(expense).fold(
                onSuccess = {
                    loadExpenses(expense.userId)
                    _uiState.value = FinanceUiState.Success("Despesa atualizada com sucesso")
                },
                onFailure = { exception ->
                    _uiState.value = FinanceUiState.Error(exception.message ?: "Erro ao atualizar despesa")
                }
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            repository.deleteExpense(expense).fold(
                onSuccess = {
                    _uiState.value = FinanceUiState.Success("Despesa removida com sucesso")
                    _expenses.value = _expenses.value.filter { it.id != expense.id }
                },
                onFailure = { exception ->
                    _uiState.value = FinanceUiState.Error(exception.message ?: "Erro ao remover despesa")
                }
            )
        }
    }

    fun loadExpenseCategories(userId: String) {
        viewModelScope.launch {
            repository.getExpenseCategories(userId).collect { categories ->
                _expenseCategories.value = categories
            }
        }
    }

    // dash consult
    suspend fun loadTotalExpenses(userId: String, start: Date, end: Date) {
        repository.getTotalExpenses(userId, start, end).onSuccess { total ->
            _totalExpenses.value = total
        }.onFailure {
            _uiState.value = FinanceUiState.Error("Erro ao carregar totais de despesas")
        }
    }


    sealed class FinanceUiState {
        object Idle : FinanceUiState()
        object Loading : FinanceUiState()
        data class Success(val message: String) : FinanceUiState()
        data class Error(val message: String) : FinanceUiState()
    }
}