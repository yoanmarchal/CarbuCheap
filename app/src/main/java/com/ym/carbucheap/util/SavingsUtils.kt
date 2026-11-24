package com.ym.carbucheap.util

import com.ym.carbucheap.data.model.Station

object SavingsUtils {

    const val FULL_TANK_LITERS = 50.0

    /**
     * Median price of the listed stations, used as the "typical price around here" reference.
     * The median (not the nearest station or the mean) so that a single overpriced
     * station nearby does not inflate the displayed savings.
     */
    fun medianPrice(stations: List<Station>): Double? {
        if (stations.isEmpty()) return null
        val sorted = stations.map { it.price }.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[mid] else (sorted[mid - 1] + sorted[mid]) / 2
    }

    /**
     * Money saved on a [liters] fill-up compared to [referencePrice].
     * Returns null when there is no meaningful saving (< 1 cent).
     */
    fun fullTankSavings(
        price: Double,
        referencePrice: Double?,
        liters: Double = FULL_TANK_LITERS
    ): Double? {
        if (referencePrice == null) return null
        val savings = (referencePrice - price) * liters
        return savings.takeIf { it >= 0.01 }
    }
}
