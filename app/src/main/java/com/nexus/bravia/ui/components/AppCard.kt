package com.nexus.bravia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.bravia.model.AppInfo
import com.nexus.bravia.ui.theme.LocalNexusPalette

@Composable
fun AppCard(
    app: AppInfo,
    isFavorite: Boolean,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalNexusPalette.current

    GlassPanel(
        modifier = modifier
            .width(168.dp)
            .height(132.dp),
        cornerRadius = 16.dp,
        focusableItem = true,
        onClick = onLaunch,
        onMenuAction = onToggleFavorite
    ) { isFocused ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Top badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!app.isLeanback) {
                    Text(
                        text = "STD",
                        color = palette.textSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(palette.borderSubtle)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Favorite",
                        tint = palette.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Center icon + label
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                if (app.icon != null) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = app.label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(palette.accentPrimary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Android,
                            contentDescription = app.label,
                            tint = palette.accentPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = app.label,
                    color = if (isFocused) palette.textPrimary else palette.textPrimary.copy(alpha = 0.88f),
                    fontSize = 14.sp,
                    fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
