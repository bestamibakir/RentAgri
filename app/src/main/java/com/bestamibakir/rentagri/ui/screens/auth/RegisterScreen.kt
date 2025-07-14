package com.bestamibakir.rentagri.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.ui.components.CountrySelector
import com.bestamibakir.rentagri.ui.components.PhoneNumberField
import com.bestamibakir.rentagri.ui.components.ProvinceDistrictSelector
import com.bestamibakir.rentagri.ui.components.RentAgriButton
import com.bestamibakir.rentagri.ui.components.RentAgriCitySelector
import com.bestamibakir.rentagri.ui.components.RentAgriPasswordTextField
import com.bestamibakir.rentagri.ui.components.RentAgriTextField
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.registerState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RentAgriTopAppBar(
            title = "Kayıt Ol",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            RentAgriTextField(
                value = uiState.firstName,
                onValueChange = { viewModel.updateFirstName(it) },
                label = "Adınız",
                isError = uiState.firstNameError != null,
                errorMessage = uiState.firstNameError ?: "",
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.updateLastName(it) },
                label = "Soyadınız",
                isError = uiState.lastNameError != null,
                errorMessage = uiState.lastNameError ?: "",
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))


            CountrySelector(
                selectedCountry = uiState.selectedCountry,
                onCountrySelected = { viewModel.updateSelectedCountry(it) },
                label = "Ülke"
            )

            Spacer(modifier = Modifier.height(16.dp))


            PhoneNumberField(
                value = uiState.phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                selectedCountry = uiState.selectedCountry,
                label = "Telefon",
                isError = uiState.phoneNumberError != null,
                errorMessage = uiState.phoneNumberError ?: ""
            )

            Spacer(modifier = Modifier.height(16.dp))


            if (uiState.selectedCountry.code == "TR") {
                ProvinceDistrictSelector(
                    selectedProvince = uiState.province,
                    selectedDistrict = uiState.district,
                    onProvinceSelected = { viewModel.updateProvince(it) },
                    onDistrictSelected = { viewModel.updateDistrict(it) },
                    isProvinceError = uiState.provinceError != null,
                    isDistrictError = uiState.districtError != null,
                    provinceErrorMessage = uiState.provinceError ?: "",
                    districtErrorMessage = uiState.districtError ?: ""
                )
            } else {

                RentAgriCitySelector(
                    selectedCity = uiState.city,
                    onCitySelected = { viewModel.updateCity(it) },
                    cities = uiState.selectedCountry.cities,
                    label = "Şehir",
                    isError = uiState.cityError != null,
                    errorMessage = uiState.cityError ?: ""
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "E-posta",
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError ?: "",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriPasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Şifre",
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError ?: "",
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))


            RentAgriPasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = "Şifre Tekrar",
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError ?: "",
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(32.dp))


            RentAgriButton(
                onClick = { viewModel.register() },
                text = "Kayıt Ol",
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RentAgriTheme {
        RegisterScreen(
            onNavigateToHome = {},
            onNavigateBack = {}
        )
    }
} 