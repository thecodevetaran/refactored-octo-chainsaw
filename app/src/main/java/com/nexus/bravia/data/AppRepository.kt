package com.nexus.bravia.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.nexus.bravia.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val knownMediaPackages = setOf(
        "com.google.android.youtube.tv",
        "com.google.android.youtube",
        "com.netflix.ninja",
        "com.netflix.mediaclient",
        "com.amazon.amazonvideo.livingroom",
        "com.amazon.avod.thirdpartyclient",
        "com.disney.disneyplus",
        "in.startv.hotstar",
        "com.plexapp.android",
        "org.xbmc.kodi",
        "com.spotify.tv.android",
        "com.spotify.music",
        "org.videolan.vlc",
        "com.apple.atve.androidtv.appletv",
        "tv.twitch.android.app",
        "com.jio.media.stb.ondemand",
        "com.sonyliv"
    )

    suspend fun loadInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val selfPackage = context.packageName

        val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val leanbackResolves = queryActivities(pm, leanbackIntent)
        val standardResolves = queryActivities(pm, launcherIntent)

        val appMap = LinkedHashMap<String, AppInfo>()

        // Prefer Leanback TV entries first
        for (info in leanbackResolves) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == selfPackage || appMap.containsKey(pkg)) continue
            appMap[pkg] = buildAppInfo(pm, info, pkg, isLeanback = true)
        }

        // Add standard/sideloaded apps that lack a Leanback entry
        for (info in standardResolves) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == selfPackage || appMap.containsKey(pkg)) continue
            appMap[pkg] = buildAppInfo(pm, info, pkg, isLeanback = false)
        }

        appMap.values.sortedBy { it.label.lowercase() }
    }

    private fun queryActivities(pm: PackageManager, intent: Intent): List<ResolveInfo> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun buildAppInfo(
        pm: PackageManager,
        resolveInfo: ResolveInfo,
        packageName: String,
        isLeanback: Boolean
    ): AppInfo {
        val label = runCatching { resolveInfo.loadLabel(pm).toString() }
            .getOrDefault(packageName)

        val launchIntent = runCatching {
            if (isLeanback) {
                pm.getLeanbackLaunchIntentForPackage(packageName)
                    ?: pm.getLaunchIntentForPackage(packageName)
            } else {
                pm.getLaunchIntentForPackage(packageName)
                    ?: pm.getLeanbackLaunchIntentForPackage(packageName)
            }
        }.getOrNull()

        val iconBitmap = runCatching {
            val drawable = resolveInfo.loadIcon(pm)
            drawableToImageBitmap(drawable, MAX_ICON_SIZE_PX)
        }.getOrNull()

        val isMedia = knownMediaPackages.contains(packageName) ||
            label.contains("TV", ignoreCase = true) ||
            label.contains("Video", ignoreCase = true) ||
            label.contains("Music", ignoreCase = true) ||
            label.contains("Player", ignoreCase = true)

        return AppInfo(
            packageName = packageName,
            label = label,
            icon = iconBitmap,
            launchIntent = launchIntent,
            isLeanback = isLeanback,
            isMediaApp = isMedia
        )
    }

    private fun drawableToImageBitmap(drawable: Drawable, maxSize: Int): ImageBitmap? {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bmp = drawable.bitmap
            return if (bmp.width > maxSize || bmp.height > maxSize) {
                Bitmap.createScaledBitmap(bmp, maxSize, maxSize, true).asImageBitmap()
            } else {
                bmp.asImageBitmap()
            }
        }

        val width = drawable.intrinsicWidth.takeIf { it > 0 }?.coerceAtMost(maxSize) ?: maxSize
        val height = drawable.intrinsicHeight.takeIf { it > 0 }?.coerceAtMost(maxSize) ?: maxSize
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }

    companion object {
        private const val MAX_ICON_SIZE_PX = 128
    }
}
