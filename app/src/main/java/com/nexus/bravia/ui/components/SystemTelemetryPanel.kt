package com.nexus.bravia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.model.SystemStats
import com.nexus.bravia.ui.theme.LocalNexusPalette
import java.util.Locale

@Composable
fun SystemTelemetryPanel(
    stats: SystemStats,
    modifier: Modifier = Modifier
) {
    val palette = LocalNexusPalette.current

    GlassPanel(
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Memory,
                        contentDescription = "System Telemetry",
                        tint = palette.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYSTEM TELEMETRY",
                        color = palette.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (stats.isConnected) palette.success else palette.warning)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stats.networkStatus,
                        color = palette.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${stats.deviceModel} • ${stats.androidVersion}",
                color = palette.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            TelemetryBar(
                label = "MEMORY",
                valueText = "${stats.usedRamMb} / ${stats.totalRamMb} MB",
                fraction = stats.ramUsageFraction
            )

            Spacer(modifier = Modifier.height(10.dp))

            TelemetryBar(
                label = "STORAGE FREE",
                valueText = String.format(
                    Locale.US,
                    "%.1f / %.1f GB",
                    stats.freeStorageGb,
                    stats.totalStorageGb
                ),
                fraction = stats.storageUsedFraction
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "UPTIME: ${stats.uptimeFormatted}",
                color = palette.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun TelemetryBar(
    label: String,
    valueText: String,
    fraction: Float
) {
    val palette = LocalNexusPalette.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = palette.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valueText,
                color = palette.textPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(palette.borderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0.03f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.accentPrimary)
            )
        }
    }
}
