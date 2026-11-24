package com.ym.carbucheap.data.remote

import com.ym.carbucheap.data.model.ChargePointRecord
import retrofit2.http.GET
import retrofit2.http.Query

/** ODRE "bornes-irve" dataset (Opendatasoft Explore v2.1). */
interface IrveApiService {

    /**
     * Uses the exports endpoint rather than /records: it has no 100-row cap,
     * and grouping can't be combined with distance ordering server-side.
     */
    @GET("exports/json")
    suspend fun getChargePoints(
        @Query("where") where: String,
        @Query("select") select: String,
        @Query("order_by") orderBy: String,
        @Query("limit") limit: Int
    ): List<ChargePointRecord>
}
