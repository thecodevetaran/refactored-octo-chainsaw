package com.nexus.bravia.model

data class SystemStats(
    val deviceModel: String = "Sony BRAVIA",
    val androidVersion: String = "Android TV",
    val usedRamMb: Long = 0L,
    val totalRamMb: Long = 0L,
    val freeStorageGb: Double = 0.0,
    val totalStorageGb: Double = 0.0,
    val networkStatus: String = "Offline",
    val isConnected: Boolean = false,
    val uptimeFormatted: String = "0m"
) {
    val ramUsageFraction: Float
        get() = if (totalRamMb > 0L) (usedRamMb.toFloat() / totalRamMb.toFloat()).coerceIn(0f, 1f) else 0f

    val storageUsedFraction: Float
        get() = if (totalStorageGb > 0.0) ((totalStorageGb - freeStorageGb) / totalStorageGb).toFloat().coerceIn(0f, 1f) else 0f
}
