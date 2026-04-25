/*
 * Copyright (C) 2025 AxionOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)

package com.android.axion.axthemestore.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.android.axion.axthemestore.R
import com.android.axion.axthemestore.data.model.Theme
import com.android.axion.axthemestore.data.model.ThemeInstallState
import com.android.axion.axthemestore.data.model.formatFileSize
import com.android.axion.axthemestore.data.model.hasUpdate

@Composable
fun ThemeCard(
    theme: Theme,
    installState: ThemeInstallState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(MaterialTheme.shapes.extraLarge)
            ) {
                LocalPreviewBox(theme = theme)
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = theme.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = theme.author,
                                style = MaterialTheme.typography.labelLargeEmphasized,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        if (theme.totalFileSize > 0) {
                            Text(
                                text = theme.totalFileSize.formatFileSize(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    InstallStateBadge(
                        state = installState,
                        theme = theme,
                    )
                }
            }
        }
    }
}

@Composable
private fun InstallStateBadge(
    state: ThemeInstallState,
    theme: Theme,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor, contentColor) = when (state) {
        is ThemeInstallState.NotInstalled -> Triple(
            Icons.Default.Download,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        is ThemeInstallState.Installed -> {
            if (theme.hasUpdate(state.installedVersionCode)) {
                Triple(
                    Icons.Default.Update,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.onTertiary
                )
            } else {
                Triple(
                    Icons.Default.Check,
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        is ThemeInstallState.InstalledInactive -> {
            if (theme.hasUpdate(state.installedVersionCode)) {
                Triple(
                    Icons.Default.Update,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.onTertiary
                )
            } else {
                Triple(
                    Icons.Default.Download,
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        is ThemeInstallState.PartiallyInstalled -> Triple(
            Icons.Default.Download,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        is ThemeInstallState.Downloading -> Triple(
            null,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        is ThemeInstallState.Installing -> Triple(
            null,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        is ThemeInstallState.Error -> Triple(
            Icons.Default.Error,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        is ThemeInstallState.Downloaded -> Triple(
            Icons.Default.Download,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ThemeInstallState.Downloading -> {
                val animatedProgress by animateFloatAsState(
                    targetValue = state.progress,
                    label = "download_progress"
                )
                LoadingIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(24.dp),
                    color = contentColor
                )
            }
            is ThemeInstallState.Installing -> {
                LoadingIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor
                )
            }
            else -> {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalPreviewBox(theme: Theme) {
    val context = LocalContext.current
    val packageName = theme.overlays.firstOrNull()?.packageName ?: ""
    val category = theme.category.ifEmpty { theme.overlays.firstOrNull()?.componentId ?: "" }
    val resIds = remember(packageName) { previewResIdsFor(context, packageName) }

    val isBattery = packageName.contains("battery") || category.contains("battery")
    val isBackGesture = packageName.contains("back_gesture") || category.contains("back_gesture")
    val isUdfpsAnim = packageName.contains("udfps_animation") || category.contains("udfps_animation")
    val isChargingAnim = packageName.contains("charging_animation") || category.contains("charging_animation")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        if (resIds.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                resIds.forEach { resId ->
                    GlideImage(
                        model = resId,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.onSurface
                        ),
                    )
                }
            }
        } else if (isBattery) {
            BatteryStylePreview(
                packageName = packageName,
                modifier = Modifier.fillMaxSize()
            )
        } else if (isBackGesture) {
            BackGesturePreview(modifier = Modifier.fillMaxSize())
        } else if (isChargingAnim) {
            ChargingAnimationBannerPreview(
                packageName = packageName,
                modifier = Modifier.fillMaxSize(),
                animate = false,
            )
        } else if (isUdfpsAnim) {
            UdfpsAnimationBannerPreview(
                packageName = packageName,
                modifier = Modifier.fillMaxSize(),
                animate = false,
            )
        } else if (theme.previewImages.isNotEmpty()) {
            AsyncNetworkImage(
                url = theme.previewImages.first(),
                contentDescription = theme.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                errorContent = {
                    Icon(
                        imageVector = categoryIcon(theme),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            )
        } else {
            Icon(
                imageVector = categoryIcon(theme),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

private val sPreviewPrefixCache = mutableMapOf<String, String>()
private var sPreviewPrefixLoaded = false
private val sPreviewIdsCache = mutableMapOf<String, List<Int>>()

private fun previewResIdsFor(context: Context, packageName: String): List<Int> {
    if (packageName.isEmpty()) return emptyList()
    if (!sPreviewPrefixLoaded) {
        try {
            val entries = context.resources.getStringArray(R.array.overlay_preview_map)
            for (entry in entries) {
                val parts = entry.split("|", limit = 2)
                if (parts.size == 2) sPreviewPrefixCache[parts[0]] = parts[1]
            }
        } catch (_: Exception) {}
        sPreviewPrefixLoaded = true
    }
    sPreviewIdsCache[packageName]?.let { return it }
    val prefix = sPreviewPrefixCache[packageName] ?: run {
        sPreviewIdsCache[packageName] = emptyList()
        return emptyList()
    }
    val ids = (1..4).mapNotNull { i ->
        val id = context.resources.getIdentifier("${prefix}_$i", "drawable", context.packageName)
        if (id != 0) id else null
    }
    sPreviewIdsCache[packageName] = ids
    return ids
}

private fun categoryIcon(theme: Theme): ImageVector {
    val category = theme.category.ifEmpty {
        theme.overlays.firstOrNull()?.componentId ?: ""
    }
    return when {
        "charging" in category -> Icons.Default.BatteryChargingFull
        "battery" in category -> Icons.Default.BatteryFull
        "wifi" in category -> Icons.Default.Wifi
        "signal" in category -> Icons.Default.SignalCellular4Bar
        "icon_pack" in category -> Icons.Default.Apps
        "back_gesture" in category -> Icons.Default.Gesture
        "volume" in category -> Icons.Default.VolumeUp
        "ui_qs" in category || "qs" in category -> Icons.Default.Dashboard
        else -> Icons.Default.Palette
    }
}
