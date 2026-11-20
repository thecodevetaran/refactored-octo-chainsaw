package com.nexus.bravia.data

import com.nexus.bravia.model.WeatherState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Optional, zero-API-key weather provider powered by Open-Meteo.
 * Remains completely idle/offline unless the user configures a city in Settings.
 */
class WeatherRepository {

    suspend fun fetchWeather(city: String): WeatherState = withContext(Dispatchers.IO) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            return@withContext WeatherState.Disabled
        }

        try {
            val encodedCity = URLEncoder.encode(trimmed, "UTF-8")
            val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=en&format=json"
            val geoJson = JSONObject(httpGet(geoUrl))
            val results = geoJson.optJSONArray("results")
                ?: return@withContext WeatherState.Error(trimmed, "City not found")
            if (results.length() == 0) {
                return@withContext WeatherState.Error(trimmed, "City not found")
            }

            val place = results.getJSONObject(0)
            val lat = place.getDouble("latitude")
            val lon = place.getDouble("longitude")
            val resolvedName = place.optString("name", trimmed)

            val weatherUrl =
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code,wind_speed_10m"
            val weatherJson = JSONObject(httpGet(weatherUrl))
            val current = weatherJson.getJSONObject("current")

            val temp = current.getDouble("temperature_2m")
            val code = current.getInt("weather_code")
            val wind = current.optDouble("wind_speed_10m", 0.0)

            WeatherState.Ready(
                city = resolvedName,
                temperatureC = temp,
                condition = weatherCodeToLabel(code),
                windKmh = wind
            )
        } catch (e: Exception) {
            WeatherState.Error(trimmed, e.message ?: "Network unavailable")
        }
    }

    private fun httpGet(urlString: String): String {
        val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
            connectTimeout = 6000
            readTimeout = 6000
            requestMethod = "GET"
        }
        return conn.inputStream.bufferedReader().use { it.readText() }
    }

    private fun weatherCodeToLabel(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1, 2, 3 -> "Partly Cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain Showers"
        95, 96, 99 -> "Thunderstorm"
        else -> "Cloudy"
    }
}
