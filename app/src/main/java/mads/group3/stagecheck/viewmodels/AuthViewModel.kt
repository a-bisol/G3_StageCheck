package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mads.group3.stagecheck.models.User

class AuthViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun checkSignedIn() {
        val currentUser = auth.currentUser
        _uiState.value = _uiState.value.copy(
            currentUser = currentUser?.email
        )

        currentUser?.let { user ->
            viewModelScope.launch {
                ensureUserDocExists(user.uid, user.email ?: "")
            }
        }
    }

    private suspend fun ensureUserDocExists(uid: String, email: String) {
        try {
            val docRef = firestore.collection("users").document(uid)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val user = User(email = email, createdAt = Timestamp.now())
                docRef.set(user).await()
                Log.d("AuthViewModel - ensureUserDocExists", "Created missing user doc for $uid")
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("AuthViewModel - ensureUserDocExists", it)
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill both fields."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("User not found")
                ensureUserDocExists(uid, email)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = email,
                    errorMessage = null
                )
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException,
                    is FirebaseAuthInvalidCredentialsException -> "Could not find email/password combination"

                    else -> "Sign-in failed: ${e.localizedMessage}"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
                Log.e("AuthViewModel", "SignIn error", e)
            }
        }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill both fields"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("User creation failed")
                val user = User(email = email, createdAt = Timestamp.now())
                firestore.collection("users").document(uid).set(user).await()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = email,
                    errorMessage = null
                )
            } catch (e: Exception) {
                val errorMessage = if (e is FirebaseAuthUserCollisionException) {
                    "Email already exists. Please sign in instead."
                } else {
                    "Registration failed: ${e.localizedMessage}"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
                Log.e("AuthViewModel", "Register error", e)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.value = _uiState.value.copy(currentUser = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    data class AuthUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val currentUser: String? = null
    )
}