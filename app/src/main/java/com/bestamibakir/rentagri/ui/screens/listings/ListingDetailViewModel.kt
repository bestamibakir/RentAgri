package com.bestamibakir.rentagri.ui.screens.listings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Listing
import com.bestamibakir.rentagri.data.model.Offer
import com.bestamibakir.rentagri.data.model.User
import com.bestamibakir.rentagri.data.repository.ListingRepository
import com.bestamibakir.rentagri.data.repository.OfferRepository
import com.bestamibakir.rentagri.data.repository.UserRepository
import com.bestamibakir.rentagri.ui.navigation.NavDestinations
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ListingDetailState(
    val listing: Listing? = null,
    val owner: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isOwner: Boolean = false,
    val acceptedOffer: Offer? = null
)

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val userRepository: UserRepository,
    private val offerRepository: OfferRepository,
    private val firestore: FirebaseFirestore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listingId: String = checkNotNull(savedStateHandle[NavDestinations.LISTING_ID_ARG])

    var detailState by mutableStateOf(ListingDetailState(isLoading = true))
        private set

    init {
        loadListingDetail()
    }

    private fun loadListingDetail() {
        detailState = detailState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                Log.d("ListingDetailViewModel", "Loading listing details for ID: $listingId")

                listingRepository.getListingById(listingId).fold(
                    onSuccess = { listing ->
                        if (listing != null) {
                            detailState = detailState.copy(listing = listing)


                            val currentUser = userRepository.getCurrentUser()


                            val isOwner = currentUser?.id == listing.userId
                            detailState = detailState.copy(isOwner = isOwner)

                            loadOwnerInfo(listing.userId)
                        } else {
                            detailState = detailState.copy(
                                isLoading = false,
                                error = "İlan bulunamadı"
                            )
                        }
                    },
                    onFailure = { exception ->
                        detailState = detailState.copy(
                            isLoading = false,
                            error = exception.message
                                ?: "İlan detayları yüklenirken bir hata oluştu"
                        )
                    }
                )
            } catch (e: Exception) {
                detailState = detailState.copy(
                    isLoading = false,
                    error = e.message ?: "Beklenmeyen bir hata oluştu"
                )
            }
        }
    }

    private fun loadOwnerInfo(ownerId: String) {
        viewModelScope.launch {
            try {
                Log.d("ListingDetailViewModel", "Loading owner info for ID: $ownerId")

                val snapshot = firestore.collection("users").document(ownerId).get().await()
                val owner = snapshot.toObject(User::class.java)
                Log.d("ListingDetailViewModel", "Owner loaded successfully: ${owner?.firstName}")

                detailState = detailState.copy(
                    owner = owner,
                    isLoading = false
                )


                loadAcceptedOffer()
            } catch (e: Exception) {
                Log.e("ListingDetailViewModel", "Error loading owner info", e)
                detailState = detailState.copy(
                    isLoading = false,
                    error = e.message ?: "Kullanıcı bilgileri yüklenirken bir hata oluştu"
                )
            }
        }
    }

    private fun loadAcceptedOffer() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {

                    offerRepository.getAcceptedOfferForListing(listingId, currentUser.id).fold(
                        onSuccess = { acceptedOffer ->
                            detailState = detailState.copy(acceptedOffer = acceptedOffer)
                            Log.d(
                                "ListingDetailViewModel",
                                "Accepted offer loaded: ${acceptedOffer?.id}"
                            )
                        },
                        onFailure = { exception ->
                            Log.e(
                                "ListingDetailViewModel",
                                "Error loading accepted offer",
                                exception
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ListingDetailViewModel", "Error in loadAcceptedOffer", e)
            }
        }
    }

    fun deleteListing() {
        if (detailState.isOwner && detailState.listing != null) {
            viewModelScope.launch {
                try {
                    listingRepository.deleteListing(listingId).fold(
                        onSuccess = {
                            detailState = detailState.copy(listing = null)
                        },
                        onFailure = { exception ->
                            detailState = detailState.copy(
                                error = exception.message ?: "İlan silinirken bir hata oluştu"
                            )
                        }
                    )
                } catch (e: Exception) {
                    detailState = detailState.copy(
                        error = e.message ?: "Beklenmeyen bir hata oluştu",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun clearMessages() {
        detailState = detailState.copy(
            error = null,
            successMessage = null
        )
    }

    fun makeOffer(amount: Double, message: String) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                val listing = detailState.listing

                if (currentUser != null && listing != null) {
                    offerRepository.hasUserMadeOffer(currentUser.id, listingId).fold(
                        onSuccess = { hasOffer ->
                            if (hasOffer) {
                                detailState = detailState.copy(
                                    error = "Bu ilan için zaten bir teklifiniz var",
                                    successMessage = null
                                )
                            } else {
                                val offer = Offer(
                                    listingId = listingId,
                                    buyerId = currentUser.id,
                                    sellerId = listing.userId,
                                    amount = amount,
                                    message = message
                                )

                                offerRepository.createOffer(offer).fold(
                                    onSuccess = {
                                        detailState = detailState.copy(
                                            error = null,
                                            successMessage = "Teklifiniz başarıyla gönderildi!"
                                        )

                                        loadAcceptedOffer()
                                    },
                                    onFailure = { exception ->
                                        detailState = detailState.copy(
                                            error = exception.message
                                                ?: "Teklif gönderilirken hata oluştu",
                                            successMessage = null
                                        )
                                    }
                                )
                            }
                        },
                        onFailure = { exception ->
                            detailState = detailState.copy(
                                error = exception.message ?: "Teklif kontrol edilirken hata oluştu",
                                successMessage = null
                            )
                        }
                    )
                } else {
                    detailState = detailState.copy(
                        error = "Teklif vermek için giriş yapmanız gerekiyor",
                        successMessage = null
                    )
                }
            } catch (e: Exception) {
                detailState = detailState.copy(
                    error = e.message ?: "Beklenmeyen bir hata oluştu"
                )
            }
        }
    }
} 