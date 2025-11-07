package com.example.everywrite.data.api

// Simple weather service without API calls
object SimpleWeatherService {
    fun getWeatherForCity(city: String): String {
        val cityWeather = mapOf(
            "london" to "🌧️ Rainy, 15°C",
            "paris" to "⛅ Cloudy, 18°C",
            "new york" to "☀️ Sunny, 22°C",
            "tokyo" to "☀️ Sunny, 25°C",
            "sydney" to "☀️ Sunny, 28°C",
            "berlin" to "⛅ Cloudy, 16°C",
            "rome" to "☀️ Sunny, 24°C",
            "madrid" to "☀️ Sunny, 26°C",
            "amsterdam" to "🌧️ Rainy, 14°C",
            "dublin" to "🌧️ Rainy, 13°C",
            "moscow" to "❄️ Snowy, -5°C",
            "dubai" to "☀️ Sunny, 35°C"
        )

        return cityWeather[city.lowercase()] ?: "🌈 Beautiful day, 20°C"
    }

    fun getWeatherEmoji(weatherText: String): String {
        return when {
            weatherText.contains("☀️") -> "☀️"
            weatherText.contains("⛅") -> "⛅"
            weatherText.contains("🌧️") -> "🌧️"
            weatherText.contains("❄️") -> "❄️"
            weatherText.contains("⛈️") -> "⛈️"
            else -> "🌈"
        }
    }
}