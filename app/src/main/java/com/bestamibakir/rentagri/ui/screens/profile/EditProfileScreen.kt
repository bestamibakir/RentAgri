package com.bestamibakir.rentagri.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bestamibakir.rentagri.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.ui.components.CountrySelector
import com.bestamibakir.rentagri.ui.components.PhoneNumberField
import com.bestamibakir.rentagri.ui.components.ProvinceDistrictSelector
import com.bestamibakir.rentagri.ui.components.RentAgriButton
import com.bestamibakir.rentagri.ui.components.RentAgriCitySelector
import com.bestamibakir.rentagri.ui.components.RentAgriTextField
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.editProfileState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Profil başarıyla güncellendi!")
            }
            onNavigateBack()
        }
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
            }
        }
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(
                title = stringResource(R.string.edit_profile),
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Profil bilgileri yükleniyor...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))


                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {

                        viewModel.selectProfilePhoto()
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.select_profile_photo),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                        IconButton(
                            onClick = { viewModel.selectProfilePhoto() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = stringResource(R.string.change_photo),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


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

                Spacer(modifier = Modifier.height(32.dp))


                RentAgriButton(
                    onClick = { viewModel.updateProfile() },
                    text = stringResource(R.string.update_profile),
                    enabled = !uiState.isUpdating && uiState.hasChanges,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    RentAgriTheme {
        EditProfileScreen(
            onNavigateBack = {}
        )
    }
} 