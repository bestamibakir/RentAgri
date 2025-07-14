package com.bestamibakir.rentagri.ui.screens.listings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.Listing
import com.bestamibakir.rentagri.data.repository.ListingRepository
import com.bestamibakir.rentagri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListingsUiState(
    val listings: List<Listing> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false,
    val cacheInfo: String? = null,
    val searchQuery: String = "",
    val selectedMachineType: String? = null,
    val selectedCity: String? = null,
    val isRefreshing: Boolean = false
)


@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val tag = "ListingsViewModel"


    private val _uiState = MutableStateFlow(ListingsUiState())
    val uiState: StateFlow<ListingsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMachineType = MutableStateFlow<String?>(null)
    private val _selectedCity = MutableStateFlow<String?>(null)

    init {
        Log.d(tag, "ListingsViewModel initialized")
        observeListings()
        loadCacheInfo()
    }


    private fun observeListings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)


                listingRepository.getAllListingsFlow()
                    .catch { error ->
                        Log.e(tag, "Error observing listings", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "İlanlar yüklenirken hata oluştu: ${error.message}",
                            isOffline = true
                        )
                    }
                    .collect { listings ->
                        Log.d(tag, "Received ${listings.size} listings from flow")

                        val isFromCache = listings.firstOrNull()?.lastUpdated?.let { lastUpdate ->
                            System.currentTimeMillis() - lastUpdate.time > 5000
                        } ?: false

                        _uiState.value = _uiState.value.copy(
                            listings = listings,
                            isLoading = false,
                            error = null,
                            isOffline = isFromCache && listings.isNotEmpty(),
                            isRefreshing = false
                        )
                        loadCacheInfo()
                    }
            } catch (e: Exception) {
                Log.e(tag, "Error in observeListings", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "İlanlar yüklenirken hata oluştu: ${e.message}",
                    isRefreshing = false
                )
            }
        }
    }


    fun refreshListings() {
        viewModelScope.launch {
            Log.d(tag, "Refreshing listings")
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

            try {

                observeListings()
            } catch (e: Exception) {
                Log.e(tag, "Error refreshing listings", e)
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "Yenileme sırasında hata oluştu: ${e.message}"
                )
            }
        }
    }


    fun searchListings(query: String) {
        viewModelScope.launch {
            Log.d(tag, "Searching listings with query: $query")
            _searchQuery.value = query
            _uiState.value = _uiState.value.copy(searchQuery = query)

            if (query.isBlank()) {

                observeListings()
                return@launch
            }

            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = listingRepository.searchListings(
                    query = query,
                    machineType = _selectedMachineType.value,
                    city = _selectedCity.value
                )

                if (result.isSuccess) {
                    val listings = result.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        listings = listings,
                        isLoading = false,
                        error = null
                    )

                    Log.d(tag, "Search completed, found ${listings.size} listings")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Arama yapılırken hata oluştu: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error searching listings", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Arama yapılırken hata oluştu: ${e.message}"
                )
            }
        }
    }


    fun filterByMachineType(machineType: String?) {
        viewModelScope.launch {
            Log.d(tag, "Filtering by machine type: $machineType")
            _selectedMachineType.value = machineType
            _uiState.value = _uiState.value.copy(selectedMachineType = machineType)

            performSearch()
        }
    }


    fun filterByCity(city: String?) {
        viewModelScope.launch {
            Log.d(tag, "Filtering by city: $city")
            _selectedCity.value = city
            _uiState.value = _uiState.value.copy(selectedCity = city)

            performSearch()
        }
    }


    fun clearAllFilters() {
        viewModelScope.launch {
            Log.d(tag, "Clearing all filters")
            _searchQuery.value = ""
            _selectedMachineType.value = null
            _selectedCity.value = null

            _uiState.value = _uiState.value.copy(
                searchQuery = "",
                selectedMachineType = null,
                selectedCity = null
            )


            observeListings()
        }
    }


    fun clearCache() {
        viewModelScope.launch {
            try {
                listingRepository.clearCache()
                Log.d(tag, "Cache cleared successfully")


                observeListings()
                loadCacheInfo()
            } catch (e: Exception) {
                Log.e(tag, "Error clearing cache", e)
                _uiState.value = _uiState.value.copy(
                    error = "Cache temizlenirken hata oluştu: ${e.message}"
                )
            }
        }
    }


    fun checkConnectivity() {
        viewModelScope.launch {
            try {
                val result = listingRepository.testFirestoreConnection()
                val isOnline = result.isSuccess

                _uiState.value = _uiState.value.copy(
                    isOffline = !isOnline
                )

                Log.d(tag, "Connectivity check: ${if (isOnline) "Online" else "Offline"}")
            } catch (e: Exception) {
                Log.e(tag, "Error checking connectivity", e)
                _uiState.value = _uiState.value.copy(isOffline = true)
            }
        }
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }


    private suspend fun performSearch() {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val query = _searchQuery.value.takeIf { it.isNotBlank() }
            val machineType = _selectedMachineType.value
            val city = _selectedCity.value

            val result = if (query == null && machineType == null && city == null) {

                observeListings()
                return
            } else {
                listingRepository.searchListings(
                    query = query,
                    machineType = machineType,
                    city = city
                )
            }

            if (result.isSuccess) {
                val listings = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    listings = listings,
                    isLoading = false,
                    error = null
                )

                Log.d(tag, "Search completed, found ${listings.size} listings")
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Filtreleme yapılırken hata oluştu: ${result.exceptionOrNull()?.message}"
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error performing search", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Filtreleme yapılırken hata oluştu: ${e.message}"
            )
        }
    }

    private fun loadCacheInfo() {
        viewModelScope.launch {
            try {
                val (count, lastUpdate) = listingRepository.getCacheStats()
                val cacheInfo = if (count > 0) {
                    val updateTime = lastUpdate?.let {
                        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(it))
                    } ?: "Bilinmiyor"
                    "$count ilan cached (Son güncelleme: $updateTime)"
                } else {
                    "Cache boş"
                }

                _uiState.value = _uiState.value.copy(cacheInfo = cacheInfo)
            } catch (e: Exception) {
                Log.e(tag, "Error loading cache info", e)
            }
        }
    }
} 