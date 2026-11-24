package com.ym.carbucheap

import com.ym.carbucheap.data.repository.TariffParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Samples are the most frequent real values of `tarification` in the bornes-irve dataset. */
class TariffParserTest {

    private fun kwh(text: String): Pair<Double?, Double?> =
        TariffParser.parse(text).let { it.minPerKwh to it.maxPerKwh }

    private fun assertKwh(expected: Double, text: String) {
        val (min, max) = kwh(text)
        assertEquals(text, expected, min!!, 1e-6)
        assertEquals(text, expected, max!!, 1e-6)
    }

    @Test
    fun eurosPerKwhVariants() {
        assertKwh(0.29, "0,29€ / kWh")
        assertKwh(0.55, "0,55€/kWh")
        assertKwh(0.54, "0,54 € TTC / kWh")
        assertKwh(0.30, "0,30€ / kwh pour les non abonnés.")
        assertKwh(0.42, "0.42 eur/kWh")
    }

    @Test
    fun centsPerKwhVariants() {
        assertKwh(0.59, "59 cts/kWh")
        assertKwh(0.36, "AC 36cts/KWh")
        assertKwh(0.49, "HPC 49cts/Kwh")
    }

    @Test
    fun timeOfDayTariffsWithParkingFee() {
        val t = TariffParser.parse(
            "entre 08:00 et 20:00 : 0.30916667€ par kwh de charge, 3.75€ par heure d'occupation hors charge, " +
                "entre 20:00 et 08:00 : 0.25€ par kwh de charge"
        )
        assertEquals(0.25, t.minPerKwh!!, 1e-6)
        assertEquals(0.30916667, t.maxPerKwh!!, 1e-6)
        assertEquals(3.75, t.parkingFeePerHour!!, 1e-6)
    }

    @Test
    fun misEncodedEuroSign() {
        val t = TariffParser.parse(
            "entre 07:00 et 22:00 : 0.3333Ć par kwh de charge, entre 07:00 et 22:00 : 5.0Ć par heure d'occupation hors charge"
        )
        assertEquals(0.3333, t.minPerKwh!!, 1e-6)
        assertEquals(5.0, t.parkingFeePerHour!!, 1e-6)
    }

    @Test
    fun linksAndVagueTexts() {
        val belib = TariffParser.parse("https://belib.paris")
        assertEquals("https://belib.paris", belib.url)
        assertNull(belib.minPerKwh)

        assertEquals(
            "https://apps.total-ev-charge.com/charge-points",
            TariffParser.parse("Voir https://apps.total-ev-charge.com/charge-points.").url
        )

        val vague = TariffParser.parse("Tarification au kWh plus frais de connexion éventuelles en fonction de l abonnement détenu")
        assertNull(vague.minPerKwh)
        assertNull(vague.url)

        assertNull(TariffParser.parse(null).minPerKwh)
        assertNull(TariffParser.parse("   ").minPerKwh)
    }

    @Test
    fun rejectsImplausibleValues() {
        assertNull(kwh("15,00 €/kWh").first)
        // Charging time fee is not an idle fee
        assertNull(TariffParser.parse("6.0€ par heure de charge").parkingFeePerHour)
    }
}
