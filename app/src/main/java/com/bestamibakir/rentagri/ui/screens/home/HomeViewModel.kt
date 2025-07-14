package com.bestamibakir.rentagri.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.User
import com.bestamibakir.rentagri.data.repository.UserRepository
import com.bestamibakir.rentagri.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isWeatherLoading: Boolean = false,
    val weatherTemperature: Double? = null,
    val weatherDescription: String? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    var homeState by mutableStateOf(HomeState())
        private set

    fun loadUserData() {
        homeState = homeState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                homeState = homeState.copy(
                    user = user,
                    isLoading = false
                )

                user?.city?.let { city ->
                    if (city.isNotBlank()) {
                        loadWeatherData(city)
                    }
                }
            } catch (e: Exception) {
                homeState = homeState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadWeatherData(city: String = homeState.user?.city ?: "") {
        if (city.isBlank()) return

        homeState = homeState.copy(isWeatherLoading = true)

        viewModelScope.launch {
            weatherRepository.getWeatherByCity(city).fold(
                onSuccess = { response ->
                    homeState = homeState.copy(
                        isWeatherLoading = false,
                        weatherTemperature = response.main.temp,
                        weatherDescription = response.weather.firstOrNull()?.description ?: ""
                    )
                },
                onFailure = { exception ->
                    homeState = homeState.copy(
                        isWeatherLoading = false,
                        error = exception.message
                    )
                }
            )
        }
    }
} 