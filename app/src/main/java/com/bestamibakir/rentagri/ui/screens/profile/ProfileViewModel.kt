package com.bestamibakir.rentagri.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Listing
import com.bestamibakir.rentagri.data.model.Offer
import com.bestamibakir.rentagri.data.model.User
import com.bestamibakir.rentagri.data.repository.ListingRepository
import com.bestamibakir.rentagri.data.repository.OfferRepository
import com.bestamibakir.rentagri.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val userListings: List<Listing> = emptyList(),
    val receivedOffers: List<Offer> = emptyList(),
    val sentOffers: List<Offer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val listingRepository: ListingRepository,
    private val offerRepository: OfferRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    var profileState by mutableStateOf(ProfileState(isLoading = true))
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            profileState = profileState.copy(isLoading = true, error = null)
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {

                    val userListingsResult = listingRepository.getListingsByUserId(user.id)
                    val userListings = userListingsResult.getOrNull() ?: emptyList()

                    val receivedOffersResult = offerRepository.getOffersForSeller(user.id)
                    val receivedOffers = receivedOffersResult.getOrNull() ?: emptyList()

                    val sentOffersResult = offerRepository.getOffersFromBuyer(user.id)
                    val sentOffers = sentOffersResult.getOrNull() ?: emptyList()

                    profileState = profileState.copy(
                        user = user,
                        userListings = userListings,
                        receivedOffers = receivedOffers,
                        sentOffers = sentOffers,
                        isLoading = false
                    )
                } else {
                    profileState = profileState.copy(
                        isLoading = false,
                        error = "Kullanıcı bilgileri bulunamadı"
                    )
                }
            } catch (e: Exception) {
                profileState = profileState.copy(
                    isLoading = false,
                    error = "Profil bilgileri yüklenirken bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {

                auth.signOut()

                userRepository.logout()

                profileState = ProfileState(
                    user = null,
                    userListings = emptyList(),
                    receivedOffers = emptyList(),
                    sentOffers = emptyList(),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                profileState = profileState.copy(
                    error = e.message ?: "Çıkış yaparken bir hata oluştu"
                )
            }
        }
    }

    fun refreshProfile() {
        loadProfile()
    }

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            try {
                listingRepository.deleteListing(listingId)
                loadProfile()
            } catch (e: Exception) {
                profileState = profileState.copy(
                    error = "İlan silinirken bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun updateOfferStatus(
        offerId: String,
        status: com.bestamibakir.rentagri.data.model.OfferStatus
    ) {
        viewModelScope.launch {
            try {

                if (status == com.bestamibakir.rentagri.data.model.OfferStatus.ACCEPTED) {
                    val offerResult = offerRepository.getOfferById(offerId)
                    offerResult.fold(
                        onSuccess = { offer ->
                            if (offer != null) {

                                offerRepository.updateOfferStatusWithContactInfo(
                                    offerId = offerId,
                                    status = status,
                                    buyerId = offer.buyerId,
                                    sellerId = offer.sellerId
                                ).fold(
                                    onSuccess = {
                                        loadProfile()
                                    },
                                    onFailure = { error ->
                                        profileState = profileState.copy(
                                            error = "Teklif kabul edilirken bir hata oluştu: ${error.message}"
                                        )
                                    }
                                )
                            } else {
                                profileState = profileState.copy(
                                    error = "Teklif bulunamadı"
                                )
                            }
                        },
                        onFailure = { error ->
                            profileState = profileState.copy(
                                error = "Teklif bilgileri alınırken bir hata oluştu: ${error.message}"
                            )
                        }
                    )
                } else {

                    offerRepository.updateOfferStatus(offerId, status).fold(
                        onSuccess = {
                            loadProfile()
                        },
                        onFailure = { error ->
                            profileState = profileState.copy(
                                error = "Teklif durumu güncellenirken bir hata oluştu: ${error.message}"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                profileState = profileState.copy(
                    error = "Teklif durumu güncellenirken bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun debugFirestoreData() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    Log.d("ProfileViewModel", "=== DEBUG FIRESTORE DATA ===")
                    Log.d("ProfileViewModel", "Current User: ${currentUser}")
                    Log.d("ProfileViewModel", "User ID: ${currentUser.id}")


                    listingRepository.testFirestoreConnection().fold(
                        onSuccess = { message ->
                            Log.d("ProfileViewModel", "Firestore connection test: $message")
                        },
                        onFailure = { error ->
                            Log.e("ProfileViewModel", "Firestore connection test failed", error)
                        }
                    )


                    offerRepository.getOffersForSeller(currentUser.id).fold(
                        onSuccess = { offers ->
                            Log.d("ProfileViewModel", "Offers for user: ${offers.size}")
                            offers.forEach { offer ->
                                Log.d(
                                    "ProfileViewModel",
                                    "Offer: ${offer.id}, sellerId: ${offer.sellerId}, buyerId: ${offer.buyerId}"
                                )
                            }
                        },
                        onFailure = { error ->
                            Log.e("ProfileViewModel", "Error getting offers", error)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Debug error", e)
            }
        }
    }
} 