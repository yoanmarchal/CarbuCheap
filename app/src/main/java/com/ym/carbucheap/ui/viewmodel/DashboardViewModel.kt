package com.ym.carbucheap.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ym.carbucheap.data.location.LocationProvider
import com.ym.carbucheap.data.model.ChargingStation
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.data.remote.RetrofitInstance
import com.ym.carbucheap.data.repository.ChargingRepository
import com.ym.carbucheap.data.repository.EnergyMode
import com.ym.carbucheap.data.repository.FuelRepository
import com.ym.carbucheap.data.repository.StationNameRepository
import com.ym.carbucheap.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
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
    data class ChargingSuccess(
        val stations: List<ChargingStation>
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val stationNameRepository = StationNameRepository(application)
    private val repository = FuelRepository(RetrofitInstance.api) { stationNameRepository.getNames() }
    private val chargingRepository = ChargingRepository(RetrofitInstance.irveApi)
    private val locationProvider = LocationProvider(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _mode = MutableStateFlow(EnergyMode.FUEL)
    val mode: StateFlow<EnergyMode> = _mode.asStateFlow()

    private val _selectedFuelType = MutableStateFlow(FuelType.SP95)
    val selectedFuelType: StateFlow<FuelType> = _selectedFuelType.asStateFlow()

    private val _selectedRadius = MutableStateFlow(10)
    val selectedRadius: StateFlow<Int> = _selectedRadius.asStateFlow()

    private val _selectedConnector = MutableStateFlow<Connector?>(null)
    val selectedConnector: StateFlow<Connector?> = _selectedConnector.asStateFlow()

    private val _minPowerKw = MutableStateFlow(0)
    val minPowerKw: StateFlow<Int> = _minPowerKw.asStateFlow()

    private var lastLat: Double? = null
    private var lastLon: Double? = null

    init {
        viewModelScope.launch {
            val savedPrefs = preferencesRepository.userPreferencesFlow.first()
            _selectedFuelType.value = savedPrefs.fuelType
            _selectedRadius.value = savedPrefs.radiusKm
            _mode.value = savedPrefs.mode
            _selectedConnector.value = savedPrefs.connector
            _minPowerKw.value = savedPrefs.minPowerKw
            Log.d("DashboardVM", "Préférences restaurées : $savedPrefs")
            loadStations()
        }
    }

    /**
     * Single in-flight load. Every new request cancels the previous one, so a slow
     * response for an old selection can never overwrite the current state.
     */
    private var loadJob: Job? = null

    private fun launchLoad(block: suspend () -> Unit) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch { block() }
    }

    fun loadStations(forceRefresh: Boolean = false) {
        launchLoad {
            _uiState.value = DashboardUiState.Loading
            locateAndFetch(forceRefresh)
        }
    }

    private suspend fun locateAndFetch(forceRefresh: Boolean) {
        locationProvider.getCurrentLocation().fold(
            onSuccess = { (lat, lon) ->
                Log.d("DashboardVM", "GPS position: lat=$lat, lon=$lon")
                lastLat = lat
                lastLon = lon
                fetch(lat, lon, forceRefresh)
            },
            onFailure = {
                _uiState.value = DashboardUiState.Error(
                    "Impossible d'obtenir votre position GPS.\nVérifiez que la localisation est activée."
                )
            }
        )
    }

    fun selectMode(mode: EnergyMode) {
        if (_mode.value == mode) return
        _mode.value = mode
        viewModelScope.launch { preferencesRepository.saveMode(mode) }
        reloadForSelection()
    }

    fun selectFuelType(fuelType: FuelType) {
        _selectedFuelType.value = fuelType
        viewModelScope.launch { preferencesRepository.saveFuelType(fuelType) }
        reloadForSelection()
    }

    fun selectRadius(radiusKm: Int) {
        _selectedRadius.value = radiusKm
        viewModelScope.launch { preferencesRepository.saveRadius(radiusKm) }
        reloadForSelection()
    }

    /** Selecting the active connector again clears the filter. */
    fun toggleConnector(connector: Connector) {
        val newValue = if (_selectedConnector.value == connector) null else connector
        _selectedConnector.value = newValue
        viewModelScope.launch { preferencesRepository.saveConnector(newValue) }
        reloadForSelection()
    }

    fun selectMinPower(minPowerKw: Int) {
        _minPowerKw.value = minPowerKw
        viewModelScope.launch { preferencesRepository.saveMinPower(minPowerKw) }
        reloadForSelection()
    }

    private fun reloadForSelection() {
        val lat = lastLat
        val lon = lastLon
        if (lat == null || lon == null) {
            loadStations()
            return
        }
        launchLoad {
            _isRefreshing.value = false
            _uiState.value = DashboardUiState.Loading
            fetch(lat, lon)
        }
    }

    fun refresh() {
        val lat = lastLat
        val lon = lastLon
        if (lat == null || lon == null) {
            loadStations(forceRefresh = true)
            return
        }
        launchLoad {
            _isRefreshing.value = true
            try {
                fetch(lat, lon, forceRefresh = true)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun fetch(lat: Double, lon: Double, forceRefresh: Boolean = false) {
        when (_mode.value) {
            EnergyMode.FUEL -> fetchStations(lat, lon, _selectedFuelType.value, _selectedRadius.value, forceRefresh)
            EnergyMode.ELECTRIC -> fetchChargingStations(lat, lon, forceRefresh)
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

    private suspend fun fetchChargingStations(lat: Double, lon: Double, forceRefresh: Boolean) {
        chargingRepository.getStations(
            userLat = lat,
            userLon = lon,
            radiusKm = _selectedRadius.value,
            connector = _selectedConnector.value,
            minPowerKw = _minPowerKw.value,
            forceRefresh = forceRefresh
        ).fold(
            onSuccess = { _uiState.value = DashboardUiState.ChargingSuccess(it) },
            onFailure = { error ->
                Log.e("DashboardVM", "IRVE request failed", error)
                _uiState.value = DashboardUiState.Error(
                    "Impossible de récupérer les bornes de recharge.\nVérifiez votre connexion."
                )
            }
        )
    }
}
