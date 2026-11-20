package com.nexus.bravia.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun QuickSettingsBar(
    onOpenAllApps: () -> Unit,
    onOpenNexusSettings: () -> Unit,
    onLaunchSystemIntent: (Intent, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickActionChip(
            icon = Icons.Rounded.Apps,
            label = "All Apps",
            onClick = onOpenAllApps
        )
        QuickActionChip(
            icon = Icons.Rounded.Wifi,
            label = "Network",
            onClick = {
                onLaunchSystemIntent(
                    Intent(Settings.ACTION_WIFI_SETTINGS),
                    "Network settings unavailable on this TV"
                )
            }
        )
        QuickActionChip(
            icon = Icons.Rounded.Bluetooth,
            label = "Remotes",
            onClick = {
                onLaunchSystemIntent(
                    Intent(Settings.ACTION_BLUETOOTH_SETTINGS),
                    "Bluetooth settings unavailable on this TV"
                )
            }
        )
        QuickActionChip(
            icon = Icons.Rounded.DisplaySettings,
            label = "Display",
            onClick = {
                onLaunchSystemIntent(
                    Intent(Settings.ACTION_DISPLAY_SETTINGS),
                    "Display settings unavailable on this TV"
                )
            }
        )
        QuickActionChip(
            icon = Icons.Rounded.Settings,
            label = "TV Settings",
            onClick = {
                onLaunchSystemIntent(
                    Intent(Settings.ACTION_SETTINGS),
                    "System settings unavailable"
                )
            }
        )
        QuickActionChip(
            icon = Icons.Rounded.Tune,
            label = "NEXUS",
            onClick = onOpenNexusSettings
        )
    }
}

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val palette = LocalNexusPalette.current

    GlassPanel(
        focusableItem = true,
        cornerRadius = 14.dp,
        onClick = onClick
    ) { isFocused ->
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isFocused) palette.accentPrimary else palette.textPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = if (isFocused) palette.textPrimary else palette.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
