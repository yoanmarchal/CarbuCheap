package com.ym.carbucheap.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ym.carbucheap.data.model.Connector
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.repository.EnergyMode
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.ui.theme.CarbuCheapTheme
import com.ym.carbucheap.ui.viewmodel.DashboardUiState
import com.ym.carbucheap.ui.viewmodel.DashboardViewModel
import com.ym.carbucheap.util.IntentUtils
import com.ym.carbucheap.util.SavingsUtils
import com.ym.carbucheap.R

// Listes stables extraites au top-level pour éviter les ré-allocations à chaque recomposition
private val FUEL_TYPE_LIST = FuelType.entries.toList()
private val RADIUS_OPTIONS = listOf(5, 10, 20, 30, 50)
private val CONNECTOR_LIST = Connector.entries.filter { it != Connector.DOMESTIC }
private val MIN_POWER_OPTIONS = listOf(0, 22, 50, 150)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFuel by viewModel.selectedFuelType.collectAsStateWithLifecycle()
    val selectedRadius by viewModel.selectedRadius.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val selectedConnector by viewModel.selectedConnector.collectAsStateWithLifecycle()
    val minPowerKw by viewModel.minPowerKw.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        mode = mode,
        selectedConnector = selectedConnector,
        minPowerKw = minPowerKw,
        onModeSelected = { viewModel.selectMode(it) },
        onConnectorToggled = { viewModel.toggleConnector(it) },
        onMinPowerSelected = { viewModel.selectMinPower(it) },
        selectedFuel = selectedFuel,
        selectedRadius = selectedRadius,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onRetry = { viewModel.loadStations() },
        onFuelSelected = { viewModel.selectFuelType(it) },
        onRadiusSelected = { viewModel.selectRadius(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    mode: EnergyMode,
    selectedConnector: Connector?,
    minPowerKw: Int,
    onModeSelected: (EnergyMode) -> Unit,
    onConnectorToggled: (Connector) -> Unit,
    onMinPowerSelected: (Int) -> Unit,
    selectedFuel: FuelType,
    selectedRadius: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onFuelSelected: (FuelType) -> Unit,
    onRadiusSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                ),
                title = {
                    Row(verticalAlignment = CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "CarbuCheap",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualiser"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues)
        ) {
            ModeSelector(mode = mode, onModeSelected = onModeSelected)

            when (mode) {
                EnergyMode.FUEL -> FuelTypeSelector(
                    selectedFuel = selectedFuel,
                    onFuelSelected = onFuelSelected
                )
                EnergyMode.ELECTRIC -> ChargingFilters(
                    selectedConnector = selectedConnector,
                    minPowerKw = minPowerKw,
                    onConnectorToggled = onConnectorToggled,
                    onMinPowerSelected = onMinPowerSelected
                )
            }

            // Radius selector
            RadiusSelector(
                selectedRadius = selectedRadius,
                onRadiusSelected = onRadiusSelected
            )

            // Content — AnimatedContent pour transitions fluides entre états (MD3 motion)
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    (fadeIn(animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f)) +
                        slideInVertically(
                            animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f)
                        ) { it / 10 })
                        .togetherWith(
                            fadeOut(animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f))
                        )
                },
                label = "dashboard_state_transition",
                modifier = Modifier.fillMaxSize()
            ) { state ->
            when (state) {
                is DashboardUiState.Loading -> {
                    LoadingContent()
                }

                is DashboardUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = onRetry
                    )
                }

                is DashboardUiState.ChargingSuccess -> {
                    if (state.stations.isEmpty()) {
                        EmptyContent(
                            emoji = "🔌",
                            message = "Aucune borne trouvée avec ces critères.\nEssayez d'élargir le rayon ou de retirer un filtre."
                        )
                    } else {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = onRefresh,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.stations,
                                    key = { it.id }
                                ) { station ->
                                    ChargingStationCard(
                                        station = station,
                                        onNavigateClick = {
                                            IntentUtils.openNavigation(
                                                context = context,
                                                latitude = station.latitude,
                                                longitude = station.longitude,
                                                label = station.name
                                            )
                                        },
                                        onPricingClick = { url -> IntentUtils.openUrl(context, url) }
                                    )
                                }
                            }
                        }
                    }
                }

                is DashboardUiState.Success -> {
                    if (state.stations.isEmpty()) {
                        EmptyContent()
                    } else {
                        val referencePrice = remember(state.stations) {
                            SavingsUtils.medianPrice(state.stations)
                        }
                        val openNavigation: (Station) -> Unit = { station ->
                            IntentUtils.openNavigation(
                                context = context,
                                latitude = station.latitude,
                                longitude = station.longitude,
                                label = "${station.address}, ${station.city}"
                            )
                        }
                        var detailStationId by rememberSaveable { mutableStateOf<String?>(null) }
                        val detailStation = state.stations.firstOrNull { it.id == detailStationId }

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = onRefresh,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                itemsIndexed(
                                    items = state.stations,
                                    key = { _, station -> station.id }
                                ) { index, station ->
                                    StationCard(
                                        station = station,
                                        position = index,
                                        savings = SavingsUtils.fullTankSavings(station.price, referencePrice),
                                        onClick = { detailStationId = station.id },
                                        onNavigateClick = { openNavigation(station) }
                                    )
                                }
                            }
                        }

                        detailStation?.let { station ->
                            StationDetailSheet(
                                station = station,
                                selectedFuel = state.selectedFuel,
                                savings = SavingsUtils.fullTankSavings(station.price, referencePrice),
                                onNavigateClick = { openNavigation(station) },
                                onDismiss = { detailStationId = null }
                            )
                        }
                    }
                }
            }
            } // end AnimatedContent
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeSelector(
    mode: EnergyMode,
    onModeSelected: (EnergyMode) -> Unit
) {
    val options = listOf(
        Triple(EnergyMode.FUEL, "Carburant", Icons.Filled.LocalGasStation),
        Triple(EnergyMode.ELECTRIC, "Électrique", Icons.Filled.EvStation)
    )
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        options.forEachIndexed { index, (value, label, icon) ->
            SegmentedButton(
                selected = mode == value,
                onClick = { onModeSelected(value) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    SegmentedButtonDefaults.Icon(active = mode == value) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun ChargingFilters(
    selectedConnector: Connector?,
    minPowerKw: Int,
    onConnectorToggled: (Connector) -> Unit,
    onMinPowerSelected: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CONNECTOR_LIST) { connector ->
            val selected = connector == selectedConnector
            FilterChip(
                selected = selected,
                onClick = { onConnectorToggled(connector) },
                label = {
                    Text(
                        text = connector.displayName,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.semantics {
                    contentDescription = if (selected) {
                        "Prise ${connector.displayName} sélectionnée, toucher pour retirer le filtre"
                    } else {
                        "Filtrer sur la prise ${connector.displayName}"
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        items(MIN_POWER_OPTIONS) { power ->
            val selected = power == minPowerKw
            FilterChip(
                selected = selected,
                onClick = { onMinPowerSelected(power) },
                label = {
                    Text(
                        text = if (power == 0) "Toutes puissances" else "≥ $power kW",
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                )
            )
        }
    }
}

@Composable
private fun FuelTypeSelector(
    selectedFuel: FuelType,
    onFuelSelected: (FuelType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(FUEL_TYPE_LIST) { fuelType ->
            FilterChip(
                selected = fuelType == selectedFuel,
                onClick = { onFuelSelected(fuelType) },
                label = {
                    Text(
                        text = fuelType.displayName,
                        fontWeight = if (fuelType == selectedFuel) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.semantics {
                    contentDescription = if (fuelType == selectedFuel) {
                        "Carburant ${fuelType.displayName} sélectionné"
                    } else {
                        "Sélectionner le carburant ${fuelType.displayName}"
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun RadiusSelector(
    selectedRadius: Int,
    onRadiusSelected: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(RADIUS_OPTIONS) { radius ->
            FilterChip(
                selected = radius == selectedRadius,
                onClick = { onRadiusSelected(radius) },
                label = {
                    Text(
                        text = "${radius} km",
                        fontWeight = if (radius == selectedRadius) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                },
                leadingIcon = null,
                trailingIcon = null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Chargement en cours" },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recherche des stations…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = "Erreur",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}

@Composable
private fun EmptyContent(
    emoji: String = "⛽",
    message: String = "Aucune station trouvée à proximité."
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    CarbuCheapTheme {
        DashboardContent(
            uiState = DashboardUiState.Success(
                stations = listOf(
                    Station(
                        id = "1",
                        name = "Station Total",
                        address = "123 Rue de la Pompe",
                        city = "Paris",
                        postalCode = "75001",
                        latitude = 48.8566,
                        longitude = 2.3522,
                        price = 1.859,
                        fuelType = "Gazole",
                        distance = 1.2,
                        lastUpdate = "2023-10-27T10:00:00Z"
                    ),
                    Station(
                        id = "2",
                        name = "Station Leclerc",
                        address = "456 Avenue du Garage",
                        city = "Paris",
                        postalCode = "75002",
                        latitude = 48.8666,
                        longitude = 2.3622,
                        price = 1.899,
                        fuelType = "Gazole",
                        distance = 2.5,
                        lastUpdate = "2023-10-27T11:00:00Z"
                    ),
                    Station(
                        id = "3",
                        name = "Station Esso",
                        address = "789 Boulevard du Carburant",
                        city = "Paris",
                        postalCode = "75003",
                        latitude = 48.8766,
                        longitude = 2.3722,
                        price = 1.929,
                        fuelType = "Gazole",
                        distance = 3.8,
                        lastUpdate = "2023-10-27T12:00:00Z"
                    )
                ),
                selectedFuel = FuelType.GAZOLE
            ),
            mode = EnergyMode.FUEL,
            selectedConnector = null,
            minPowerKw = 0,
            onModeSelected = {},
            onConnectorToggled = {},
            onMinPowerSelected = {},
            selectedFuel = FuelType.GAZOLE,
            selectedRadius = 10,
            isRefreshing = false,
            onRefresh = {},
            onRetry = {},
            onFuelSelected = {},
            onRadiusSelected = {}
        )
    }
}
