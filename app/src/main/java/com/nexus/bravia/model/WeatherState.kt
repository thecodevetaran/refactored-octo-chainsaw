package com.nexus.bravia.model

sealed interface WeatherState {
    data object Disabled : WeatherState
    data class Loading(val city: String) : WeatherState
    data class Ready(
        val city: String,
        val temperatureC: Double,
        val condition: String,
        val windKmh: Double
    ) : WeatherState
    data class Error(val city: String, val message: String) : WeatherState
}
