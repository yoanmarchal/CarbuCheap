package com.ym.carbucheap.data.repository

import com.ym.carbucheap.data.model.FuelPrice
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.data.model.StationRecord
import com.ym.carbucheap.util.DistanceUtils

/** Maps raw API records to domain [Station]s. Pure — no Android dependencies. */
object StationMapper {

    /** Returns null when the record has no valid price for [fuelType] or no coordinates. */
    fun toStation(
        record: StationRecord,
        fuelType: FuelType,
        userLat: Double,
        userLon: Double,
        brand: String? = null
    ): Station? {
        val price = record.getPriceForFuel(fuelType)?.takeIf { it > 0 } ?: return null
        val lat = record.geom?.lat ?: return null
        val lon = record.geom.lon ?: return null

        val address = record.adresse.titlecased()
        val city = record.ville.titlecased()

        return Station(
            id = record.id?.toString() ?: "",
            name = listOf(address, city).filter { it.isNotEmpty() }
                .joinToString(", ").ifEmpty { "Station" },
            brand = brand?.trim()?.takeIf { it.isNotEmpty() },
            address = address,
            city = city,
            postalCode = record.cp ?: "",
            latitude = lat,
            longitude = lon,
            price = price,
            fuelType = fuelType.displayName,
            distance = DistanceUtils.haversineDistance(userLat, userLon, lat, lon),
            lastUpdate = record.getLastUpdateForFuel(fuelType) ?: "",
            allPrices = FuelType.entries.mapNotNull { type ->
                val p = record.getPriceForFuel(type)?.takeIf { it > 0 } ?: return@mapNotNull null
                FuelPrice(type, p, record.getLastUpdateForFuel(type) ?: "")
            },
            temporaryShortages = record.carburantsRuptureTemporaire
                ?.split(';')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),
            services = record.services.orEmpty().map { it.trim() }.filter { it.isNotEmpty() },
            isAutomate24h = record.automate24.equals("Oui", ignoreCase = true)
        )
    }

    private fun String?.titlecased(): String =
        this?.trim()?.replaceFirstChar { it.titlecase() } ?: ""
}
