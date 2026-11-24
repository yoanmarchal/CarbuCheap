package com.ym.carbucheap.data.model

enum class Connector(val displayName: String, val shortName: String, val apiField: String) {
    CCS("CCS Combo", "CCS", "prise_type_combo_ccs"),
    TYPE_2("Type 2", "T2", "prise_type_2"),
    CHADEMO("CHAdeMO", "CHAdeMO", "prise_type_chademo"),
    DOMESTIC("Domestique", "E/F", "prise_type_ef")
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
    /** Cheapest / most expensive €/kWh found in the operator's tariff text, null if unknown. */
    val minPricePerKwh: Double?,
    val maxPricePerKwh: Double?,
    /** Idle fee once charging is over (€/h), when published. */
    val parkingFeePerHour: Double?,
    /** Operator's tariff page, when the dataset only gives a link. */
    val pricingUrl: String?,
    val isOpen24h: Boolean,
    val lastUpdate: String
)
