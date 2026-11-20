package com.nexus.bravia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.nexus.bravia.model.AppInfo
import com.nexus.bravia.ui.components.AppCard
import com.nexus.bravia.ui.components.GlassPanel
import com.nexus.bravia.ui.theme.LocalNexusPalette

private enum class AppFilter(val label: String) {
    ALL("All Apps"),
    TV("TV Apps"),
    STANDARD("Sideloaded"),
    FAVORITES("Favorites")
}

@Composable
fun AllAppsOverlay(
    allApps: List<AppInfo>,
    favoritePackages: Set<String>,
    onLaunchApp: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    val palette = LocalNexusPalette.current
    var selectedFilter by remember { mutableStateOf(AppFilter.ALL) }

    val filteredApps = remember(allApps, favoritePackages, selectedFilter) {
        when (selectedFilter) {
            AppFilter.ALL -> allApps
            AppFilter.TV -> allApps.filter { it.isLeanback }
            AppFilter.STANDARD -> allApps.filter { !it.isLeanback }
            AppFilter.FAVORITES -> allApps.filter { favoritePackages.contains(it.packageName) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.backgroundDeep.copy(alpha = 0.92f))
            .padding(horizontal = 48.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "APPLICATION MATRIX",
                    color = palette.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Press OK to launch • Press MENU to pin/unpin favorite • Press BACK to return",
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
                    text = "BACK TO DASHBOARD",
                    color = if (isFocused) palette.accentPrimary else palette.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppFilter.entries.forEach { filter ->
                val isSelected = filter == selectedFilter
                GlassPanel(
                    focusableItem = true,
                    cornerRadius = 12.dp,
                    onClick = { selectedFilter = filter }
                ) { isFocused ->
                    Text(
                        text = filter.label,
                        color = when {
                            isFocused -> palette.accentPrimary
                            isSelected -> palette.textPrimary
                            else -> palette.textSecondary
                        },
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (filteredApps.isEmpty()) {
            Text(
                text = "No applications match this filter.",
                color = palette.textSecondary,
                fontSize = 15.sp
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 168.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredApps, key = { "grid_${it.packageName}" }) { app ->
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
}
