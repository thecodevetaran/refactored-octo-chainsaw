package com.nexus.bravia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.model.ClockStyle
import com.nexus.bravia.model.NexusSettings
import com.nexus.bravia.model.WeatherState
import com.nexus.bravia.ui.theme.LocalNexusPalette
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockHeroWidget(
    settings: NexusSettings,
    weatherState: WeatherState,
    modifier: Modifier = Modifier
) {
    val palette = LocalNexusPalette.current
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(settings.showSeconds) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(if (settings.showSeconds) 1_000L else 15_000L)
        }
    }

    val timePattern = when {
        settings.use24HourClock && settings.showSeconds -> "HH:mm:ss"
        settings.use24HourClock -> "HH:mm"
        settings.showSeconds -> "hh:mm:ss a"
        else -> "hh:mm a"
    }

    val timeText = remember(nowMillis, timePattern) {
        SimpleDateFormat(timePattern, Locale.getDefault()).format(Date(nowMillis))
    }
    val dateText = remember(nowMillis) {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(nowMillis))
    }

    val fontFamily = when (settings.clockStyle) {
        ClockStyle.COMMAND -> FontFamily.SansSerif
        ClockStyle.MINIMAL -> FontFamily.SansSerif
        ClockStyle.TELEMETRY -> FontFamily.Monospace
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Custom NEXUS Branding Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(palette.accentPrimary.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(palette.accentPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BRAVIA // NEXUS OS",
                    color = palette.accentPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = timeText,
                color = palette.textPrimary,
                fontSize = if (settings.clockStyle == ClockStyle.MINIMAL) 52.sp else 58.sp,
                fontWeight = if (settings.clockStyle == ClockStyle.MINIMAL) FontWeight.Light else FontWeight.SemiBold,
                fontFamily = fontFamily,
                letterSpacing = if (settings.clockStyle == ClockStyle.TELEMETRY) 2.sp else (-1).sp
            )

            Text(
                text = dateText.uppercase(),
                color = palette.textSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
        }

        // Weather Panel (only active when a weather source city is configured)
        if (weatherState !is WeatherState.Disabled) {
            GlassPanel(
                modifier = Modifier.padding(start = 24.dp),
                cornerRadius = 14.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (weatherState is WeatherState.Error) Icons.Rounded.CloudOff else Icons.Rounded.Cloud,
                        contentDescription = "Weather",
                        tint = palette.accentPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    when (weatherState) {
                        is WeatherState.Loading -> {
                            Text(
                                text = "Syncing ${weatherState.city}...",
                                color = palette.textSecondary,
                                fontSize = 14.sp
                            )
                        }
                        is WeatherState.Ready -> {
                            Column {
                                Text(
                                    text = "${weatherState.temperatureC.toInt()}°C • ${weatherState.condition}",
                                    color = palette.textPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${weatherState.city} • Wind ${weatherState.windKmh.toInt()} km/h",
                                    color = palette.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        is WeatherState.Error -> {
                            Column {
                                Text(
                                    text = weatherState.city,
                                    color = palette.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = weatherState.message,
                                    color = palette.warning,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        WeatherState.Disabled -> Unit
                    }
                }
            }
        }
    }
}
