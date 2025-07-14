package com.bestamibakir.rentagri.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.R
import com.bestamibakir.rentagri.ui.components.RentAgriButton
import com.bestamibakir.rentagri.ui.components.RentAgriPasswordTextField
import com.bestamibakir.rentagri.ui.components.RentAgriTextButton
import com.bestamibakir.rentagri.ui.components.RentAgriTextField
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState = viewModel.loginState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
                viewModel.clearError()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.splash_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )


            RentAgriTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "E-posta",
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError ?: "",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                enabled = !uiState.isLoading && !uiState.isRetrying
            )

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriPasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Şifre",
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError ?: "",
                imeAction = ImeAction.Done,
                enabled = !uiState.isLoading && !uiState.isRetrying
            )

            Spacer(modifier = Modifier.height(24.dp))


            if (uiState.isLoading || uiState.isRetrying) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (uiState.isRetrying)
                            "Tekrar deneniyor... (${uiState.retryCount}/3)"
                        else
                            "Giriş yapılıyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }


            RentAgriButton(
                onClick = { viewModel.login() },
                text = "Giriş Yap",
                enabled = !uiState.isLoading && !uiState.isRetrying
            )


            if (uiState.error?.contains("bağlantı", ignoreCase = true) == true ||
                uiState.error?.contains("network", ignoreCase = true) == true
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.retryLogin() },
                    enabled = !uiState.isLoading && !uiState.isRetrying && uiState.retryCount < 3
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Tekrar Dene")
                    }
                }

                if (uiState.retryCount > 0) {
                    Text(
                        text = "Deneme: ${uiState.retryCount}/3",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriButton(
                onClick = onNavigateToRegister,
                text = "Kayıt Ol",
                enabled = !uiState.isLoading && !uiState.isRetrying
            )

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriTextButton(
                onClick = { /* TODO: Şifremi unuttum akışı */ },
                text = "Şifremi unuttum",
                modifier = Modifier.wrapContentSize(),
                enabled = !uiState.isLoading && !uiState.isRetrying
            )


            if (uiState.error?.contains("bağlantı", ignoreCase = true) == true ||
                uiState.error?.contains("network", ignoreCase = true) == true
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "İnternet bağlantınızı kontrol edin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RentAgriTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToHome = {}
        )
    }
} 