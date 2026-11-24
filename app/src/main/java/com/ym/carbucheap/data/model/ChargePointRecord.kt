package com.ym.carbucheap.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * One charge point (PDC) from the ODRE "bornes-irve" dataset.
 * Booleans come as free-form strings ("true", "True", "TRUE", "1", "false"…).
 */
@JsonClass(generateAdapter = false)
data class ChargePointRecord(
    @param:Json(name = "id_station_itinerance") val stationId: String? = null,
    @param:Json(name = "nom_station") val stationName: String? = null,
    @param:Json(name = "adresse_station") val address: String? = null,
    @param:Json(name = "nom_operateur") val operator: String? = null,
    @param:Json(name = "nom_enseigne") val brand: String? = null,
    @param:Json(name = "puissance_nominale") val powerKw: Double? = null,
    @param:Json(name = "prise_type_combo_ccs") val ccs: String? = null,
    @param:Json(name = "prise_type_2") val type2: String? = null,
    @param:Json(name = "prise_type_chademo") val chademo: String? = null,
    @param:Json(name = "prise_type_ef") val domestic: String? = null,
    @param:Json(name = "gratuit") val free: String? = null,
    @param:Json(name = "tarification") val pricing: String? = null,
    @param:Json(name = "horaires") val openingHours: String? = null,
    @param:Json(name = "date_maj") val lastUpdate: String? = null,
    // The consolidated_* fields have lat/lon in the right order (coordonneesxy is swapped).
    @param:Json(name = "consolidated_latitude") val latitude: Double? = null,
    @param:Json(name = "consolidated_longitude") val longitude: Double? = null
)
