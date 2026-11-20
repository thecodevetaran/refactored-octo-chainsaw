package com.nexus.bravia.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.nexus.bravia.model.ThemeAccent

data class NexusPalette(
    val backgroundDeep: Color = Color(0xFF06080D),
    val surfaceGlass: Color = Color(0xAA101726),
    val surfaceGlassFocused: Color = Color(0xD9182338),
    val borderSubtle: Color = Color(0x2EFFFFFF),
    val textPrimary: Color = Color(0xFFF8FAFC),
    val textSecondary: Color = Color(0xFF94A3B8),
    val accentPrimary: Color = Color(0xFF38BDF8),
    val accentSecondary: Color = Color(0xFF818CF8),
    val success: Color = Color(0xFF34D399),
    val warning: Color = Color(0xFFFBBF24),
    val danger: Color = Color(0xFFF87171)
)

val LocalNexusPalette = staticCompositionLocalOf { NexusPalette() }

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BraviaNexusTheme(
    accent: ThemeAccent,
    content: @Composable () -> Unit
) {
    val primary = Color(accent.primaryHex)
    val secondary = Color(accent.secondaryHex)

    val palette = NexusPalette(
        accentPrimary = primary,
        accentSecondary = secondary
    )

    val tvColors = darkColorScheme(
        primary = primary,
        secondary = secondary,
        background = palette.backgroundDeep,
        surface = palette.surfaceGlass,
        onBackground = palette.textPrimary,
        onSurface = palette.textPrimary
    )

    CompositionLocalProvider(LocalNexusPalette provides palette) {
        MaterialTheme(
            colorScheme = tvColors,
            content = content
        )
    }
}
