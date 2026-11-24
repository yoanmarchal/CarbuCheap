package com.ym.carbucheap

import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.util.FormatUtils
import com.ym.carbucheap.util.SavingsUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class FormatAndSavingsTest {

    private val now = Instant.parse("2026-11-24T12:00:00Z")

    @Test
    fun updateAgeLabels() {
        assertEquals("MAJ à l'instant", FormatUtils.formatUpdateAge("2026-11-24T12:00:00+00:00", now))
        assertEquals("MAJ il y a 25 min", FormatUtils.formatUpdateAge("2026-11-24T11:35:00+00:00", now))
        assertEquals("MAJ il y a 3 h", FormatUtils.formatUpdateAge("2026-11-24T11:00:00+02:00", now))
        assertEquals("MAJ il y a 12 j", FormatUtils.formatUpdateAge("2026-11-12T06:55:20+00:00", now))
        assertNull(FormatUtils.formatUpdateAge("", now))
        assertNull(FormatUtils.formatUpdateAge("pas une date", now))
    }

    @Test
    fun staleAfter48Hours() {
        assertFalse(FormatUtils.isStale("2026-11-23T12:00:00+00:00", now))
        assertTrue(FormatUtils.isStale("2026-11-22T11:59:00+00:00", now))
        assertFalse(FormatUtils.isStale("", now))
    }

    private fun station(price: Double, distance: Double) = Station(
        id = "$price", name = "", address = "", city = "", postalCode = "",
        latitude = 0.0, longitude = 0.0, price = price, fuelType = "Gazole",
        distance = distance, lastUpdate = ""
    )

    @Test
    fun savingsAgainstMedianPrice() {
        // One overpriced station nearby must not inflate the reference
        val stations = listOf(station(1.80, 5.0), station(2.53, 0.5), station(1.85, 2.0))
        val reference = SavingsUtils.medianPrice(stations)
        assertEquals(1.85, reference!!, 0.0)
        assertEquals(2.5, SavingsUtils.fullTankSavings(1.80, reference)!!, 0.0001)
        assertNull(SavingsUtils.fullTankSavings(1.85, reference))
        assertNull(SavingsUtils.fullTankSavings(2.53, reference))
        assertNull(SavingsUtils.fullTankSavings(1.80, null))
        assertNull(SavingsUtils.medianPrice(emptyList()))
        assertEquals(1.825, SavingsUtils.medianPrice(stations.take(1) + stations.drop(2))!!, 0.0001)
    }
}
