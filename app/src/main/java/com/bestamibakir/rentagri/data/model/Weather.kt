package com.bestamibakir.rentagri.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val main: WeatherMain,
    val weather: List<WeatherInfo>,
    val name: String
)

@Serializable
data class WeatherMain(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

@Serializable
data class WeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) 