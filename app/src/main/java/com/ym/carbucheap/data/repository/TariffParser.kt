package com.ym.carbucheap.data.repository

/**
 * Extracts structured prices from the free-text `tarification` field of the IRVE dataset.
 * Operators write it however they like, e.g.:
 *  - "0,29€ / kWh", "0,54 € TTC / kWh", "59 cts/kWh", "HPC 49cts/Kwh"
 *  - "entre 08:00 et 20:00 : 0.309€ par kwh de charge, 3.75€ par heure d'occupation hors charge"
 *  - "https://belib.paris"
 * Some sources are mis-encoded and show "Ć" instead of "€".
 */
object TariffParser {

    data class Tariff(
        val minPerKwh: Double?,
        val maxPerKwh: Double?,
        val parkingFeePerHour: Double?,
        val url: String?
    )

    private const val NUMBER = """(\d+(?:[.,]\d+)?)"""

    /** "0,29 € / kWh", "0.309€ par kwh", "0,54 € TTC / kWh", "0,42 eur/kWh" */
    private val EUROS_PER_KWH = Regex(
        """$NUMBER\s*(?:€|eur(?:os?)?)?\s*(?:ttc|ht)?\s*(?:/|par)\s*kwh""",
        RegexOption.IGNORE_CASE
    )

    /** "59 cts/kWh", "36cts/KWh", "49 c€/kWh", "30 centimes / kWh" */
    private val CENTS_PER_KWH = Regex(
        """$NUMBER\s*(?:cts?|centimes?|c€)\s*(?:ttc|ht)?\s*(?:/|par)\s*kwh""",
        RegexOption.IGNORE_CASE
    )

    /** "3.75€ par heure d'occupation", "6 €/h d'occupation" */
    private val PARKING_FEE = Regex(
        """$NUMBER\s*(?:€|eur(?:os?)?)?\s*(?:/|par)\s*(?:h|heure)\s+d['’]\s*occupation""",
        RegexOption.IGNORE_CASE
    )

    private val URL = Regex("""https?://[^\s,;"'<>]+""", RegexOption.IGNORE_CASE)

    private val PLAUSIBLE_KWH = 0.05..2.0
    private val PLAUSIBLE_PARKING = 0.1..50.0

    fun parse(text: String?): Tariff {
        if (text.isNullOrBlank()) return Tariff(null, null, null, null)
        val normalized = text.replace('Ć', '€')

        val cents = CENTS_PER_KWH.findAll(normalized).toList()
        val centsRanges = cents.map { it.range }
        val euros = EUROS_PER_KWH.findAll(normalized)
            // "59 cts/kWh" must not also be read as 59 €/kWh
            .filter { match -> centsRanges.none { it.overlaps(match.range) } }

        val prices = (euros.mapNotNull { it.toDouble() } + cents.mapNotNull { m -> m.toDouble()?.div(100) })
            .filter { it in PLAUSIBLE_KWH }
            .toList()

        val parkingFee = PARKING_FEE.findAll(normalized)
            .mapNotNull { it.toDouble() }
            .filter { it in PLAUSIBLE_PARKING }
            .minOrNull()

        return Tariff(
            minPerKwh = prices.minOrNull(),
            maxPerKwh = prices.maxOrNull(),
            parkingFeePerHour = parkingFee,
            url = URL.find(normalized)?.value?.trimEnd('.', ')')
        )
    }

    private fun MatchResult.toDouble(): Double? =
        groupValues[1].replace(',', '.').toDoubleOrNull()

    private fun IntRange.overlaps(other: IntRange): Boolean =
        first <= other.last && other.first <= last
}
