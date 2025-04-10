package com.example.moneymind.ui.screens.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymind.auth.AuthViewModel
import com.example.moneymind.feature.datastore.ContextDataStore
import com.example.moneymind.worker.ReminderWorker
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: FirebaseUser?,
    onBackClick: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var notificationsEnabled by rememberSaveable { mutableStateOf(false) }
    var dayOfMonth by rememberSaveable { mutableIntStateOf(1) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableLongStateOf(
            LocalDate.now()
                .withDayOfMonth(dayOfMonth)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }

    val dataStore = remember { ContextDataStore(context) }
    var pendingToggle by remember { mutableStateOf<Boolean?>(null) }
    var updatedDay by remember { mutableStateOf<Int?>(null) }

    // Carrega as configurações salvas
    LaunchedEffect(Unit) {
        dataStore.notificationEnabled.collect { enabled ->
            notificationsEnabled = enabled
        }
        dataStore.notificationDay.collect { day ->
            dayOfMonth = day
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ProfileImage(user?.photoUrl)

            CardSection {
                InfoRow(
                    label = "Email",
                    value = user?.email ?: "Não disponível",
                    icon = Icons.Rounded.Info
                )
            }

            CardSection {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Lembretes Mensais",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            notificationsEnabled = enabled
                            pendingToggle = enabled
                        }
                    )
                }

                if (notificationsEnabled) {
                    InfoRow(
                        label = "Dia do mês",
                        value = dayOfMonth.toString(),
                        icon = Icons.Rounded.Edit,
                        onClick = { showDatePicker = true }
                    )

                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Sair da Conta", fontSize = 16.sp)
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate,
                yearRange = IntRange(2024, 2026),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = LocalDate.ofInstant(
                            java.time.Instant.ofEpochMilli(utcTimeMillis),
                            ZoneId.systemDefault()
                        )
                        return date.dayOfMonth in 1..28
                    }

                    override fun isSelectableYear(year: Int): Boolean = true
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = LocalDate.ofInstant(
                                    java.time.Instant.ofEpochMilli(millis),
                                    ZoneId.systemDefault()
                                )
                                dayOfMonth = date.dayOfMonth
                                selectedDate = millis
                                updatedDay = date.dayOfMonth
                                showDatePicker = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    LaunchedEffect(pendingToggle) {
        pendingToggle?.let { enabled ->
            dataStore.setNotificationEnabled(enabled)
            if (enabled) {
                ReminderWorker.scheduleMonthlyReminder(context, dayOfMonth)
            } else {
                ReminderWorker.cancelReminder(context)
            }
            pendingToggle = null
        }
    }

    LaunchedEffect(updatedDay) {
        updatedDay?.let { day ->
            dataStore.setNotificationDay(day)
            if (notificationsEnabled) {
                ReminderWorker.scheduleMonthlyReminder(context, day)
            }
            updatedDay = null
        }
    }
}
