package com.bestamibakir.rentagri.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.FirebaseNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isRetrying: Boolean = false,
    val retryCount: Int = 0
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

    companion object {
        private const val TAG = "LoginViewModel"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 2000L
    }

    fun updateEmail(email: String) {
        loginState = loginState.copy(
            email = email,
            emailError = null,
            error = null
        )
    }

    fun updatePassword(password: String) {
        loginState = loginState.copy(
            password = password,
            passwordError = null,
            error = null
        )
    }

    fun login() {
        if (!validateInputs()) return

        loginState = loginState.copy(
            isLoading = true,
            error = null,
            retryCount = 0,
            isRetrying = false
        )

        performLogin()
    }

    fun retryLogin() {
        if (loginState.retryCount >= MAX_RETRY_COUNT) {
            loginState = loginState.copy(
                error = "Maksimum deneme sayısı aşıldı. Lütfen internet bağlantınızı kontrol edip tekrar deneyin."
            )
            return
        }

        loginState = loginState.copy(
            isRetrying = true,
            error = null,
            retryCount = loginState.retryCount + 1
        )

        viewModelScope.launch {
            delay(RETRY_DELAY_MS)
            performLogin()
        }
    }

    private fun performLogin() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting login for email: ${loginState.email}")


                try {
                    auth.signInWithEmailAndPassword(loginState.email, loginState.password).await()

                    Log.d(TAG, "Login successful")
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = true,
                        isRetrying = false
                    )

                } catch (gmsException: Exception) {

                    if (gmsException.message?.contains("broker", ignoreCase = true) == true ||
                        gmsException.message?.contains(
                            "Unknown calling package",
                            ignoreCase = true
                        ) == true
                    ) {

                        Log.w(
                            TAG,
                            "Google Play Services issue detected, attempting alternative auth flow"
                        )


                        delay(1000)
                        val freshAuth = FirebaseAuth.getInstance()
                        freshAuth.signInWithEmailAndPassword(loginState.email, loginState.password)
                            .await()

                        Log.d(TAG, "Alternative auth flow successful")
                        loginState = loginState.copy(
                            isLoading = false,
                            isSuccess = true,
                            isRetrying = false
                        )
                    } else {
                        throw gmsException
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)

                val errorMessage = when (e) {
                    is FirebaseNetworkException -> {
                        "İnternet bağlantısı sorunu. Lütfen bağlantınızı kontrol edin."
                    }

                    is FirebaseAuthException -> {
                        when (e.errorCode) {
                            "ERROR_USER_NOT_FOUND" -> "Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı."
                            "ERROR_WRONG_PASSWORD" -> "Şifre hatalı. Lütfen kontrol edin."
                            "ERROR_USER_DISABLED" -> "Bu hesap devre dışı bırakılmış."
                            "ERROR_TOO_MANY_REQUESTS" -> "Çok fazla başarısız deneme. Lütfen biraz bekleyin."
                            "ERROR_NETWORK_REQUEST_FAILED" -> "Ağ bağlantısı hatası. İnternet bağlantınızı kontrol edin."
                            else -> "Giriş yapılamadı: ${e.message}"
                        }
                    }

                    else -> {
                        when {
                            e.message?.contains("network", ignoreCase = true) == true ||
                                    e.message?.contains("timeout", ignoreCase = true) == true ||
                                    e.message?.contains(
                                        "unreachable",
                                        ignoreCase = true
                                    ) == true -> {
                                "Ağ bağlantısı sorunu. İnternet bağlantınızı kontrol edin."
                            }

                            e.message?.contains("broker", ignoreCase = true) == true ||
                                    e.message?.contains(
                                        "google.android.gms",
                                        ignoreCase = true
                                    ) == true ||
                                    e.message?.contains(
                                        "SecurityException",
                                        ignoreCase = true
                                    ) == true -> {
                                "Google Play Services sorunu. Emülatörü yeniden başlatmayı deneyin."
                            }

                            else -> {
                                "Giriş yapılamadı: ${e.message ?: "Bilinmeyen hata"}"
                            }
                        }
                    }
                }

                loginState = loginState.copy(
                    isLoading = false,
                    isRetrying = false,
                    error = errorMessage
                )
            }
        }
    }

    fun clearError() {
        loginState = loginState.copy(error = null)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (loginState.email.isBlank()) {
            loginState = loginState.copy(emailError = "E-posta boş olamaz")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginState.email).matches()) {
            loginState = loginState.copy(emailError = "Geçerli bir e-posta giriniz")
            isValid = false
        }

        if (loginState.password.isBlank()) {
            loginState = loginState.copy(passwordError = "Şifre boş olamaz")
            isValid = false
        } else if (loginState.password.length < 6) {
            loginState = loginState.copy(passwordError = "Şifre en az 6 karakter olmalıdır")
            isValid = false
        }

        return isValid
    }
} 