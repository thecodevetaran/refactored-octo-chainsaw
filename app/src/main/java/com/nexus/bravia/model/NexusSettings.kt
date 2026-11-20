package com.nexus.bravia.model

enum class ThemeAccent(val displayName: String, val primaryHex: Long, val secondaryHex: Long) {
    ELECTRIC_BLUE("Electric Blue", 0xFF38BDF8, 0xFF818CF8),
    CYBER_VIOLET("Cyber Violet", 0xFFA855F7, 0xFF38BDF8),
    EMERALD_PULSE("Emerald Pulse", 0xFF34D399, 0xFF38BDF8),
    AMBER_HORIZON("Amber Horizon", 0xFFF59E0B, 0xFFF43F5E)
}

enum class ClockStyle(val displayName: String) {
    COMMAND("Command Digital"),
    MINIMAL("Minimalist"),
    TELEMETRY("Telemetry Mono")
}

enum class BackgroundMode(val displayName: String) {
    NEBULA("Ambient Nebula"),
    GRID("Neural Grid"),
    OBSIDIAN("Pure Obsidian")
}

data class NexusSettings(
    val themeAccent: ThemeAccent = ThemeAccent.ELECTRIC_BLUE,
    val clockStyle: ClockStyle = ClockStyle.COMMAND,
    val backgroundMode: BackgroundMode = BackgroundMode.NEBULA,
    val use24HourClock: Boolean = false,
    val showSeconds: Boolean = false,
    val showSystemTelemetry: Boolean = true,
    val showMediaHub: Boolean = true,
    val showQuickSettings: Boolean = true,
    val showRecentApps: Boolean = true,
    val weatherCity: String = ""
)
