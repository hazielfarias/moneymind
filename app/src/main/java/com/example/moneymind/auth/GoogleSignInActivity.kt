package com.example.moneymind.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseUser

class GoogleSignInActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            authViewModel.userState.collect { user ->
                updateUI(user)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI(authViewModel.currentUser())
    }

    fun loginWithEmail(email: String, password: String) {
        authViewModel.signInWithEmail(email, password) { success, message ->
            if (success) {
                showToast("Login bem-sucedido!")
            } else {
                showToast("Erro: $message")
            }
        }
    }

    fun registerWithEmail(email: String, password: String) {
        authViewModel.signUpWithEmail(email, password) { success, message ->
            if (success) {
                showToast("Conta criada com sucesso!")
            } else {
                showToast("Erro ao cadastrar: $message")
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            showToast("Usuário autenticado: ${user.email}")
        } else {
            showToast("Usuário deslogado")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
