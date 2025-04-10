package com.example.moneymind

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneymind.auth.AuthViewModel
import com.example.moneymind.feature.viewmodel.FinanceViewModel
import com.example.moneymind.ui.screens.AddExpenseScreen
import com.example.moneymind.ui.screens.HomeScreen
import com.example.moneymind.ui.screens.LoginScreen
import com.example.moneymind.ui.screens.RegisterScreen
import com.example.moneymind.ui.screens.components.ProfileContent
import com.example.moneymind.ui.theme.MoneymindTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        // usuario negou -> vai direcionar para as config
                        val intent = Intent().apply {
                            action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        startActivity(intent)
                    }
                }
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneymindTheme {
                val authViewModel: AuthViewModel = viewModel ()
                val currentUser by authViewModel.userState.collectAsState()
                val navController = rememberNavController()
                val financeViewModel: FinanceViewModel = viewModel()

                val startDestination = if (currentUser != null) "home" else "login"

                Scaffold(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                authViewModel.checkCurrentUser()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                                onNavigateToSignUp = {
                                    navController.navigate("register") {
                                        popUpTo("login") { inclusive = false }
                                    }
                                })
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    authViewModel.checkCurrentUser()
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(navController = navController, financeViewModel = financeViewModel, userId = currentUser?.uid.toString())
                        }
                        composable("profile_content") {
                            ProfileContent(user = currentUser, navController = navController,  onBackClick = { navController.popBackStack() })
                        }
                        composable("add_expense") {
                            val viewModel = hiltViewModel<FinanceViewModel>()
                            currentUser?.uid?.let { userId ->
                                AddExpenseScreen(
                                    viewModel = viewModel,
                                    userId = userId,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                                            }
                }
            }
        }
    }
}
