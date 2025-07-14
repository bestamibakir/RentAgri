package com.bestamibakir.rentagri.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Country
import com.bestamibakir.rentagri.data.model.CountryData
import com.bestamibakir.rentagri.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val selectedCountry: Country = CountryData.supportedCountries.first { it.code == "TR" },
    val city: String = "",
    val province: String = "",
    val district: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val cityError: String? = null,
    val provinceError: String? = null,
    val districtError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var registerState by mutableStateOf(RegisterState())
        private set

    fun updateFirstName(firstName: String) {

        val filteredName = firstName.filter { it.isLetter() || it.isWhitespace() }
        registerState = registerState.copy(firstName = filteredName, firstNameError = null)
    }

    fun updateLastName(lastName: String) {

        val filteredName = lastName.filter { it.isLetter() || it.isWhitespace() }
        registerState = registerState.copy(lastName = filteredName, lastNameError = null)
    }

    fun updatePhoneNumber(phoneNumber: String) {

        val filteredPhone = phoneNumber.filter { it.isDigit() }
        val maxLength = registerState.selectedCountry.phoneLength.maxOrNull() ?: 15
        val trimmedPhone = if (filteredPhone.length > maxLength) {
            filteredPhone.take(maxLength)
        } else {
            filteredPhone
        }
        registerState = registerState.copy(phoneNumber = trimmedPhone, phoneNumberError = null)
    }

    fun updateSelectedCountry(country: Country) {

        registerState = registerState.copy(
            selectedCountry = country,
            phoneNumber = "",
            city = "",
            province = "",
            district = "",
            phoneNumberError = null,
            cityError = null,
            provinceError = null,
            districtError = null
        )
    }

    fun updateCity(city: String) {
        registerState = registerState.copy(city = city, cityError = null)
    }

    fun updateProvince(province: String) {
        registerState = registerState.copy(
            province = province,
            district = "",
            provinceError = null,
            districtError = null
        )
    }

    fun updateDistrict(district: String) {
        registerState = registerState.copy(district = district, districtError = null)
    }

    fun updateEmail(email: String) {
        registerState = registerState.copy(email = email, emailError = null)
    }

    fun updatePassword(password: String) {
        registerState = registerState.copy(password = password, passwordError = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        registerState =
            registerState.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
    }

    fun register() {
        if (!validateInputs()) return

        registerState = registerState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {

                val authResult = auth.createUserWithEmailAndPassword(
                    registerState.email, registerState.password
                ).await()

                authResult.user?.let { firebaseUser ->

                    val user = User(
                        id = firebaseUser.uid,
                        firstName = registerState.firstName,
                        lastName = registerState.lastName,
                        email = registerState.email,
                        phoneNumber = registerState.phoneNumber,
                        countryCode = registerState.selectedCountry.phoneCode,
                        country = registerState.selectedCountry.code,
                        city = registerState.city,
                        province = registerState.province,
                        district = registerState.district
                    )

                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .set(user)
                        .await()

                    registerState = registerState.copy(isLoading = false, isSuccess = true)
                } ?: throw Exception("Kullanıcı oluşturulamadı.")

            } catch (e: Exception) {
                registerState = registerState.copy(
                    isLoading = false,
                    error = e.message ?: "Kayıt işlemi başarısız oldu."
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true


        if (registerState.firstName.isBlank()) {
            registerState = registerState.copy(firstNameError = "Ad boş olamaz")
            isValid = false
        } else if (registerState.firstName.length < 2) {
            registerState = registerState.copy(firstNameError = "Ad en az 2 karakter olmalıdır")
            isValid = false
        } else if (!registerState.firstName.matches(Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$"))) {
            registerState = registerState.copy(firstNameError = "Ad sadece harf içerebilir")
            isValid = false
        }


        if (registerState.lastName.isBlank()) {
            registerState = registerState.copy(lastNameError = "Soyad boş olamaz")
            isValid = false
        } else if (registerState.lastName.length < 2) {
            registerState = registerState.copy(lastNameError = "Soyad en az 2 karakter olmalıdır")
            isValid = false
        } else if (!registerState.lastName.matches(Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$"))) {
            registerState = registerState.copy(lastNameError = "Soyad sadece harf içerebilir")
            isValid = false
        }


        val phoneNumber = registerState.phoneNumber.trim()
        val selectedCountry = registerState.selectedCountry

        if (phoneNumber.isBlank()) {
            registerState = registerState.copy(phoneNumberError = "Telefon boş olamaz")
            isValid = false
        } else if (!phoneNumber.matches(Regex("^[0-9]+$"))) {
            registerState = registerState.copy(phoneNumberError = "Telefon sadece rakam içerebilir")
            isValid = false
        } else if (phoneNumber.length !in selectedCountry.phoneLength) {
            val lengthText = if (selectedCountry.phoneLength.count() == 1) {
                "${selectedCountry.phoneLength.first()} haneli"
            } else {
                "${selectedCountry.phoneLength.min()}-${selectedCountry.phoneLength.max()} haneli"
            }
            registerState = registerState.copy(phoneNumberError = "Telefon $lengthText olmalıdır")
            isValid = false
        } else {

            when (selectedCountry.code) {
                "TR" -> {

                    if (!phoneNumber.startsWith("5")) {
                        registerState = registerState.copy(
                            phoneNumberError = "Türkiye'de cep telefonu 5 ile başlamalıdır"
                        )
                        isValid = false
                    }
                }

                else -> {

                    val regexPattern = selectedCountry.phoneFormat.replace("X", "\\d")
                    val isValidFormat = phoneNumber.matches(regexPattern.toRegex())
                    if (!isValidFormat) {
                        registerState = registerState.copy(
                            phoneNumberError = "Geçersiz telefon formatı. Örnek: ${selectedCountry.phoneFormat}"
                        )
                        isValid = false
                    }
                }
            }
        }


        if (registerState.selectedCountry.code == "TR") {
            if (registerState.province.isBlank()) {
                registerState = registerState.copy(provinceError = "İl seçimi zorunludur")
                isValid = false
            }


            if (registerState.district.isBlank()) {
                registerState = registerState.copy(districtError = "İlçe seçimi zorunludur")
                isValid = false
            }
        }


        if (registerState.selectedCountry.code != "TR" && registerState.city.isBlank()) {
            registerState = registerState.copy(cityError = "Şehir boş olamaz")
            isValid = false
        } else if (registerState.selectedCountry.code != "TR" &&
            !registerState.selectedCountry.cities.contains(registerState.city)
        ) {
            registerState = registerState.copy(cityError = "Geçersiz şehir seçimi")
            isValid = false
        }


        if (registerState.email.isBlank()) {
            registerState = registerState.copy(emailError = "E-posta boş olamaz")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(registerState.email).matches()) {
            registerState = registerState.copy(emailError = "Geçerli bir e-posta giriniz")
            isValid = false
        }


        if (registerState.password.isBlank()) {
            registerState = registerState.copy(passwordError = "Şifre boş olamaz")
            isValid = false
        } else if (registerState.password.length < 6) {
            registerState = registerState.copy(passwordError = "Şifre en az 6 karakter olmalıdır")
            isValid = false
        } else if (registerState.password.length > 20) {
            registerState =
                registerState.copy(passwordError = "Şifre en fazla 20 karakter olabilir")
            isValid = false
        } else if (!registerState.password.matches(Regex(".*[A-Za-z].*"))) {
            registerState = registerState.copy(passwordError = "Şifre en az bir harf içermelidir")
            isValid = false
        } else if (!registerState.password.matches(Regex(".*[0-9].*"))) {
            registerState = registerState.copy(passwordError = "Şifre en az bir rakam içermelidir")
            isValid = false
        }


        if (registerState.confirmPassword.isBlank()) {
            registerState = registerState.copy(confirmPasswordError = "Şifre tekrarı boş olamaz")
            isValid = false
        } else if (registerState.password != registerState.confirmPassword) {
            registerState = registerState.copy(confirmPasswordError = "Şifreler eşleşmiyor")
            isValid = false
        }

        return isValid
    }
} 