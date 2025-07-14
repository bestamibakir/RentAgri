package com.bestamibakir.rentagri.ui.screens.listings

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Listing
import com.bestamibakir.rentagri.data.repository.ImageRepository
import com.bestamibakir.rentagri.data.repository.ListingRepository
import com.bestamibakir.rentagri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class CreateListingState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isUploadingImages: Boolean = false,
    val uploadProgress: Float = 0f,
    val uploadedImageUrls: List<String> = emptyList(),
    val tempListingId: String = UUID.randomUUID().toString()
)

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    var createListingState by mutableStateOf(CreateListingState())
        private set

    fun uploadImages(imageUris: List<Uri>) {
        if (imageUris.isEmpty()) return

        createListingState = createListingState.copy(
            isUploadingImages = true,
            uploadProgress = 0f,
            error = null
        )
        viewModelScope.launch {
            imageRepository.uploadListingImages(imageUris, createListingState.tempListingId).fold(
                onSuccess = { urls ->
                    createListingState = createListingState.copy(
                        isUploadingImages = false,
                        uploadProgress = 1f,
                        uploadedImageUrls = createListingState.uploadedImageUrls + urls
                    )
                },
                onFailure = { exception ->
                    createListingState = createListingState.copy(
                        isUploadingImages = false,
                        uploadProgress = 0f,
                        error = "Resim yükleme hatası: ${exception.message}"
                    )
                }
            )
        }
    }


    fun uploadSingleImage(imageUri: Uri) {
        createListingState = createListingState.copy(
            isUploadingImages = true,
            uploadProgress = 0f,
            error = null
        )

        viewModelScope.launch {
            imageRepository.uploadListingImage(imageUri, createListingState.tempListingId).fold(
                onSuccess = { url ->
                    createListingState = createListingState.copy(
                        isUploadingImages = false,
                        uploadProgress = 1f,
                        uploadedImageUrls = createListingState.uploadedImageUrls + url
                    )
                },
                onFailure = { exception ->
                    createListingState = createListingState.copy(
                        isUploadingImages = false,
                        uploadProgress = 0f,
                        error = "Resim yükleme hatası: ${exception.message}"
                    )
                }
            )
        }
    }


    fun removeUploadedImage(index: Int) {
        val currentUrls = createListingState.uploadedImageUrls.toMutableList()
        if (index in currentUrls.indices) {
            currentUrls.removeAt(index)
            createListingState = createListingState.copy(
                uploadedImageUrls = currentUrls
            )
        }
    }

    fun createListing(
        title: String,
        description: String,
        price: Double,
        location: String,
        machineType: String
    ) {
        if (title.isBlank() || description.isBlank() || price <= 0 || location.isBlank() || machineType.isBlank()) {
            createListingState = createListingState.copy(
                error = "Lütfen tüm alanları doldurun"
            )
            return
        }

        createListingState = createListingState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()

                if (currentUser == null) {
                    createListingState = createListingState.copy(
                        isLoading = false,
                        error = "Kullanıcı girişi yapılmamış"
                    )
                    return@launch
                }
                val listing = Listing(
                    id = "",
                    userId = currentUser.id,
                    title = title,
                    description = description,
                    price = price,
                    location = location,
                    imageUrls = createListingState.uploadedImageUrls,
                    machineType = machineType,
                    createdAt = Date(),
                    isActive = true
                )
                listingRepository.createListing(listing).fold(
                    onSuccess = {
                        createListingState = createListingState.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { exception ->
                        createListingState = createListingState.copy(
                            isLoading = false,
                            error = exception.message ?: "İlan oluşturulurken bir hata oluştu"
                        )
                    }
                )
            } catch (e: Exception) {
                createListingState = createListingState.copy(
                    isLoading = false,
                    error = e.message ?: "Beklenmeyen bir hata oluştu"
                )
            }
        }
    }

    fun resetState() {
        createListingState = CreateListingState()
    }

    fun clearError() {
        createListingState = createListingState.copy(error = null)
    }
} 