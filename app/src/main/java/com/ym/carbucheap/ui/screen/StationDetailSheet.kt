package com.ym.carbucheap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ym.carbucheap.data.model.FuelType
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.util.FormatUtils
import com.ym.carbucheap.util.SavingsUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailSheet(
    station: Station,
    selectedFuel: FuelType,
    savings: Double?,
    onNavigateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        StationDetailContent(
            station = station,
            selectedFuel = selectedFuel,
            savings = savings,
            onNavigateClick = onNavigateClick
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StationDetailContent(
    station: Station,
    selectedFuel: FuelType,
    savings: Double?,
    onNavigateClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = station.address,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            text = "${station.postalCode} ${station.city} · ${FormatUtils.formatDistance(station.distance)}",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )

        if (station.isAutomate24h) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = MaterialTheme.shapes.small,
                color = colors.secondaryContainer,
                contentColor = colors.onSecondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Automate 24/24", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        savings?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Savings, contentDescription = null, tint = colors.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Économie de ${FormatUtils.formatEuros(it)} sur un plein de " +
                        "${SavingsUtils.FULL_TANK_LITERS.toInt()} L par rapport au prix médian des stations affichées",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Prix")

        station.allPrices.forEach { fuelPrice ->
            val isSelected = fuelPrice.fuelType == selectedFuel
            val stale = FormatUtils.isStale(fuelPrice.lastUpdate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fuelPrice.fuelType.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    FormatUtils.formatUpdateAge(fuelPrice.lastUpdate)?.let { age ->
                        Text(
                            text = if (stale) "Prix à vérifier · $age" else age,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (stale) colors.error else colors.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = FormatUtils.formatPrice(fuelPrice.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) colors.tertiary else colors.onSurface
                )
            }
        }

        station.temporaryShortages.forEach { fuel ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$fuel : rupture temporaire",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.error
                )
            }
        }

        if (station.services.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Services")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                station.services.forEach { service ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = colors.surfaceContainerHigh,
                        contentColor = colors.onSurfaceVariant
                    ) {
                        Text(
                            text = service,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Y aller")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .semantics { heading() }
    )
}
