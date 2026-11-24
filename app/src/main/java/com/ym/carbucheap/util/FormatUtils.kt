package com.ym.carbucheap.util

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale

object FormatUtils {

    /** Prices older than this are flagged as possibly outdated. */
    val STALE_THRESHOLD: Duration = Duration.ofHours(48)

    fun formatPrice(price: Double): String {
        return String.format(Locale.FRANCE, "%.3f €/L", price)
    }

    fun formatEuros(amount: Double): String {
        return String.format(Locale.FRANCE, "%.2f €", amount)
    }

    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0) {
            String.format(Locale.FRANCE, "%d m", (distanceKm * 1000).toInt())
        } else {
            String.format(Locale.FRANCE, "%.1f km", distanceKm)
        }
    }

    /** Parses API timestamps such as "2026-11-12T06:55:20+00:00". */
    fun parseUpdate(iso: String): Instant? = try {
        if (iso.isBlank()) null else OffsetDateTime.parse(iso).toInstant()
    } catch (e: DateTimeParseException) {
        null
    }

    /** "MAJ il y a 3 h" style label, or null when the date is unknown. */
    fun formatUpdateAge(iso: String, now: Instant = Instant.now()): String? {
        val updated = parseUpdate(iso) ?: return null
        val age = Duration.between(updated, now).coerceAtLeast(Duration.ZERO)
        return when {
            age.toMinutes() < 1 -> "MAJ à l'instant"
            age.toHours() < 1 -> "MAJ il y a ${age.toMinutes()} min"
            age.toDays() < 1 -> "MAJ il y a ${age.toHours()} h"
            else -> "MAJ il y a ${age.toDays()} j"
        }
    }

    fun isStale(iso: String, now: Instant = Instant.now()): Boolean {
        val updated = parseUpdate(iso) ?: return false
        return Duration.between(updated, now) > STALE_THRESHOLD
    }
}
