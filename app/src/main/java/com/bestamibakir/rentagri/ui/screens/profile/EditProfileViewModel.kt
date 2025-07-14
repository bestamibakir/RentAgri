package com.bestamibakir.rentagri.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Country
import com.bestamibakir.rentagri.data.model.CountryData
import com.bestamibakir.rentagri.data.model.User
import com.bestamibakir.rentagri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val originalUser: User? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val selectedCountry: Country = CountryData.supportedCountries.first { it.code == "TR" },
    val city: String = "",
    val province: String = "",
    val district: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val cityError: String? = null,
    val provinceError: String? = null,
    val districtError: String? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val hasChanges: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var editProfileState by mutableStateOf(EditProfileState(isLoading = true))
        private set

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        editProfileState = editProfileState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {

                    val userCountry = CountryData.supportedCountries.find {
                        it.code == currentUser.country
                    } ?: CountryData.supportedCountries.first { it.code == "TR" }

                    editProfileState = editProfileState.copy(
                        originalUser = currentUser,
                        firstName = currentUser.firstName,
                        lastName = currentUser.lastName,
                        phoneNumber = currentUser.phoneNumber,
                        selectedCountry = userCountry,
                        city = currentUser.city,
                        province = currentUser.province,
                        district = currentUser.district,
                        isLoading = false
                    )

                    checkForChanges()
                } else {
                    editProfileState = editProfileState.copy(
                        isLoading = false,
                        error = "Kullanıcı bilgisi bulunamadı"
                    )
                }
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error loading user", e)
                editProfileState = editProfileState.copy(
                    isLoading = false,
                    error = e.message ?: "Kullanıcı bilgisi yüklenirken bir hata oluştu"
                )
            }
        }
    }

    fun updateFirstName(firstName: String) {

        val filteredName = firstName.filter { it.isLetter() || it.isWhitespace() }
        editProfileState = editProfileState.copy(firstName = filteredName, firstNameError = null)
        checkForChanges()
    }

    fun updateLastName(lastName: String) {

        val filteredName = lastName.filter { it.isLetter() || it.isWhitespace() }
        editProfileState = editProfileState.copy(lastName = filteredName, lastNameError = null)
        checkForChanges()
    }

    fun updatePhoneNumber(phoneNumber: String) {

        val filteredPhone = phoneNumber.filter { it.isDigit() }
        val maxLength = editProfileState.selectedCountry.phoneLength.maxOrNull() ?: 15
        val trimmedPhone = if (filteredPhone.length > maxLength) {
            filteredPhone.take(maxLength)
        } else {
            filteredPhone
        }
        editProfileState =
            editProfileState.copy(phoneNumber = trimmedPhone, phoneNumberError = null)
        checkForChanges()
    }

    fun updateSelectedCountry(country: Country) {

        editProfileState = editProfileState.copy(
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
        checkForChanges()
    }

    fun updateCity(city: String) {
        editProfileState = editProfileState.copy(city = city, cityError = null)
        checkForChanges()
    }

    fun updateProvince(province: String) {
        editProfileState = editProfileState.copy(
            province = province,
            district = "",
            provinceError = null,
            districtError = null
        )
        checkForChanges()
    }

    fun updateDistrict(district: String) {
        editProfileState = editProfileState.copy(district = district, districtError = null)
        checkForChanges()
    }

    fun selectProfilePhoto() {
        // TODO: Profil fotoğrafı seçme işlemi
    }

    private fun checkForChanges() {
        val original = editProfileState.originalUser
        if (original == null) {
            editProfileState = editProfileState.copy(hasChanges = false)
            return
        }

        val hasChanges = editProfileState.firstName != original.firstName ||
                editProfileState.lastName != original.lastName ||
                editProfileState.phoneNumber != original.phoneNumber ||
                editProfileState.selectedCountry.code != original.country ||
                editProfileState.city != original.city ||
                editProfileState.province != original.province ||
                editProfileState.district != original.district

        editProfileState = editProfileState.copy(hasChanges = hasChanges)
    }

    fun updateProfile() {
        if (!validateInputs()) return

        editProfileState = editProfileState.copy(isUpdating = true, error = null)

        viewModelScope.launch {
            try {
                val originalUser = editProfileState.originalUser
                if (originalUser != null) {
                    val updatedUser = originalUser.copy(
                        firstName = editProfileState.firstName,
                        lastName = editProfileState.lastName,
                        phoneNumber = editProfileState.phoneNumber,
                        countryCode = editProfileState.selectedCountry.phoneCode,
                        country = editProfileState.selectedCountry.code,
                        city = editProfileState.city,
                        province = editProfileState.province,
                        district = editProfileState.district
                    )

                    userRepository.updateUser(updatedUser)

                    editProfileState = editProfileState.copy(
                        isUpdating = false,
                        isSuccess = true,
                        originalUser = updatedUser,
                        hasChanges = false
                    )
                } else {
                    editProfileState = editProfileState.copy(
                        isUpdating = false,
                        error = "Kullanıcı bilgisi bulunamadı"
                    )
                }
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error updating profile", e)
                editProfileState = editProfileState.copy(
                    isUpdating = false,
                    error = e.message ?: "Profil güncellenirken bir hata oluştu"
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        Log.d("EditProfileViewModel", "Validating inputs...")
        Log.d("EditProfileViewModel", "First name: '${editProfileState.firstName}'")
        Log.d("EditProfileViewModel", "Last name: '${editProfileState.lastName}'")
        Log.d("EditProfileViewModel", "Phone: '${editProfileState.phoneNumber}'")
        Log.d("EditProfileViewModel", "Country: '${editProfileState.selectedCountry.code}'")
        Log.d("EditProfileViewModel", "City: '${editProfileState.city}'")


        if (editProfileState.firstName.isBlank()) {
            editProfileState = editProfileState.copy(firstNameError = "Ad boş olamaz")
            isValid = false
        } else if (editProfileState.firstName.length < 2) {
            editProfileState =
                editProfileState.copy(firstNameError = "Ad en az 2 karakter olmalıdır")
            isValid = false
        } else if (!editProfileState.firstName.matches(Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$"))) {
            editProfileState = editProfileState.copy(firstNameError = "Ad sadece harf içerebilir")
            isValid = false
        }


        if (editProfileState.lastName.isBlank()) {
            editProfileState = editProfileState.copy(lastNameError = "Soyad boş olamaz")
            isValid = false
        } else if (editProfileState.lastName.length < 2) {
            editProfileState =
                editProfileState.copy(lastNameError = "Soyad en az 2 karakter olmalıdır")
            isValid = false
        } else if (!editProfileState.lastName.matches(Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$"))) {
            editProfileState = editProfileState.copy(lastNameError = "Soyad sadece harf içerebilir")
            isValid = false
        }


        val phoneNumber = editProfileState.phoneNumber.trim()
        val selectedCountry = editProfileState.selectedCountry

        Log.d(
            "EditProfileViewModel",
            "Phone validation - Number: '$phoneNumber', Length: ${phoneNumber.length}, Country: ${selectedCountry.code}"
        )

        if (phoneNumber.isBlank()) {
            editProfileState = editProfileState.copy(phoneNumberError = "Telefon boş olamaz")
            isValid = false
        } else if (!phoneNumber.matches(Regex("^[0-9]+$"))) {
            editProfileState =
                editProfileState.copy(phoneNumberError = "Telefon sadece rakam içerebilir")
            isValid = false
        } else if (phoneNumber.length !in selectedCountry.phoneLength) {
            val lengthText = if (selectedCountry.phoneLength.count() == 1) {
                "${selectedCountry.phoneLength.first()} haneli"
            } else {
                "${selectedCountry.phoneLength.min()}-${selectedCountry.phoneLength.max()} haneli"
            }
            editProfileState =
                editProfileState.copy(phoneNumberError = "Telefon $lengthText olmalıdır")
            isValid = false
        } else {

            when (selectedCountry.code) {
                "TR" -> {

                    if (!phoneNumber.startsWith("5")) {
                        editProfileState = editProfileState.copy(
                            phoneNumberError = "Türkiye'de cep telefonu 5 ile başlamalıdır"
                        )
                        isValid = false
                    }
                }

                "US" -> {

                }

                "DE" -> {

                }

                "FR" -> {

                }

                "GB" -> {

                }
            }
        }


        if (editProfileState.selectedCountry.code == "TR") {
            if (editProfileState.province.isBlank()) {
                editProfileState = editProfileState.copy(provinceError = "İl seçimi zorunludur")
                isValid = false
            }


            if (editProfileState.district.isBlank()) {
                editProfileState = editProfileState.copy(districtError = "İlçe seçimi zorunludur")
                isValid = false
            }
        }


        if (editProfileState.selectedCountry.code != "TR" && editProfileState.city.isBlank()) {
            editProfileState = editProfileState.copy(cityError = "Şehir boş olamaz")
            isValid = false
        } else if (editProfileState.selectedCountry.code != "TR" &&
            !editProfileState.selectedCountry.cities.contains(editProfileState.city)
        ) {
            editProfileState = editProfileState.copy(cityError = "Geçersiz şehir seçimi")
            isValid = false
        }

        Log.d("EditProfileViewModel", "Validation result: $isValid")
        return isValid
    }
} 