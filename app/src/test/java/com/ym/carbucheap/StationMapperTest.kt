package com.ym.carbucheap

import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.GeoPoint
import com.ym.carbucheap.data.model.StationRecord
import com.ym.carbucheap.data.repository.StationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StationMapperTest {

    private fun record(
        gazole: Double? = 2.109,
        sp95: Double? = null,
        e10: Double? = 1.931,
        geom: GeoPoint? = GeoPoint(lon = -2.69, lat = 48.061),
        rupture: String? = null,
        automate: String? = "Oui"
    ) = StationRecord(
        id = 56580001,
        adresse = "16 rue chateaubriand",
        ville = "bréhan",
        cp = "56580",
        geom = geom,
        gazolePrix = gazole, gazoleMaj = "2026-11-12T06:55:20+00:00",
        sp95Prix = sp95, sp95Maj = null,
        sp98Prix = null, sp98Maj = null,
        e85Prix = null, e85Maj = null,
        e10Prix = e10, e10Maj = "2026-11-10T06:00:00+00:00",
        gplcPrix = null, gplcMaj = null,
        carburantsDisponibles = listOf("Gazole", "E10"),
        carburantsRuptureTemporaire = rupture,
        services = listOf("Lavage automatique", " DAB (Distributeur automatique de billets) "),
        automate24 = automate
    )

    @Test
    fun mapsSelectedFuelAndAllPrices() {
        val station = StationMapper.toStation(record(), FuelType.GAZOLE, 48.061, -2.69)!!
        assertEquals(2.109, station.price, 0.0)
        assertEquals("16 rue chateaubriand", station.address.lowercase())
        assertEquals("Bréhan", station.city)
        assertEquals(listOf(FuelType.GAZOLE, FuelType.E10), station.allPrices.map { it.fuelType })
        assertEquals("2026-11-10T06:00:00+00:00", station.allPrices[1].lastUpdate)
        assertEquals(0.0, station.distance, 0.001)
        assertTrue(station.isAutomate24h)
        assertEquals("DAB (Distributeur automatique de billets)", station.services[1])
    }

    @Test
    fun returnsNullWithoutPriceOrCoordinates() {
        assertNull(StationMapper.toStation(record(), FuelType.SP95, 0.0, 0.0))
        assertNull(StationMapper.toStation(record(gazole = 0.0), FuelType.GAZOLE, 0.0, 0.0))
        assertNull(StationMapper.toStation(record(geom = null), FuelType.GAZOLE, 0.0, 0.0))
    }

    @Test
    fun parsesTemporaryShortages() {
        val station = StationMapper.toStation(record(rupture = "SP95; E85"), FuelType.GAZOLE, 0.0, 0.0)!!
        assertEquals(listOf("SP95", "E85"), station.temporaryShortages)

        val none = StationMapper.toStation(record(rupture = null, automate = "Non"), FuelType.GAZOLE, 0.0, 0.0)!!
        assertTrue(none.temporaryShortages.isEmpty())
        assertFalse(none.isAutomate24h)
    }
}
