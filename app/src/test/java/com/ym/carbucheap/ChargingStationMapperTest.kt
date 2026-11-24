package com.ym.carbucheap

import com.ym.carbucheap.data.model.ChargePointRecord
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.data.repository.ChargingStationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargingStationMapperTest {

    private val userLat = 48.8566
    private val userLon = 2.3522

    private fun point(
        id: String = "FRE04POAZS375",
        address: String = "4 Rue de Lobau 75004 Paris",
        lat: Double = 48.8564424,
        lon: Double = 2.3538459,
        power: Double = 100.0,
        ccs: String = "True",
        type2: String = "False",
        free: String? = "False",
        pricing: String? = null,
        hours: String? = "24/7",
        date: String? = "2026-10-30"
    ) = ChargePointRecord(
        stationId = id, stationName = "QPARK - PARIS - LOBAU", address = address,
        operator = "IZIVIA", brand = "Izivia Express", powerKw = power,
        ccs = ccs, type2 = type2, chademo = "false", domestic = "0",
        free = free, pricing = pricing, openingHours = hours, lastUpdate = date,
        latitude = lat, longitude = lon
    )

    @Test
    fun geoPointIsWrittenLatThenLon_becauseDatasetIndexIsSwapped() {
        assertEquals("geom'POINT(48.856600 2.352200)'", ChargingStationMapper.geoPoint(userLat, userLon))
    }

    @Test
    fun whereClauseAddsOptionalFilters() {
        assertEquals(
            "within_distance(coordonneesxy, geom'POINT(48.856600 2.352200)', 10km)",
            ChargingStationMapper.whereClause(userLat, userLon, 10, null, 0)
        )
        assertEquals(
            "within_distance(coordonneesxy, geom'POINT(48.856600 2.352200)', 5km)" +
                " and puissance_nominale >= 50 and prise_type_combo_ccs in ('true','True','TRUE','1')",
            ChargingStationMapper.whereClause(userLat, userLon, 5, Connector.CCS, 50)
        )
    }

    @Test
    fun groupsChargePointsIntoStations() {
        val stations = ChargingStationMapper.toStations(
            listOf(
                point(power = 100.0, ccs = "True", type2 = "False", date = "2026-10-30"),
                point(power = 22.0, ccs = "false", type2 = "TRUE", date = "2026-10-31T00:00:00+00:00", pricing = "0,38 €/kWh"),
                point(id = "OTHER", lat = 48.90, lon = 2.40, address = "1 rue X 93100 Montreuil", ccs = "0", type2 = "1")
            ),
            userLat, userLon
        )
        assertEquals(listOf("FRE04POAZS375", "OTHER"), stations.map { it.id })

        val lobau = stations[0]
        assertEquals(2, lobau.pointCount)
        assertEquals(100.0, lobau.maxPowerKw, 0.0)
        assertEquals(setOf(Connector.CCS, Connector.TYPE_2), lobau.connectors)
        assertEquals(0.38, lobau.pricePerKwh!!, 0.0)
        assertTrue(lobau.isOpen24h)
        assertFalse(lobau.isFree)
        assertEquals("2026-10-31T00:00:00+00:00", lobau.lastUpdate)
        assertTrue(lobau.distance < 0.2)

        assertEquals(setOf(Connector.TYPE_2), stations[1].connectors)
    }

    @Test
    fun dropsStationsSharingHeadOfficeCoordinatesAcrossDepartments() {
        val stations = ChargingStationMapper.toStations(
            listOf(
                point(id = "A", lat = 48.8728, lon = 2.3162, address = "360 AV DE L EUROPE 28230 EPERNON"),
                point(id = "B", lat = 48.8728, lon = 2.3162, address = "2682 RUE FAFEUR 11000 CARCASSONNE"),
                // Two operators in the same car park: same coordinates, same département → kept
                point(id = "C", address = "4 Rue de Lobau 75004 Paris"),
                point(id = "D", address = "4 Rue de Lobau, 75004 PARIS")
            ),
            userLat, userLon
        )
        assertEquals(setOf("C", "D"), stations.map { it.id }.toSet())
    }

    @Test
    fun parsesSimpleTariffsOnly() {
        assertEquals(0.30, ChargingStationMapper.parsePricePerKwh("0,30€ / kwh pour les non abonnés.")!!, 0.0)
        assertEquals(0.59, ChargingStationMapper.parsePricePerKwh("0.59 €/kWh")!!, 0.0)
        assertNull(ChargingStationMapper.parsePricePerKwh("https://belib.paris"))
        assertNull(ChargingStationMapper.parsePricePerKwh("Les tarifs de recharge peuvent varier"))
        assertNull(ChargingStationMapper.parsePricePerKwh("15,00 €/kWh"))
        assertNull(ChargingStationMapper.parsePricePerKwh(null))
    }

    @Test
    fun skipsRecordsWithoutIdOrCoordinates() {
        val stations = ChargingStationMapper.toStations(
            listOf(point(id = ""), point(id = "X").copy(latitude = null)),
            userLat, userLon
        )
        assertTrue(stations.isEmpty())
    }

    @Test
    fun departmentFromAddress() {
        assertEquals("75", ChargingStationMapper.departmentOf("4 Rue de Lobau 75004 Paris"))
        assertNull(ChargingStationMapper.departmentOf("Parking sans code postal"))
    }
}
