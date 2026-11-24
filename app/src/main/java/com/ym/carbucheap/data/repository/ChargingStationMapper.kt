package com.ym.carbucheap.data.repository

import com.ym.carbucheap.data.model.ChargePointRecord
import com.ym.carbucheap.data.model.ChargingStation
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.util.DistanceUtils
import java.util.Locale

/** Builds ODSQL queries for the IRVE dataset and aggregates charge points into stations. Pure. */
object ChargingStationMapper {

    private val TRUTHY = setOf("true", "1")
    private val TRUTHY_SQL = "('true','True','TRUE','1')"

    private val POSTAL_CODE = Regex("""\b(\d{5})\b""")
    private val ALWAYS_OPEN = Regex("""^\s*(24/7|Mo-Su 00:00-(23:5\d|24:00))\s*$""")

    val SELECT_FIELDS = listOf(
        "id_station_itinerance", "nom_station", "adresse_station", "nom_operateur", "nom_enseigne",
        "puissance_nominale", "prise_type_combo_ccs", "prise_type_2", "prise_type_chademo",
        "prise_type_ef", "gratuit", "tarification", "horaires", "date_maj",
        "consolidated_latitude", "consolidated_longitude"
    ).joinToString(",")

    /**
     * WARNING: the dataset's geo index (`coordonneesxy`) stores lat and lon swapped,
     * so the WKT point must be written POINT(lat lon) — the reverse of the WKT standard.
     */
    fun geoPoint(userLat: Double, userLon: Double): String =
        String.format(Locale.US, "geom'POINT(%.6f %.6f)'", userLat, userLon)

    fun whereClause(
        userLat: Double,
        userLon: Double,
        radiusKm: Int,
        connector: Connector?,
        minPowerKw: Int
    ): String = buildList {
        add("within_distance(coordonneesxy, ${geoPoint(userLat, userLon)}, ${radiusKm}km)")
        if (minPowerKw > 0) add("puissance_nominale >= $minPowerKw")
        if (connector != null) add("${connector.apiField} in $TRUTHY_SQL")
    }.joinToString(" and ")

    fun orderBy(userLat: Double, userLon: Double): String =
        "distance(coordonneesxy, ${geoPoint(userLat, userLon)})"

    /** Groups charge points by station, drops records with unusable data, sorts by distance. */
    fun toStations(
        records: List<ChargePointRecord>,
        userLat: Double,
        userLon: Double
    ): List<ChargingStation> {
        val stations = records
            .filter { !it.stationId.isNullOrBlank() && it.latitude != null && it.longitude != null }
            .groupBy { it.stationId!! }
            .map { (id, points) -> toStation(id, points, userLat, userLon) }

        return dropBogusCoordinates(stations).sortedBy { it.distance }
    }

    private fun toStation(
        id: String,
        points: List<ChargePointRecord>,
        userLat: Double,
        userLon: Double
    ): ChargingStation {
        val first = points.first()
        val tariffs = points.mapNotNull { it.pricing }.distinct().map(TariffParser::parse)
        val lat = first.latitude!!
        val lon = first.longitude!!
        return ChargingStation(
            id = id,
            name = first.stationName?.trim()?.takeIf { it.isNotEmpty() }
                ?: first.brand?.trim()?.takeIf { it.isNotEmpty() }
                ?: first.operator?.trim().orEmpty().ifEmpty { "Borne de recharge" },
            operator = first.operator?.trim().orEmpty(),
            brand = first.brand?.trim().orEmpty(),
            address = first.address?.trim().orEmpty(),
            latitude = lat,
            longitude = lon,
            distance = DistanceUtils.haversineDistance(userLat, userLon, lat, lon),
            maxPowerKw = points.maxOf { it.powerKw ?: 0.0 },
            pointCount = points.size,
            connectors = Connector.entries.filter { c -> points.any { isTrue(it.flagFor(c)) } }.toSet(),
            isFree = points.any { isTrue(it.free) },
            minPricePerKwh = tariffs.mapNotNull { it.minPerKwh }.minOrNull(),
            maxPricePerKwh = tariffs.mapNotNull { it.maxPerKwh }.maxOrNull(),
            parkingFeePerHour = tariffs.mapNotNull { it.parkingFeePerHour }.minOrNull(),
            pricingUrl = tariffs.firstNotNullOfOrNull { it.url },
            isOpen24h = points.any { it.openingHours?.let(ALWAYS_OPEN::matches) == true },
            lastUpdate = points.mapNotNull { it.lastUpdate }.maxOrNull().orEmpty()
        )
    }

    /**
     * Some operators geolocate all their sites at their head office: stations from
     * different départements then share the exact same coordinates. Drop those.
     */
    private fun dropBogusCoordinates(stations: List<ChargingStation>): List<ChargingStation> {
        val bogus = stations
            .groupBy { it.latitude to it.longitude }
            .filterValues { group ->
                group.mapNotNull { departmentOf(it.address) }.distinct().size > 1
            }
            .keys
        return stations.filter { (it.latitude to it.longitude) !in bogus }
    }

    internal fun departmentOf(address: String): String? =
        POSTAL_CODE.findAll(address).lastOrNull()?.groupValues?.get(1)?.take(2)

    internal fun isTrue(value: String?): Boolean =
        value?.trim()?.lowercase() in TRUTHY

    private fun ChargePointRecord.flagFor(connector: Connector): String? = when (connector) {
        Connector.CCS -> ccs
        Connector.TYPE_2 -> type2
        Connector.CHADEMO -> chademo
        Connector.DOMESTIC -> domestic
    }
}
