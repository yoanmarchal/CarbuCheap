package com.ym.carbucheap.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class ApiResponse(
    @param:Json(name = "total_count") val totalCount: Int?,
    @param:Json(name = "results") val results: List<StationRecord>?
)

@JsonClass(generateAdapter = false)
data class StationRecord(
    @param:Json(name = "id") val id: Long?,
    @param:Json(name = "adresse") val adresse: String?,
    @param:Json(name = "ville") val ville: String?,
    @param:Json(name = "cp") val cp: String?,
    @param:Json(name = "geom") val geom: GeoPoint?,
    @param:Json(name = "gazole_prix") val gazolePrix: Double?,
    @param:Json(name = "gazole_maj") val gazoleMaj: String?,
    @param:Json(name = "sp95_prix") val sp95Prix: Double?,
    @param:Json(name = "sp95_maj") val sp95Maj: String?,
    @param:Json(name = "sp98_prix") val sp98Prix: Double?,
    @param:Json(name = "sp98_maj") val sp98Maj: String?,
    @param:Json(name = "e85_prix") val e85Prix: Double?,
    @param:Json(name = "e85_maj") val e85Maj: String?,
    @param:Json(name = "e10_prix") val e10Prix: Double?,
    @param:Json(name = "e10_maj") val e10Maj: String?,
    @param:Json(name = "gplc_prix") val gplcPrix: Double?,
    @param:Json(name = "gplc_maj") val gplcMaj: String?,
    @param:Json(name = "carburants_disponibles") val carburantsDisponibles: List<String>?,
    @param:Json(name = "services_service") val services: List<String>?,
    @param:Json(name = "horaires_automate_24_24") val automate24: String?
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
    @param:Json(name = "lon") val lon: Double?,
    @param:Json(name = "lat") val lat: Double?
)

