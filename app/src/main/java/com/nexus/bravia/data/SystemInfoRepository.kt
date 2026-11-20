package com.nexus.bravia.data

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import com.nexus.bravia.model.SystemStats

class SystemInfoRepository(private val context: Context) {

    fun getSystemStats(): SystemStats {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)

        val totalRamMb = memInfo.totalMem / (1024L * 1024L)
        val availRamMb = memInfo.availMem / (1024L * 1024L)
        val usedRamMb = (totalRamMb - availRamMb).coerceAtLeast(0L)

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalBytes = statFs.blockCountLong * statFs.blockSizeLong
        val availBytes = statFs.availableBlocksLong * statFs.blockSizeLong
        val bytesPerGb = 1024.0 * 1024.0 * 1024.0

        val (connected, netLabel) = readNetworkStatus()

        val uptimeMillis = SystemClock.elapsedRealtime()
        val totalMinutes = uptimeMillis / 60_000L
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L
        val uptimeStr = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val fullModel = if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model"
        }

        return SystemStats(
            deviceModel = fullModel,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            usedRamMb = usedRamMb,
            totalRamMb = totalRamMb,
            freeStorageGb = availBytes / bytesPerGb,
            totalStorageGb = totalBytes / bytesPerGb,
            networkStatus = netLabel,
            isConnected = connected,
            uptimeFormatted = uptimeStr
        )
    }

    private fun readNetworkStatus(): Pair<Boolean, String> {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false to "Offline"
        val network = cm.activeNetwork ?: return false to "Offline"
        val caps = cm.getNetworkCapabilities(network) ?: return false to "Offline"

        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true to "Ethernet Connected"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true to "Wi-Fi Connected"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true to "Cellular Connected"
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true to "Online"
            else -> false to "Offline"
        }
    }
}
