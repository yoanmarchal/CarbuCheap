package com.ym.carbucheap.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class ApiResponse(
    @Json(name = "total_count") val totalCount: Int?,
    @Json(name = "results") val results: List<StationRecord>?
)

@JsonClass(generateAdapter = false)
data class StationRecord(
    @Json(name = "id") val id: Long?,
    @Json(name = "adresse") val adresse: String?,
    @Json(name = "ville") val ville: String?,
    @Json(name = "cp") val cp: String?,
    @Json(name = "geom") val geom: GeoPoint?,
    @Json(name = "gazole_prix") val gazolePrix: Double?,
    @Json(name = "gazole_maj") val gazoleMaj: String?,
    @Json(name = "sp95_prix") val sp95Prix: Double?,
    @Json(name = "sp95_maj") val sp95Maj: String?,
    @Json(name = "sp98_prix") val sp98Prix: Double?,
    @Json(name = "sp98_maj") val sp98Maj: String?,
    @Json(name = "e85_prix") val e85Prix: Double?,
    @Json(name = "e85_maj") val e85Maj: String?,
    @Json(name = "e10_prix") val e10Prix: Double?,
    @Json(name = "e10_maj") val e10Maj: String?,
    @Json(name = "gplc_prix") val gplcPrix: Double?,
    @Json(name = "gplc_maj") val gplcMaj: String?,
    @Json(name = "carburants_disponibles") val carburantsDisponibles: List<String>?,
    @Json(name = "services_service") val services: List<String>?,
    @Json(name = "horaires_automate_24_24") val automate24: String?
) {
    fun getPriceForFuel(fuelType: FuelType): Double? = when (fuelType) {
        FuelType.GAZOLE -> gazolePrix
        FuelType.SP95 -> sp95Prix
        FuelType.SP98 -> sp98Prix
        FuelType.E85 -> e85Prix
        FuelType.E10 -> e10Prix
        FuelType.GPLC -> gplcPrix
    }

    fun getLastUpdateForFuel(fuelType: FuelType): String? = when (fuelType) {
        FuelType.GAZOLE -> gazoleMaj
        FuelType.SP95 -> sp95Maj
        FuelType.SP98 -> sp98Maj
        FuelType.E85 -> e85Maj
        FuelType.E10 -> e10Maj
        FuelType.GPLC -> gplcMaj
    }
}

@JsonClass(generateAdapter = false)
data class GeoPoint(
    @Json(name = "lon") val lon: Double?,
    @Json(name = "lat") val lat: Double?
)

