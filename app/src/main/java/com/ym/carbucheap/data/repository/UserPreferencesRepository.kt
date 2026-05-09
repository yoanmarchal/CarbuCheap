package com.ym.carbucheap.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ym.carbucheap.data.model.FuelType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val fuelType: FuelType = FuelType.SP95,
    val radiusKm: Int = 10
)

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val FUEL_TYPE_KEY = stringPreferencesKey("fuel_type")
        private val RADIUS_KM_KEY = intPreferencesKey("radius_km")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val fuelTypeName = prefs[FUEL_TYPE_KEY]
        val fuelType = if (fuelTypeName != null) {
            runCatching { FuelType.valueOf(fuelTypeName) }.getOrDefault(FuelType.SP95)
        } else {
            FuelType.SP95
        }
        val radiusKm = prefs[RADIUS_KM_KEY] ?: 10
        UserPreferences(fuelType = fuelType, radiusKm = radiusKm)
    }

    suspend fun saveFuelType(fuelType: FuelType) {
        context.dataStore.edit { prefs ->
            prefs[FUEL_TYPE_KEY] = fuelType.name
        }
    }

    suspend fun saveRadius(radiusKm: Int) {
        context.dataStore.edit { prefs ->
            prefs[RADIUS_KM_KEY] = radiusKm
        }
    }
}

