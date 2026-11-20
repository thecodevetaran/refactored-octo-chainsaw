package com.nexus.bravia.data

import android.content.Context
import android.content.SharedPreferences
import com.nexus.bravia.model.BackgroundMode
import com.nexus.bravia.model.ClockStyle
import com.nexus.bravia.model.NexusSettings
import com.nexus.bravia.model.ThemeAccent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<NexusSettings> = _settings.asStateFlow()

    private val _favorites = MutableStateFlow(loadFavorites())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _recentPackages = MutableStateFlow(loadRecents())
    val recentPackages: StateFlow<List<String>> = _recentPackages.asStateFlow()

    private fun loadSettings(): NexusSettings {
        val accentName = prefs.getString(KEY_ACCENT, ThemeAccent.ELECTRIC_BLUE.name)
        val clockName = prefs.getString(KEY_CLOCK_STYLE, ClockStyle.COMMAND.name)
        val bgName = prefs.getString(KEY_BG_MODE, BackgroundMode.NEBULA.name)

        return NexusSettings(
            themeAccent = runCatching { ThemeAccent.valueOf(accentName ?: "") }
                .getOrDefault(ThemeAccent.ELECTRIC_BLUE),
            clockStyle = runCatching { ClockStyle.valueOf(clockName ?: "") }
                .getOrDefault(ClockStyle.COMMAND),
            backgroundMode = runCatching { BackgroundMode.valueOf(bgName ?: "") }
                .getOrDefault(BackgroundMode.NEBULA),
            use24HourClock = prefs.getBoolean(KEY_24H, false),
            showSeconds = prefs.getBoolean(KEY_SECONDS, false),
            showSystemTelemetry = prefs.getBoolean(KEY_SHOW_TELEMETRY, true),
            showMediaHub = prefs.getBoolean(KEY_SHOW_MEDIA, true),
            showQuickSettings = prefs.getBoolean(KEY_SHOW_QUICK_SETTINGS, true),
            showRecentApps = prefs.getBoolean(KEY_SHOW_RECENTS, true),
            weatherCity = prefs.getString(KEY_WEATHER_CITY, "") ?: ""
        )
    }

    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet())?.toSet() ?: emptySet()
    }

    private fun loadRecents(): List<String> {
        val raw = prefs.getString(KEY_RECENTS, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun updateSettings(transform: (NexusSettings) -> NexusSettings) {
        val updated = transform(_settings.value)
        _settings.value = updated
        prefs.edit()
            .putString(KEY_ACCENT, updated.themeAccent.name)
            .putString(KEY_CLOCK_STYLE, updated.clockStyle.name)
            .putString(KEY_BG_MODE, updated.backgroundMode.name)
            .putBoolean(KEY_24H, updated.use24HourClock)
            .putBoolean(KEY_SECONDS, updated.showSeconds)
            .putBoolean(KEY_SHOW_TELEMETRY, updated.showSystemTelemetry)
            .putBoolean(KEY_SHOW_MEDIA, updated.showMediaHub)
            .putBoolean(KEY_SHOW_QUICK_SETTINGS, updated.showQuickSettings)
            .putBoolean(KEY_SHOW_RECENTS, updated.showRecentApps)
            .putString(KEY_WEATHER_CITY, updated.weatherCity.trim())
            .apply()
    }

    fun toggleFavorite(packageName: String) {
        val current = _favorites.value.toMutableSet()
        if (!current.add(packageName)) {
            current.remove(packageName)
        }
        _favorites.value = current
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun recordAppLaunch(packageName: String) {
        val updated = listOf(packageName) + _recentPackages.value.filter { it != packageName }
        val trimmed = updated.take(MAX_RECENTS)
        _recentPackages.value = trimmed
        prefs.edit().putString(KEY_RECENTS, trimmed.joinToString(",")).apply()
    }

    companion object {
        private const val PREFS_NAME = "bravia_nexus_prefs"
        private const val KEY_ACCENT = "theme_accent"
        private const val KEY_CLOCK_STYLE = "clock_style"
        private const val KEY_BG_MODE = "bg_mode"
        private const val KEY_24H = "use_24h"
        private const val KEY_SECONDS = "show_seconds"
        private const val KEY_SHOW_TELEMETRY = "show_telemetry"
        private const val KEY_SHOW_MEDIA = "show_media"
        private const val KEY_SHOW_QUICK_SETTINGS = "show_quick_settings"
        private const val KEY_SHOW_RECENTS = "show_recents"
        private const val KEY_WEATHER_CITY = "weather_city"
        private const val KEY_FAVORITES = "favorite_packages"
        private const val KEY_RECENTS = "recent_packages"
        private const val MAX_RECENTS = 10
    }
}
