package com.ym.carbucheap.data.model

data class Station(
    val id: String,
    val name: String,
    /** Brand / station name ("TotalEnergies"…) when known, else null — the API has none. */
    val brand: String? = null,
    val address: String,
    val city: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val fuelType: String,
    val distance: Double, // in km
    val lastUpdate: String,
    /** Every fuel sold at this station with its current price (selected fuel included). */
    val allPrices: List<FuelPrice> = emptyList(),
    /** Display names of fuels temporarily out of stock. */
    val temporaryShortages: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val isAutomate24h: Boolean = false
) {
    /** Card / sheet title: the brand when known, else the street address. */
    val title: String get() = brand ?: address

    /** Second line: whatever the title doesn't already show. */
    val subtitle: String
        get() = if (brand != null) "$address, $postalCode $city" else "$postalCode $city"
}

data class FuelPrice(
    val fuelType: FuelType,
    val price: Double,
    val lastUpdate: String
)
