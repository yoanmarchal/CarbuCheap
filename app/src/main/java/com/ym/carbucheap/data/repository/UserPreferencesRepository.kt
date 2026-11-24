package com.ym.carbucheap.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.data.model.FuelType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class EnergyMode { FUEL, ELECTRIC }

data class UserPreferences(
    val fuelType: FuelType = FuelType.SP95,
    val radiusKm: Int = 10,
    val mode: EnergyMode = EnergyMode.FUEL,
    /** Null = any connector. */
    val connector: Connector? = null,
    val minPowerKw: Int = 0
)

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val FUEL_TYPE_KEY = stringPreferencesKey("fuel_type")
        private val RADIUS_KM_KEY = intPreferencesKey("radius_km")
        private val MODE_KEY = stringPreferencesKey("energy_mode")
        private val CONNECTOR_KEY = stringPreferencesKey("connector")
        private val MIN_POWER_KEY = intPreferencesKey("min_power_kw")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            fuelType = prefs[FUEL_TYPE_KEY].toEnumOr(FuelType.SP95),
            radiusKm = prefs[RADIUS_KM_KEY] ?: 10,
            mode = prefs[MODE_KEY].toEnumOr(EnergyMode.FUEL),
            connector = prefs[CONNECTOR_KEY]?.let { name ->
                runCatching { Connector.valueOf(name) }.getOrNull()
            },
            minPowerKw = prefs[MIN_POWER_KEY] ?: 0
        )
    }

    suspend fun saveFuelType(fuelType: FuelType) {
        context.dataStore.edit { it[FUEL_TYPE_KEY] = fuelType.name }
    }

    suspend fun saveRadius(radiusKm: Int) {
        context.dataStore.edit { it[RADIUS_KM_KEY] = radiusKm }
    }

    suspend fun saveMode(mode: EnergyMode) {
        context.dataStore.edit { it[MODE_KEY] = mode.name }
    }

    suspend fun saveConnector(connector: Connector?) {
        context.dataStore.edit { prefs ->
            if (connector == null) prefs.remove(CONNECTOR_KEY) else prefs[CONNECTOR_KEY] = connector.name
        }
    }

    suspend fun saveMinPower(minPowerKw: Int) {
        context.dataStore.edit { it[MIN_POWER_KEY] = minPowerKw }
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOr(default: T): T =
        this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: default
}
