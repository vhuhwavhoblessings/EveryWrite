package com.example.everywrite.data.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Free geocoding - no API key needed!
const val GEOCODING_BASE_URL = "https://nominatim.openstreetmap.org/"

interface GeocodingApiService {
    @GET("search")
    suspend fun getCoordinates(
        @Query("q") city: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): Response<List<GeocodingResponse>>
}

data class GeocodingResponse(
    val lat: String,
    val lon: String,
    val display_name: String
)

object GeocodingService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(GEOCODING_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: GeocodingApiService = retrofit.create(GeocodingApiService::class.java)
}