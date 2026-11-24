package com.ym.carbucheap.data.repository

import com.ym.carbucheap.data.model.ChargingStation
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.data.remote.IrveApiService
import com.ym.carbucheap.util.DistanceUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

class ChargingRepository(private val api: IrveApiService) {

    private data class CacheEntry(
        val timestamp: Long,
        val userLat: Double,
        val userLon: Double,
        val stations: List<ChargingStation>
    )

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    companion object {
        // The dataset is refreshed about once a day: a longer cache than fuel prices is fine.
        private const val CACHE_DURATION_MS = 60 * 60 * 1000L
        private const val CACHE_DISTANCE_THRESHOLD_KM = 1.0
        /** Nearest charge points fetched; grouped into roughly 30–80 stations in cities. */
        private const val MAX_CHARGE_POINTS = 500
    }

    suspend fun getStations(
        userLat: Double,
        userLon: Double,
        radiusKm: Int,
        connector: Connector?,
        minPowerKw: Int,
        forceRefresh: Boolean = false
    ): Result<List<ChargingStation>> {
        val cacheKey = "${connector?.name}_${minPowerKw}_$radiusKm"
        if (!forceRefresh) {
            cache[cacheKey]?.let { cached ->
                val age = System.currentTimeMillis() - cached.timestamp
                val moved = DistanceUtils.haversineDistance(userLat, userLon, cached.userLat, cached.userLon)
                if (age < CACHE_DURATION_MS && moved < CACHE_DISTANCE_THRESHOLD_KM) {
                    return Result.success(cached.stations)
                }
            }
        }

        return try {
            val records = api.getChargePoints(
                where = ChargingStationMapper.whereClause(userLat, userLon, radiusKm, connector, minPowerKw),
                select = ChargingStationMapper.SELECT_FIELDS,
                orderBy = ChargingStationMapper.orderBy(userLat, userLon),
                limit = MAX_CHARGE_POINTS
            )
            val stations = ChargingStationMapper.toStations(records, userLat, userLon)
            cache[cacheKey] = CacheEntry(System.currentTimeMillis(), userLat, userLon, stations)
            Result.success(stations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
