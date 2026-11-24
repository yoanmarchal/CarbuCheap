package com.ym.carbucheap

import com.ym.carbucheap.data.model.ChargingStation
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.GeoPoint
import com.ym.carbucheap.data.model.StationRecord
import com.ym.carbucheap.data.repository.StationMapper
import com.ym.carbucheap.data.repository.StationNameRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StationNamesTest {

    @Test
    fun parsesTsvToleratingCrlfAndBadLines() {
        val names = StationNameRepository.parse(
            sequenceOf("1000001\tCarrefour Market\r", "1000009\tIntermarché", "garbage", "\tNoId", "42\t  ", "")
        )
        assertEquals(mapOf("1000001" to "Carrefour Market", "1000009" to "Intermarché"), names)
    }

    private val record = StationRecord(
        id = 1000001, adresse = "596 avenue de trevoux", ville = "Saint-Denis-lès-Bourg", cp = "01000",
        geom = GeoPoint(lon = 5.198, lat = 46.201),
        gazolePrix = 1.9, gazoleMaj = null, sp95Prix = null, sp95Maj = null, sp98Prix = null, sp98Maj = null,
        e85Prix = null, e85Maj = null, e10Prix = null, e10Maj = null, gplcPrix = null, gplcMaj = null,
        carburantsDisponibles = null, services = null, automate24 = null
    )

    @Test
    fun brandBecomesTitleAndAddressMovesToSubtitle() {
        val station = StationMapper.toStation(record, FuelType.GAZOLE, 46.2, 5.2, brand = " Carrefour Market ")!!
        assertEquals("Carrefour Market", station.title)
        assertEquals("596 avenue de trevoux, 01000 Saint-Denis-lès-Bourg", station.subtitle)
    }

    @Test
    fun withoutBrandAddressStaysTitle() {
        val station = StationMapper.toStation(record, FuelType.GAZOLE, 46.2, 5.2, brand = "  ")!!
        assertNull(station.brand)
        assertEquals("596 avenue de trevoux", station.title)
        assertEquals("01000 Saint-Denis-lès-Bourg", station.subtitle)
    }

    private fun charging(name: String, brand: String, operator: String) = ChargingStation(
        id = "x", name = name, operator = operator, brand = brand, address = "", latitude = 0.0,
        longitude = 0.0, distance = 0.0, maxPowerKw = 22.0, pointCount = 1, connectors = emptySet(),
        isFree = false, minPricePerKwh = null, maxPricePerKwh = null, parkingFeePerHour = null,
        pricingUrl = null, isOpen24h = false, lastUpdate = ""
    )

    @Test
    fun networkLabelPrefersBrandAndAvoidsRepeatingTheName() {
        assertEquals("Izivia Express", charging("QPARK - PARIS - LOBAU", "Izivia Express", "IZIVIA").networkLabel)
        assertEquals("IZIVIA", charging("QPARK - PARIS - LOBAU", "", "IZIVIA").networkLabel)
        assertNull(charging("Freshmile France/FCJCBB", "Freshmile", "Freshmile").networkLabel)
        assertNull(charging("Station", "", "").networkLabel)
    }
}
