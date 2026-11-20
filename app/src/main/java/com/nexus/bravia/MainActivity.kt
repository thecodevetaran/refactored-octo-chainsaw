package com.nexus.bravia

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nexus.bravia.ui.components.AmbientBackground
import com.nexus.bravia.ui.components.GlassPanel
import com.nexus.bravia.ui.screens.AllAppsOverlay
import com.nexus.bravia.ui.screens.HomeDashboardScreen
import com.nexus.bravia.ui.screens.SettingsScreen
import com.nexus.bravia.ui.theme.BraviaNexusTheme
import com.nexus.bravia.ui.theme.LocalNexusPalette

private enum class ActiveScreen {
    DASHBOARD,
    ALL_APPS,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshApps()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableImmersiveTvMode()
        registerPackageUpdates()

        setContent {
            val settings by viewModel.settings.collectAsState()
            val allApps by viewModel.allApps.collectAsState()
            val favoriteApps by viewModel.favoriteApps.collectAsState()
            val recentApps by viewModel.recentApps.collectAsState()
            val mediaApps by viewModel.mediaApps.collectAsState()
            val favoritePackages by viewModel.favoritePackages.collectAsState()
            val systemStats by viewModel.systemStats.collectAsState()
            val weatherState by viewModel.weatherState.collectAsState()
            val isLoadingApps by viewModel.isLoadingApps.collectAsState()
            val statusBanner by viewModel.statusBanner.collectAsState()

            var activeScreen by remember { mutableStateOf(ActiveScreen.DASHBOARD) }

            BackHandler(enabled = activeScreen != ActiveScreen.DASHBOARD) {
                activeScreen = ActiveScreen.DASHBOARD
            }

            BraviaNexusTheme(accent = settings.themeAccent) {
                val palette = LocalNexusPalette.current

                Box(modifier = Modifier.fillMaxSize()) {
                    AmbientBackground(mode = settings.backgroundMode)

                    HomeDashboardScreen(
                        settings = settings,
                        allApps = allApps,
                        favoriteApps = favoriteApps,
                        recentApps = recentApps,
                        mediaApps = mediaApps,
                        favoritePackages = favoritePackages,
                        systemStats = systemStats,
                        weatherState = weatherState,
                        isLoadingApps = isLoadingApps,
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it.packageName, it.label) },
                        onOpenAllApps = { activeScreen = ActiveScreen.ALL_APPS },
                        onOpenSettings = { activeScreen = ActiveScreen.SETTINGS },
                        onLaunchSystemIntent = { intent, fallback ->
                            viewModel.openSystemIntent(intent, fallback)
                        }
                    )

                    AnimatedVisibility(
                        visible = activeScreen == ActiveScreen.ALL_APPS,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AllAppsOverlay(
                            allApps = allApps,
                            favoritePackages = favoritePackages,
                            onLaunchApp = { viewModel.launchApp(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it.packageName, it.label) },
                            onClose = { activeScreen = ActiveScreen.DASHBOARD }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeScreen == ActiveScreen.SETTINGS,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        SettingsScreen(
                            settings = settings,
                            onUpdateSettings = { viewModel.updateSettings(it) },
                            onLaunchSystemIntent = { intent, fallback ->
                                viewModel.openSystemIntent(intent, fallback)
                            },
                            onClose = { activeScreen = ActiveScreen.DASHBOARD }
                        )
                    }

                    // Toast-style Glass Status Banner
                    AnimatedVisibility(
                        visible = statusBanner != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 28.dp)
                    ) {
                        GlassPanel(cornerRadius = 12.dp) {
                            Text(
                                text = statusBanner ?: "",
                                color = palette.textPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(packageReceiver) }
        super.onDestroy()
    }

    private fun registerPackageUpdates() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)
    }

    private fun enableImmersiveTvMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
