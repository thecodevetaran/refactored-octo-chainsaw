package com.nexus.bravia

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.bravia.data.AppRepository
import com.nexus.bravia.data.PreferencesRepository
import com.nexus.bravia.data.SystemInfoRepository
import com.nexus.bravia.data.WeatherRepository
import com.nexus.bravia.model.AppInfo
import com.nexus.bravia.model.NexusSettings
import com.nexus.bravia.model.SystemStats
import com.nexus.bravia.model.WeatherState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = PreferencesRepository(application)
    private val appRepo = AppRepository(application)
    private val sysInfoRepo = SystemInfoRepository(application)
    private val weatherRepo = WeatherRepository()

    val settings: StateFlow<NexusSettings> = prefsRepo.settings
    val favoritePackages: StateFlow<Set<String>> = prefsRepo.favorites
    val recentPackages: StateFlow<List<String>> = prefsRepo.recentPackages

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(true)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    private val _systemStats = MutableStateFlow(sysInfoRepo.getSystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Disabled)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    val favoriteApps: StateFlow<List<AppInfo>> = combine(_allApps, favoritePackages) { apps, favs ->
        apps.filter { favs.contains(it.packageName) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentApps: StateFlow<List<AppInfo>> = combine(_allApps, recentPackages) { apps, recents ->
        val byPkg = apps.associateBy { it.packageName }
        recents.mapNotNull { byPkg[it] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaApps: StateFlow<List<AppInfo>> = _allApps.combine(favoritePackages) { apps, _ ->
        apps.filter { it.isMediaApp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshApps()
        startTelemetryLoop()
        observeWeatherSettings()
    }

    fun refreshApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            _allApps.value = appRepo.loadInstalledApps()
            _isLoadingApps.value = false
        }
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (isActive) {
                _systemStats.value = sysInfoRepo.getSystemStats()
                delay(5_000L)
            }
        }
    }

    private fun observeWeatherSettings() {
        viewModelScope.launch {
            var lastCity: String? = null
            settings.collect { current ->
                val city = current.weatherCity.trim()
                if (city.isEmpty()) {
                    lastCity = ""
                    _weatherState.value = WeatherState.Disabled
                } else if (city != lastCity) {
                    lastCity = city
                    refreshWeather(city)
                }
            }
        }
    }

    fun refreshWeather(city: String = settings.value.weatherCity) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            _weatherState.value = WeatherState.Disabled
            return
        }
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading(trimmed)
            _weatherState.value = weatherRepo.fetchWeather(trimmed)
        }
    }

    fun launchApp(app: AppInfo) {
        val intent = app.launchIntent
        if (intent == null) {
            showBanner("${app.label} cannot be launched directly on this device.")
            return
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
            prefsRepo.recordAppLaunch(app.packageName)
        } catch (e: Exception) {
            showBanner("Unable to launch ${app.label}: ${e.localizedMessage ?: "Unsupported activity"}")
        }
    }

    fun openSystemIntent(intent: Intent, fallbackMessage: String) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {
            showBanner(fallbackMessage)
        }
    }

    fun toggleFavorite(packageName: String, appLabel: String) {
        val wasFav = favoritePackages.value.contains(packageName)
        prefsRepo.toggleFavorite(packageName)
        showBanner(if (wasFav) "Removed $appLabel from Favorites" else "Pinned $appLabel to Favorites")
    }

    fun updateSettings(transform: (NexusSettings) -> NexusSettings) {
        prefsRepo.updateSettings(transform)
    }

    fun showBanner(message: String) {
        viewModelScope.launch {
            _statusBanner.value = message
            delay(3_200L)
            if (_statusBanner.value == message) {
                _statusBanner.value = null
            }
        }
    }
}
