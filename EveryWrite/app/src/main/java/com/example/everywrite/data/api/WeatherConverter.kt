package com.example.everywrite.data.api

object WeatherConverter {
    fun getWeatherEmoji(weatherCode: Int): String {
        return when (weatherCode) {
            in 0..1 -> "☀️"  // Clear, mainly clear
            in 2..3 -> "⛅"  // Partly cloudy
            in 45..48 -> "🌫️" // Fog
            in 51..55 -> "🌦️" // Drizzle
            in 61..65 -> "🌧️" // Rain
            in 66..67 -> "🌧️" // Freezing rain
            in 71..77 -> "❄️"  // Snow
            in 80..82 -> "🌧️" // Rain showers
            in 85..86 -> "❄️"  // Snow showers
            in 95..99 -> "⛈️"  // Thunderstorm
            else -> "🌈"
        }
    }

    fun getWeatherDescription(weatherCode: Int): String {
        return when (weatherCode) {
            in 0..1 -> "Sunny"
            in 2..3 -> "Partly Cloudy"
            in 45..48 -> "Foggy"
            in 51..55 -> "Drizzling"
            in 61..65 -> "Raining"
            in 66..67 -> "Freezing Rain"
            in 71..77 -> "Snowing"
            in 80..82 -> "Rain Showers"
            in 85..86 -> "Snow Showers"
            in 95..99 -> "Thunderstorm"
            else -> "Clear"
        }
    }
}