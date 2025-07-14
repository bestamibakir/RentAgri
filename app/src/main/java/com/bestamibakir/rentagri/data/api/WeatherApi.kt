package com.bestamibakir.rentagri.data.api

import com.bestamibakir.rentagri.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "tr",
        @Query("appid") apiKey: String
    ): WeatherResponse
} 