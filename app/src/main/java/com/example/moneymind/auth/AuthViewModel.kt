package com.example.moneymind.auth

import android.app.Application
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.example.moneymind.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor ( private val application: Application ) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(application.applicationContext)

    private val _userState = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val userState: StateFlow<FirebaseUser?> = _userState


    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userState.value = auth.currentUser
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signUpWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userState.value = auth.currentUser
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext

                Log.d("Auth web client id: ", context.getString(R.string.default_web_client_id))

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()

                Log.d("Auth google id option: ", googleIdOption.toString())

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                Log.d("Auth request: ", request.toString())

                val result = credentialManager.getCredential(context, request)

                Log.d("Auth result: ", result.toString())

                handleSignIn(result.credential)

            } catch (e: GetCredentialException) {
                Log.e(TAG, "Erro ao obter credenciais: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        Log.d("credentials:  ", credential.toString() + "  type : " + credential.type)
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credencial inválida!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Login com Google bem-sucedido")
                    _userState.value = auth.currentUser
                } else {
                    Log.w(TAG, "Falha no login com Google", task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        viewModelScope.launch {
            try {
                val request = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(request)
                _userState.value = null
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Erro ao limpar credenciais: ${e.localizedMessage}")
            }
        }
    }

    fun checkCurrentUser() {
        _userState.value = auth.currentUser
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}
