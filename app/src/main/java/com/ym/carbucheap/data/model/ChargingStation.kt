package com.ym.carbucheap.data.model

enum class Connector(val displayName: String, val apiField: String) {
    CCS("CCS Combo", "prise_type_combo_ccs"),
    TYPE_2("Type 2", "prise_type_2"),
    CHADEMO("CHAdeMO", "prise_type_chademo"),
    DOMESTIC("Domestique", "prise_type_ef")
}

/** One charging site, aggregated from its individual charge points (PDC). */
data class ChargingStation(
    val id: String,
    val name: String,
    val operator: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double, // km
    val maxPowerKw: Double,
    val pointCount: Int,
    val connectors: Set<Connector>,
    val isFree: Boolean,
    /** Parsed €/kWh when the operator published a simple tariff, else null. */
    val pricePerKwh: Double?,
    val isOpen24h: Boolean,
    val lastUpdate: String
)
