package com.nexus.bravia.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.nexus.bravia.model.BackgroundMode
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun AmbientBackground(
    mode: BackgroundMode,
    modifier: Modifier = Modifier
) {
    val palette = LocalNexusPalette.current
    val transition = rememberInfiniteTransition(label = "bg_ambient")

    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.backgroundDeep)
    ) {
        when (mode) {
            BackgroundMode.NEBULA -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val orb1Center = Offset(w * (0.18f + 0.08f * drift), h * (0.22f + 0.05f * drift))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.accentPrimary.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = orb1Center,
                            radius = w * 0.45f
                        ),
                        radius = w * 0.45f,
                        center = orb1Center
                    )

                    val orb2Center = Offset(w * (0.82f - 0.08f * drift), h * (0.76f - 0.06f * drift))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.accentSecondary.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = orb2Center,
                            radius = w * 0.48f
                        ),
                        radius = w * 0.48f,
                        center = orb2Center
                    )
                }
            }

            BackgroundMode.GRID -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val step = 72f
                    val gridAlpha = 0.055f + 0.025f * drift

                    var x = 0f
                    while (x <= w) {
                        drawLine(
                            color = palette.accentPrimary.copy(alpha = gridAlpha),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f
                        )
                        x += step
                    }

                    var y = 0f
                    while (y <= h) {
                        drawLine(
                            color = palette.accentPrimary.copy(alpha = gridAlpha),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.accentPrimary.copy(alpha = 0.14f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.5f, h * 0.15f),
                            radius = w * 0.55f
                        ),
                        radius = w * 0.55f,
                        center = Offset(w * 0.5f, h * 0.15f)
                    )
                }
            }

            BackgroundMode.OBSIDIAN -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF05070B),
                                Color(0xFF0A0E17)
                            )
                        )
                    )
                }
            }
        }
    }
}
