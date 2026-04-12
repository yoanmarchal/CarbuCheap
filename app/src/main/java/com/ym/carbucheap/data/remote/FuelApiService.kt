package com.ym.carbucheap.data.remote

import com.ym.carbucheap.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FuelApiService {

    @GET("records")
    suspend fun getStations(
        @Query("where") where: String,
        @Query("limit") limit: Int = 50,
        @Query("refine") refine: String? = null,
        @Query("order_by") orderBy: String? = null
    ): ApiResponse
}

