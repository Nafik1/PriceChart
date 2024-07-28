package com.example.pricechart.data.network

import com.example.pricechart.BuildConfig
import com.example.pricechart.data.model.BarListDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{timeframe}/2023-01-09/2024-07-10?adjusted=true&sort=desc&limit=50000&apiKey=${BuildConfig.API_KEY}")
    suspend fun loadBars(
        @Path("timeframe") timeFrame: String
    ) : BarListDto
}