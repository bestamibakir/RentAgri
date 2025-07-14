package com.bestamibakir.rentagri.data.repository

import com.bestamibakir.rentagri.data.api.WeatherApi
import com.bestamibakir.rentagri.data.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {

    private val apiKey = "4596fa41ba0d2004e243d84cdd883336"

    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> {
        return try {
            val response = weatherApi.getWeatherByCity(
                cityName = cityName,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 