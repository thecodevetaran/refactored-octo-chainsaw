package com.nexus.bravia.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.model.AppInfo
import com.nexus.bravia.model.NexusSettings
import com.nexus.bravia.model.SystemStats
import com.nexus.bravia.model.WeatherState
import com.nexus.bravia.ui.components.AppCard
import com.nexus.bravia.ui.components.ClockHeroWidget
import com.nexus.bravia.ui.components.MediaHubPanel
import com.nexus.bravia.ui.components.QuickSettingsBar
import com.nexus.bravia.ui.components.SystemTelemetryPanel
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun HomeDashboardScreen(
    settings: NexusSettings,
    allApps: List<AppInfo>,
    favoriteApps: List<AppInfo>,
    recentApps: List<AppInfo>,
    mediaApps: List<AppInfo>,
    favoritePackages: Set<String>,
    systemStats: SystemStats,
    weatherState: WeatherState,
    isLoadingApps: Boolean,
    onLaunchApp: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
    onOpenAllApps: () -> Unit,
    onOpenSettings: () -> Unit,
    onLaunchSystemIntent: (Intent, String) -> Unit
) {
    val palette = LocalNexusPalette.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 44.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        // Top Header: Clock Hero + Optional Weather + Quick Settings
        item(key = "header_hero") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClockHeroWidget(
                    settings = settings,
                    weatherState = weatherState,
                    modifier = Modifier.weight(1f)
                )

                if (settings.showQuickSettings) {
                    QuickSettingsBar(
                        onOpenAllApps = onOpenAllApps,
                        onOpenNexusSettings = onOpenSettings,
                        onLaunchSystemIntent = onLaunchSystemIntent
                    )
                }
            }
        }

        // Favorites Section
        if (favoriteApps.isNotEmpty()) {
            item(key = "favorites_section") {
                AppCarouselSection(
                    title = "PINNED FAVORITES",
                    subtitle = "Press MENU on any app to pin/unpin",
                    apps = favoriteApps,
                    favoritePackages = favoritePackages,
                    onLaunchApp = onLaunchApp,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }

        // Recently Used Section
        if (settings.showRecentApps && recentApps.isNotEmpty()) {
            item(key = "recents_section") {
                AppCarouselSection(
                    title = "RECENTLY LAUNCHED",
                    subtitle = "Quick resume",
                    apps = recentApps,
                    favoritePackages = favoritePackages,
                    onLaunchApp = onLaunchApp,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }

        // Main Application Carousel
        item(key = "all_apps_carousel") {
            if (isLoadingApps) {
                Text(
                    text = "Scanning installed TV applications...",
                    color = palette.textSecondary,
                    fontSize = 15.sp
                )
            } else {
                AppCarouselSection(
                    title = "APPLICATIONS (${allApps.size})",
                    subtitle = "OK to launch • MENU to favorite",
                    apps = allApps,
                    favoritePackages = favoritePackages,
                    onLaunchApp = onLaunchApp,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }

        // Customizable Bottom Widgets Row
        if (settings.showSystemTelemetry || settings.showMediaHub) {
            item(key = "widgets_row") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (settings.showSystemTelemetry) {
                        SystemTelemetryPanel(
                            stats = systemStats,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (settings.showMediaHub) {
                        MediaHubPanel(
                            mediaApps = mediaApps,
                            onLaunchApp = onLaunchApp,
                            modifier = Modifier.weight(1.35f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppCarouselSection(
    title: String,
    subtitle: String,
    apps: List<AppInfo>,
    favoritePackages: Set<String>,
    onLaunchApp: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit
) {
    val palette = LocalNexusPalette.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = palette.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp
            )
            Text(
                text = subtitle,
                color = palette.textSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(apps, key = { "${title}_${it.packageName}" }) { app ->
                AppCard(
                    app = app,
                    isFavorite = favoritePackages.contains(app.packageName),
                    onLaunch = { onLaunchApp(app) },
                    onToggleFavorite = { onToggleFavorite(app) }
                )
            }
        }
    }
}
