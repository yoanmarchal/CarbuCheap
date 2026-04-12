package com.ym.carbucheap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ym.carbucheap.data.model.Station
import com.ym.carbucheap.util.FormatUtils

@Composable
fun StationCard(
    station: Station,
    position: Int,
    onNavigateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (position == 0) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        val isHighlighted = position == 0
        val subtextColor = if (isHighlighted) {
            Color.White.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fuel icon
            Icon(
                imageVector = Icons.Filled.LocalGasStation,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Station info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = station.address,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${station.postalCode} ${station.city}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = FormatUtils.formatPrice(station.price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = FormatUtils.formatDistance(station.distance),
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtextColor,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Navigation button
            FilledTonalIconButton(
                onClick = onNavigateClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "Naviguer"
                )
            }
        }
    }
}

