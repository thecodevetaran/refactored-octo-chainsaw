package com.nexus.bravia.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.model.BackgroundMode
import com.nexus.bravia.model.ClockStyle
import com.nexus.bravia.model.NexusSettings
import com.nexus.bravia.model.ThemeAccent
import com.nexus.bravia.ui.components.GlassPanel
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun SettingsScreen(
    settings: NexusSettings,
    onUpdateSettings: ((NexusSettings) -> NexusSettings) -> Unit,
    onLaunchSystemIntent: (Intent, String) -> Unit,
    onClose: () -> Unit
) {
    val palette = LocalNexusPalette.current
    var cityInput by remember(settings.weatherCity) { mutableStateOf(settings.weatherCity) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.backgroundDeep.copy(alpha = 0.94f))
            .padding(horizontal = 48.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NEXUS COMMAND SETTINGS",
                    color = palette.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "All preferences are persisted locally on your TV",
                    color = palette.textSecondary,
                    fontSize = 12.sp
                )
            }

            GlassPanel(
                focusableItem = true,
                cornerRadius = 12.dp,
                onClick = onClose
            ) { isFocused ->
                Text(
                    text = "DONE (BACK)",
                    color = if (isFocused) palette.accentPrimary else palette.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Theme Accent
            item(key = "set_theme") {
                SettingsSection(title = "THEME ACCENT") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ThemeAccent.entries.forEach { accent ->
                            OptionChip(
                                label = accent.displayName,
                                selected = settings.themeAccent == accent,
                                onClick = { onUpdateSettings { it.copy(themeAccent = accent) } }
                            )
                        }
                    }
                }
            }

            // 2. Background Mode
            item(key = "set_bg") {
                SettingsSection(title = "BACKGROUND ANIMATION") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BackgroundMode.entries.forEach { mode ->
                            OptionChip(
                                label = mode.displayName,
                                selected = settings.backgroundMode == mode,
                                onClick = { onUpdateSettings { it.copy(backgroundMode = mode) } }
                            )
                        }
                    }
                }
            }

            // 3. Clock Customization
            item(key = "set_clock") {
                SettingsSection(title = "CLOCK & TIME FORMAT") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClockStyle.entries.forEach { style ->
                            OptionChip(
                                label = style.displayName,
                                selected = settings.clockStyle == style,
                                onClick = { onUpdateSettings { it.copy(clockStyle = style) } }
                            )
                        }
                        OptionChip(
                            label = if (settings.use24HourClock) "24-Hour: ON" else "24-Hour: OFF",
                            selected = settings.use24HourClock,
                            onClick = { onUpdateSettings { it.copy(use24HourClock = !it.use24HourClock) } }
                        )
                        OptionChip(
                            label = if (settings.showSeconds) "Seconds: ON" else "Seconds: OFF",
                            selected = settings.showSeconds,
                            onClick = { onUpdateSettings { it.copy(showSeconds = !it.showSeconds) } }
                        )
                    }
                }
            }

            // 4. Dashboard Widgets
            item(key = "set_widgets") {
                SettingsSection(title = "DASHBOARD WIDGETS") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OptionChip(
                            label = "Quick Settings: ${if (settings.showQuickSettings) "ON" else "OFF"}",
                            selected = settings.showQuickSettings,
                            onClick = { onUpdateSettings { it.copy(showQuickSettings = !it.showQuickSettings) } }
                        )
                        OptionChip(
                            label = "Recent Apps: ${if (settings.showRecentApps) "ON" else "OFF"}",
                            selected = settings.showRecentApps,
                            onClick = { onUpdateSettings { it.copy(showRecentApps = !it.showRecentApps) } }
                        )
                        OptionChip(
                            label = "System Telemetry: ${if (settings.showSystemTelemetry) "ON" else "OFF"}",
                            selected = settings.showSystemTelemetry,
                            onClick = { onUpdateSettings { it.copy(showSystemTelemetry = !it.showSystemTelemetry) } }
                        )
                        OptionChip(
                            label = "Media Hub: ${if (settings.showMediaHub) "ON" else "OFF"}",
                            selected = settings.showMediaHub,
                            onClick = { onUpdateSettings { it.copy(showMediaHub = !it.showMediaHub) } }
                        )
                    }
                }
            }

            // 5. Optional Weather Source
            item(key = "set_weather") {
                SettingsSection(title = "OPTIONAL WEATHER SOURCE (OPEN-METEO FREE API)") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            placeholder = { Text("Enter city (e.g. Tokyo) or leave blank for offline", fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = palette.textPrimary,
                                unfocusedTextColor = palette.textPrimary,
                                focusedBorderColor = palette.accentPrimary,
                                unfocusedBorderColor = palette.borderSubtle
                            ),
                            modifier = Modifier.width(340.dp)
                        )
                        OptionChip(
                            label = "Apply City",
                            selected = false,
                            onClick = { onUpdateSettings { it.copy(weatherCity = cityInput.trim()) } }
                        )
                        OptionChip(
                            label = "Disable (Offline)",
                            selected = settings.weatherCity.isBlank(),
                            onClick = {
                                cityInput = ""
                                onUpdateSettings { it.copy(weatherCity = "") }
                            }
                        )
                    }
                }
            }

            // 6. Default Home Launcher
            item(key = "set_home_launcher") {
                SettingsSection(title = "SONY BRAVIA DEFAULT HOME LAUNCHER") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OptionChip(
                            label = "Open Default Home App Settings",
                            selected = false,
                            onClick = {
                                onLaunchSystemIntent(
                                    Intent(Settings.ACTION_HOME_SETTINGS),
                                    "Your TV firmware restricts changing the default Home app via Settings."
                                )
                            }
                        )
                        Text(
                            text = "Note: Some Sony Google TV firmware versions lock the Home button to the stock launcher unless configured via ADB.",
                            color = palette.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    val palette = LocalNexusPalette.current
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = palette.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalNexusPalette.current
    GlassPanel(
        focusableItem = true,
        cornerRadius = 10.dp,
        onClick = onClick
    ) { isFocused ->
        Text(
            text = label,
            color = when {
                isFocused -> palette.accentPrimary
                selected -> palette.textPrimary
                else -> palette.textSecondary
            },
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
