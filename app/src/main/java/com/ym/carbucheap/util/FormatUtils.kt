package com.ym.carbucheap.util

import java.util.Locale

object FormatUtils {

    fun formatPrice(price: Double): String {
        return String.format(Locale.FRANCE, "%.3f €/L", price)
    }

    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0) {
            String.format(Locale.FRANCE, "%d m", (distanceKm * 1000).toInt())
        } else {
            String.format(Locale.FRANCE, "%.1f km", distanceKm)
        }
    }
}

