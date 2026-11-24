package com.ym.carbucheap.data.repository

import kotlin.coroutines.cancellation.CancellationException
import android.util.Log
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.data.remote.FuelApiService
import com.ym.carbucheap.util.DistanceUtils
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "FuelRepository"

class FuelRepository(
    private val api: FuelApiService,
    /** id → brand name; may be empty. */
    private val stationNames: suspend () -> Map<String, String> = { emptyMap() }
) {

    private data class CacheEntry(
        val timestamp: Long,
        val userLat: Double,
        val userLon: Double,
        val stations: List<Station>
    )

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    companion object {
        private const val CACHE_DURATION_MS = 15 * 60 * 1000L // 15 minutes
        private const val CACHE_DISTANCE_THRESHOLD_KM = 1.0 // invalidate if user moved > 1km
    }

    suspend fun getStations(
        userLat: Double,
        userLon: Double,
        fuelType: FuelType,
        radiusKm: Int = 10,
        forceRefresh: Boolean = false
    ): Result<List<Station>> {
        // Check cache
        val cacheKey = "${fuelType.name}_$radiusKm"
        if (!forceRefresh) {
            val cached = cache[cacheKey]
            if (cached != null) {
                val age = System.currentTimeMillis() - cached.timestamp
                val moved = DistanceUtils.haversineDistance(
                    userLat, userLon, cached.userLat, cached.userLon
                )
                if (age < CACHE_DURATION_MS && moved < CACHE_DISTANCE_THRESHOLD_KM) {
                    return Result.success(cached.stations)
                }
            }
        }

        return try {
            val whereClause = String.format(
                Locale.US,
                "within_distance(geom, geom'POINT(%.6f %.6f)', %dkm) AND %s is not null",
                userLon, userLat, radiusKm, fuelType.prixField
            )

            Log.d(TAG, "API request — lat=$userLat, lon=$userLon, fuel=${fuelType.name}")
            Log.d(TAG, "where=$whereClause")

            val response = api.getStations(
                where = whereClause,
                limit = 50,
                orderBy = fuelType.prixField
            )

            Log.d(TAG, "API response — totalCount=${response.totalCount}, results=${response.results?.size ?: 0}")

            val names = stationNames()
            val stations = (response.results ?: emptyList())
                .mapNotNull { record ->
                    StationMapper.toStation(record, fuelType, userLat, userLon, names[record.id?.toString()])
                }
                .sortedBy { it.price }

            // Update cache
            cache[cacheKey] = CacheEntry(
                timestamp = System.currentTimeMillis(),
                userLat = userLat,
                userLon = userLon,
                stations = stations
            )

            Result.success(stations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        cache.clear()
    }
}


