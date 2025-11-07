package com.example.everywrite.data.api
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val WEATHER_BASE_URL = "https://api.open-meteo.com/v1/"

interface WeatherApiService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double = 51.5074, // London default
        @Query("longitude") longitude: Double = -0.1278,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("temperature_unit") unit: String = "celsius"
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)

object WeatherService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}