package com.example.moneymind.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.moneymind.MainActivity
import com.example.moneymind.ui.screens.LoginScreen

class EmailPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(onLoginSuccess = {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            })
        }
    }
}
