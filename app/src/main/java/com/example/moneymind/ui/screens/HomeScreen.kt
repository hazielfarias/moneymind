package com.example.moneymind.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moneymind.feature.local.model.Expense
import com.example.moneymind.feature.viewmodel.FinanceViewModel
import com.example.moneymind.ui.screens.components.AppDrawer
import com.example.moneymind.ui.screens.components.TopBarWithMenu
import com.example.moneymind.ui.screens.components.TransactionItem
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    financeViewModel: FinanceViewModel,
    userId : String
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val expenses = financeViewModel.expenses.collectAsState().value

    LaunchedEffect(Unit) {
        financeViewModel.loadExpenses(userId)
    }

    val recentTransactions = remember(expenses) {
        val combined = mutableListOf<Pair<Any, Boolean>>().apply {
            addAll(expenses.map { it to true })
        }

        combined.sortedByDescending { (item, _) ->
            when (item) {
                is Expense -> item.date.time
                else -> 0L
            }
        }.take(10)
    }

    AppDrawer(
        drawerState = drawerState,
        onProfileClick = { navController.navigate("profile_content") },
        onClose = { scope.launch { drawerState.close() } }
    ) {
        Scaffold(
            topBar = {
                TopBarWithMenu(
                    drawerState = drawerState,
                    onProfileClick = { navController.navigate("profile_content") },
                    onBackupClick = { Log.d("HomeScreen", "Backup iniciado") }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Bem-vindo ao MoneyMind!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Gerencie suas finanças e investimentos de forma inteligente",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Ações Rápidas",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("add_expense") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Adicionar Gasto")
                        }
                        Button(
                            onClick = {

                            if (expenses.isNotEmpty()) {
                                val message = buildString {
                                    appendLine("💸 Meus Gastos Recentes no Money Mind:")
                                    expenses.forEach { expense ->
                                        appendLine("• [${expense.category}] ${expense.title} - R$ %.2f".format(expense.value))
                                    }
                                    append("Total: R$ %.2f".format(expenses.sumOf { it.value }))
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            } else {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Ainda não cadastrei nenhum gasto no Money Mind, mas já estou me organizando! 💪💰"
                                    )
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),) {
                            Text("Compartilhar")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column {
                        Text(
                            text = "Transações Recentes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (recentTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhuma transação recente",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                items(recentTransactions.size) { index ->
                                    val (transaction, isExpense) = recentTransactions[index]
                                    when {
                                        isExpense && transaction is Expense -> {
                                            TransactionItem(
                                                name = transaction.title,
                                                type = "Despesa: ${transaction.category}",
                                                value = transaction.value,
                                                isExpense = true
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
