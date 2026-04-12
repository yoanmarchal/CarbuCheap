package com.ym.carbucheap.data.repository

import android.util.Log
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.data.remote.FuelApiService
import com.ym.carbucheap.util.DistanceUtils
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "FuelRepository"

class FuelRepository(private val api: FuelApiService) {

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

            val stations = (response.results ?: emptyList())
                .mapNotNull { record ->
                    val price = record.getPriceForFuel(fuelType)
                        ?: return@mapNotNull null
                    if (price <= 0) return@mapNotNull null

                    val lat = record.geom?.lat ?: return@mapNotNull null
                    val lon = record.geom.lon ?: return@mapNotNull null

                    Station(
                        id = record.id?.toString() ?: "",
                        name = buildStationName(record.adresse, record.ville),
                        address = record.adresse?.trim()?.replaceFirstChar { it.titlecase() } ?: "",
                        city = record.ville?.trim()?.replaceFirstChar { it.titlecase() } ?: "",
                        postalCode = record.cp ?: "",
                        latitude = lat,
                        longitude = lon,
                        price = price,
                        fuelType = fuelType.displayName,
                        distance = DistanceUtils.haversineDistance(userLat, userLon, lat, lon),
                        lastUpdate = record.getLastUpdateForFuel(fuelType) ?: ""
                    )
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildStationName(adresse: String?, ville: String?): String {
        val parts = listOfNotNull(
            adresse?.trim()?.replaceFirstChar { it.titlecase() },
            ville?.trim()?.replaceFirstChar { it.titlecase() }
        )
        return parts.joinToString(", ").ifEmpty { "Station" }
    }

    fun clearCache() {
        cache.clear()
    }
}


