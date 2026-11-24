package com.ym.carbucheap.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
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

    /** Parses API timestamps: "2026-11-12T06:55:20+00:00" or a plain date "2026-10-30" (read as UTC midnight). */
    fun parseUpdate(iso: String): Instant? = try {
        when {
            iso.isBlank() -> null
            iso.length == 10 -> LocalDate.parse(iso).atStartOfDay(ZoneOffset.UTC).toInstant()
            else -> OffsetDateTime.parse(iso).toInstant()
        }
    } catch (e: DateTimeParseException) {
        null
    }

    fun formatPower(kw: Double): String =
        if (kw % 1.0 == 0.0) "${kw.toInt()} kW" else String.format(Locale.FRANCE, "%.1f kW", kw)

    fun formatPricePerKwh(price: Double): String =
        String.format(Locale.FRANCE, "%.2f €/kWh", price)

    /** "0,29 €/kWh", or "0,29 – 0,39 €/kWh" when the tariff varies (time of day, power…). */
    fun formatPricePerKwhRange(min: Double, max: Double?): String =
        if (max == null || String.format(Locale.FRANCE, "%.2f", max) == String.format(Locale.FRANCE, "%.2f", min)) {
            formatPricePerKwh(min)
        } else {
            String.format(Locale.FRANCE, "%.2f – %.2f €/kWh", min, max)
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
