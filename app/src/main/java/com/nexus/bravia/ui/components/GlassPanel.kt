package com.nexus.bravia.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    focusableItem: Boolean = false,
    onClick: (() -> Unit)? = null,
    onMenuAction: (() -> Unit)? = null,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit
) {
    val palette = LocalNexusPalette.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (focusableItem && isFocused) 1.045f else 1f,
        animationSpec = tween(durationMillis = 160),
        label = "glass_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (focusableItem && isFocused) palette.accentPrimary else palette.borderSubtle,
        animationSpec = tween(durationMillis = 160),
        label = "glass_border"
    )

    val bgColor by animateColorAsState(
        targetValue = if (focusableItem && isFocused) palette.surfaceGlassFocused else palette.surfaceGlass,
        animationSpec = tween(durationMillis = 160),
        label = "glass_bg"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val interactiveModifier = if (focusableItem) {
        Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    when (event.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                            onClick?.invoke()
                            true
                        }
                        Key.Menu, Key.Info -> {
                            onMenuAction?.invoke()
                            onMenuAction != null
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        bgColor,
                        bgColor.copy(alpha = (bgColor.alpha * 0.85f))
                    )
                )
            )
            .border(
                width = if (focusableItem && isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .then(interactiveModifier)
    ) {
        content(isFocused)
    }
}
