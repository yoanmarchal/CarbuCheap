package com.ym.carbucheap.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ym.carbucheap.data.location.LocationProvider
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.data.remote.RetrofitInstance
import com.ym.carbucheap.data.repository.FuelRepository
import com.ym.carbucheap.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    data object Loading : DashboardUiState()
    data class Success(
        val stations: List<Station>,
        val selectedFuel: FuelType
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FuelRepository(RetrofitInstance.api)
    private val locationProvider = LocationProvider(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedFuelType = MutableStateFlow(FuelType.SP95)
    val selectedFuelType: StateFlow<FuelType> = _selectedFuelType.asStateFlow()

    private val _selectedRadius = MutableStateFlow(10)
    val selectedRadius: StateFlow<Int> = _selectedRadius.asStateFlow()

    private var lastLat: Double? = null
    private var lastLon: Double? = null

    init {
        viewModelScope.launch {
            val savedPrefs = preferencesRepository.userPreferencesFlow.first()
            _selectedFuelType.value = savedPrefs.fuelType
            _selectedRadius.value = savedPrefs.radiusKm
            Log.d("DashboardVM", "Préférences restaurées : carburant=${savedPrefs.fuelType}, rayon=${savedPrefs.radiusKm} km")
            loadStations()
        }
    }

    fun loadStations(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            val locationResult = locationProvider.getCurrentLocation()
            locationResult.fold(
                onSuccess = { (lat, lon) ->
                    Log.d("DashboardVM", "GPS position: lat=$lat, lon=$lon")
                    lastLat = lat
                    lastLon = lon
                    fetchStations(lat, lon, _selectedFuelType.value, _selectedRadius.value, forceRefresh)
                },
                onFailure = {
                    _uiState.value = DashboardUiState.Error(
                        "Impossible d'obtenir votre position GPS.\nVérifiez que la localisation est activée."
                    )
                }
            )
        }
    }

    fun selectFuelType(fuelType: FuelType) {
        _selectedFuelType.value = fuelType
        viewModelScope.launch {
            preferencesRepository.saveFuelType(fuelType)
        }
        val lat = lastLat
        val lon = lastLon
        if (lat != null && lon != null) {
            viewModelScope.launch {
                _uiState.value = DashboardUiState.Loading
                fetchStations(lat, lon, fuelType, _selectedRadius.value)
            }
        } else {
            loadStations()
        }
    }

    fun selectRadius(radiusKm: Int) {
        _selectedRadius.value = radiusKm
        viewModelScope.launch {
            preferencesRepository.saveRadius(radiusKm)
        }
        val lat = lastLat
        val lon = lastLon
        if (lat != null && lon != null) {
            viewModelScope.launch {
                _uiState.value = DashboardUiState.Loading
                fetchStations(lat, lon, _selectedFuelType.value, radiusKm)
            }
        } else {
            loadStations()
        }
    }

    fun refresh() {
        val lat = lastLat
        val lon = lastLon
        if (lat != null && lon != null) {
            viewModelScope.launch {
                _isRefreshing.value = true
                fetchStations(lat, lon, _selectedFuelType.value, _selectedRadius.value, forceRefresh = true)
                _isRefreshing.value = false
            }
        } else {
            loadStations(forceRefresh = true)
        }
    }

    private suspend fun fetchStations(
        lat: Double,
        lon: Double,
        fuelType: FuelType,
        radiusKm: Int = 10,
        forceRefresh: Boolean = false
    ) {
        val result = repository.getStations(lat, lon, fuelType, radiusKm, forceRefresh)
        result.fold(
            onSuccess = { stations ->
                _uiState.value = DashboardUiState.Success(
                    stations = stations,
                    selectedFuel = fuelType
                )
            },
            onFailure = { error ->
                _uiState.value = DashboardUiState.Error(
                    error.message ?: "Erreur lors de la récupération des stations."
                )
            }
        )
    }
}
