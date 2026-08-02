package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun WorldMapComponent(
    mapScale: Float,
    offset: Offset,
    state: androidx.compose.foundation.gestures.TransformableState,
    selectedCountryId: String?,
    onCountryClick: (String) -> Unit,
    onCountryLongClick: (String) -> Unit,
    gameplayMode: Boolean = false,
    focusCountryId: String? = null,
    displayScaleMultiplier: Float = 1f,
    modifier: Modifier = Modifier
) {
    val progressMap = ProgressionManager.progressMap
    val mapCoords = WorldMapGeometry.mapCoords
    val density = LocalDensity.current
    val routeTransition = rememberInfiniteTransition(label = "routeFlow")
    val routeDashPhase by routeTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(animation = tween(2400, easing = LinearEasing)),
        label = "routePhase"
    )
    val oceanWavePhase by routeTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(22000, easing = LinearEasing)),
        label = "oceanPhase"
    )
    val activeFocusCountryId = if (gameplayMode) focusCountryId ?: selectedCountryId else selectedCountryId
    val focusProgress by animateFloatAsState(
        targetValue = if (activeFocusCountryId != null) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "focusProgress"
    )
    val focusScale by animateFloatAsState(
        targetValue = if (activeFocusCountryId != null) 1.14f else 1f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "focusScale"
    )

    // Cache Geometry Paths for Performance
    val landPaths = remember { WorldMapGeometry.getLandPaths() }

    FixedCoordinateSurface {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .clipToBounds()
                .background(PremiumColors.OceanDeep)
                .let {
                    if (gameplayMode) it else it.transformable(state = state)
                }
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            if (widthPx > 0 && heightPx > 0) {
                // Authoritative Aspect-Fit Transform (1000x500)
                val baseScale = min(
                    widthPx / WorldMapGeometry.MAP_WIDTH,
                    heightPx / WorldMapGeometry.MAP_HEIGHT
                ) * displayScaleMultiplier
                val baseOffsetX = (widthPx - WorldMapGeometry.MAP_WIDTH * baseScale) / 2f
                val baseOffsetY = (heightPx - WorldMapGeometry.MAP_HEIGHT * baseScale) / 2f
                val focusLayout = activeFocusCountryId?.let { id ->
                    val anchor = mapCoords[id] ?: Offset.Zero
                    val layout = WorldMapGeometry.markerLayouts[id] ?: WorldMapGeometry.MarkerLayout()
                    anchor + layout.visualOffset
                }
                val focusDisplayScale = baseScale * mapScale * focusScale
                val focusTargetOffset = focusLayout?.let { logicalPos ->
                    Offset(
                        widthPx * 0.5f - (logicalPos.x * focusDisplayScale + baseOffsetX),
                        heightPx * 0.44f - (logicalPos.y * focusDisplayScale + baseOffsetY)
                    )
                }
                val appliedOffset = if (focusTargetOffset != null) {
                    Offset(
                        offset.x + (focusTargetOffset.x - offset.x) * focusProgress,
                        offset.y + (focusTargetOffset.y - offset.y) * focusProgress
                    )
                } else {
                    offset
                }

                // 1. Static Layers (Ocean Texture & Base Terrain)
                OceanBackgroundLayer(phase = oceanWavePhase)

                // 2. Transformation Layer (User Pan/Zoom)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = mapScale * focusScale
                            scaleY = mapScale * focusScale
                            translationX = appliedOffset.x
                            translationY = appliedOffset.y
                            transformOrigin = TransformOrigin.Center
                        }
                ) {
                    // Layer 3-5: Continents, Coastline, Terrain Landmarks
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        translate(baseOffsetX, baseOffsetY) {
                            scale(baseScale, pivot = Offset.Zero) {
                                drawPremiumContinents(landPaths)
                                drawTerrainFeatures()
                            }
                        }
                    }

                    // Layer 6: Travel Routes & Leader Lines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        translate(baseOffsetX, baseOffsetY) {
                            scale(baseScale, pivot = Offset.Zero) {
                                drawTravelRoutes(
                                    mapCoords = mapCoords,
                                    progressMap = progressMap,
                                    phase = routeDashPhase,
                                    focusedCountryId = activeFocusCountryId,
                                    compactMode = gameplayMode
                                )
                                
                                // Draw Leader Lines for offset markers
                                LevelRegistry.allCountries.forEach { country ->
                                    val layout = WorldMapGeometry.markerLayouts[country.levelId] ?: return@forEach
                                    if (layout.showLeaderLine && layout.visualOffset != Offset.Zero) {
                                        val anchor = mapCoords[country.levelId] ?: return@forEach
                                        val target = anchor + layout.visualOffset
                                        
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.4f),
                                            start = anchor,
                                            end = target,
                                            strokeWidth = 1f,
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Layer 8: Markers
                    LevelRegistry.allCountries.forEach { country ->
                        val anchorPos = mapCoords[country.levelId] ?: Offset.Zero
                        val layout = WorldMapGeometry.markerLayouts[country.levelId] ?: WorldMapGeometry.MarkerLayout()
                        val progress = progressMap[country.levelId] ?: com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress(country.levelId)
                        
                        // Final logical position = anchor + offset
                        val logicalPos = anchorPos + layout.visualOffset
                        
                        val screenX = logicalPos.x * baseScale + baseOffsetX
                        val screenY = logicalPos.y * baseScale + baseOffsetY

                        with(density) {
                            val isSelected = selectedCountryId == country.levelId
                            val markerAlpha = if (!gameplayMode || isSelected) 1f else 0.35f
                            CountryNodeComposable(
                                country = country,
                                progress = progress,
                                isSelected = isSelected,
                                onClick = { onCountryClick(country.levelId) },
                                onLongClick = { onCountryLongClick(country.levelId) },
                                compactMode = gameplayMode,
                                showLabel = !gameplayMode || isSelected,
                                deemphasized = gameplayMode && !isSelected,
                                labelOffset = layout.labelOffset,
                                modifier = Modifier.offset { 
                                    IntOffset(
                                        (screenX - 19.dp.toPx()).roundToInt(), // 19dp = half of 38dp node
                                        (screenY - 19.dp.toPx()).roundToInt()
                                    ) 
                                }.alpha(markerAlpha)
                            )
                        }
                    }

                    // Layer 7: Airplane
                    if (progressMap["italy"]?.isUnlocked == true) {
                        val gLog = mapCoords["germany"] ?: Offset.Zero
                        val iLog = mapCoords["italy"] ?: Offset.Zero
                        val gPx = Offset(gLog.x * baseScale + baseOffsetX, gLog.y * baseScale + baseOffsetY)
                        val iPx = Offset(iLog.x * baseScale + baseOffsetX, iLog.y * baseScale + baseOffsetY)
                        PlaneAnimationV2(start = gPx, end = iPx)
                    }
                }
            }

            AtmosphereLayer()
        }
    }
}

@Composable
private fun OceanBackgroundLayer(phase: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Deep to shallow ocean gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(PremiumColors.OceanShallow.copy(alpha = 0.75f), PremiumColors.OceanMid, PremiumColors.OceanDeep)
            )
        )

        // Ocean shimmer bands
        repeat(8) { i ->
            val y = ((i * size.height / 8f) + phase * size.height * 0.5f) % size.height
            drawRoundRect(
                color = Color.White.copy(alpha = 0.035f),
                topLeft = Offset(-size.width * 0.15f, y),
                size = Size(size.width * 1.3f, size.height * 0.03f),
                cornerRadius = CornerRadius(40f, 40f)
            )
        }

        // Soft whirlpool-like depth circles
        repeat(4) { i ->
            drawCircle(
                color = Color.White.copy(alpha = 0.025f),
                radius = (size.width * 0.18f * (i + 1)),
                center = Offset(size.width * (0.2f + i * 0.25f), size.height * (0.22f + i * 0.17f)),
                style = Stroke(width = 1.5f)
            )
        }
    }
}

private fun DrawScope.drawPremiumContinents(paths: List<Path>) {
    val landBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF3A9C68), Color(0xFF1E5E45))
    )
    val center = Offset(WorldMapGeometry.MAP_WIDTH / 2f, WorldMapGeometry.MAP_HEIGHT / 2f)

    scale(1.07f, pivot = center) {
        paths.forEach { path ->
            // Drop Shadow
            translate(3f, 4f) {
                drawPath(path, Color.Black.copy(alpha = 0.24f))
            }
            // Coastal Highlight (Outer)
            drawPath(path, Color(0xFF8CFCD8).copy(alpha = 0.32f), style = Stroke(width = 3f))
            // Base Land
            drawPath(path, landBrush)
            // Subtle inner contour
            drawPath(path, Color.White.copy(alpha = 0.05f), style = Stroke(width = 1f))
        }
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
            center = center,
            radius = WorldMapGeometry.MAP_WIDTH * 0.55f
        ),
        radius = WorldMapGeometry.MAP_WIDTH * 0.55f,
        center = center
    )
}

private fun DrawScope.drawTerrainFeatures() {
    // Forests
    WorldMapGeometry.forestZones.forEach { center ->
        drawCircle(
            color = PremiumColors.TerrainForest,
            radius = 15f,
            center = center
        )
        drawCircle(
            color = PremiumColors.TerrainForest.copy(alpha = 0.6f),
            radius = 10f,
            center = center + Offset(8f, -5f)
        )
    }
    
    // Mountains
    WorldMapGeometry.mountainRanges.forEach { center ->
        val mountainPath = Path().apply {
            moveTo(center.x, center.y - 12f)
            lineTo(center.x - 10f, center.y + 8f)
            lineTo(center.x + 10f, center.y + 8f)
            close()
        }
        drawPath(mountainPath, PremiumColors.TerrainMountain)
    }
    
    // Sahara Desert — subtle atmospheric tint only, Northern Africa band
    drawRect(
        color = PremiumColors.TerrainDesert.copy(alpha = 0.10f),
        topLeft = Offset(460f, 168f),
        size = Size(105f, 38f)
    )
}

private fun DrawScope.drawTravelRoutes(
    mapCoords: Map<String, Offset>,
    progressMap: Map<String, com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress>,
    phase: Float,
    focusedCountryId: String? = null,
    compactMode: Boolean = false
) {
    fun drawRoute(startId: String, endId: String) {
        val start = mapCoords[startId] ?: return
        val end = mapCoords[endId] ?: return
        val isUnlocked = progressMap[endId]?.isUnlocked == true
        val isFocused = focusedCountryId != null && (startId == focusedCountryId || endId == focusedCountryId)
        val focusedAlpha = if (compactMode && !isFocused) 0.3f else 1f
        val baseColor = if (isFocused) {
            PremiumColors.Gold.copy(alpha = 0.95f * focusedAlpha)
        } else if (isUnlocked) {
            PremiumColors.Gold.copy(alpha = 0.7f * focusedAlpha)
        } else {
            Color.White.copy(alpha = 0.18f * focusedAlpha)
        }
        val glowColor = if (isFocused) {
            PremiumColors.Gold.copy(alpha = 0.42f * focusedAlpha)
        } else if (isUnlocked) {
            PremiumColors.Gold.copy(alpha = 0.24f * focusedAlpha)
        } else {
            Color.White.copy(alpha = 0.08f * focusedAlpha)
        }
        
        drawLine(
            color = glowColor,
            start = start,
            end = end,
            strokeWidth = if (isFocused) 6f else 5f
        )
        drawLine(
            color = baseColor,
            start = start,
            end = end,
            strokeWidth = if (isFocused) 2.8f else 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase)
        )
    }
    
    drawRoute("germany", "italy")
    drawRoute("italy", "france")
    drawRoute("italy", "sudan")
    drawRoute("germany", "japan")
    drawRoute("france", "mexico")
}

@Composable
private fun PlaneAnimationV2(start: Offset, end: Offset) {
    val infiniteTransition = rememberInfiniteTransition(label = "plane")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planeProgress"
    )

    val mid = Offset((start.x + end.x) / 2, (start.y + end.y) / 2)
    val dx = end.x - start.x
    val dy = end.y - start.y
    val cp = Offset(mid.x - dy * 0.25f, mid.y + dx * 0.25f)

    val t = progress
    val x = (1 - t) * (1 - t) * start.x + 2 * (1 - t) * t * cp.x + t * t * end.x
    val y = (1 - t) * (1 - t) * start.y + 2 * (1 - t) * t * cp.y + t * t * end.y

    val tx = 2 * (1 - t) * (cp.x - start.x) + 2 * t * (end.x - cp.x)
    val ty = 2 * (1 - t) * (cp.y - start.y) + 2 * t * (end.y - cp.y)
    val angle = Math.toDegrees(atan2(ty, tx).toDouble()).toFloat()

    Box(
        modifier = Modifier
            .offset { IntOffset((x - 12.dp.toPx()).roundToInt(), (y - 12.dp.toPx()).roundToInt()) }
            .size(24.dp)
            .graphicsLayer { rotationZ = angle + 90f },
        contentAlignment = Alignment.Center
    ) {
        Text("✈️", fontSize = 16.sp)
    }
}

@Composable
private fun AtmosphereLayer() {
    // Vignette and subtle clouds
    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                    radius = 1600f
                )
            )
        )
        
        val infiniteTransition = rememberInfiniteTransition(label = "clouds")
        val cloudOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2000f,
            animationSpec = infiniteRepeatable(
                animation = tween(180000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "cloudOffset"
        )

        repeat(4) { i ->
            Box(
                modifier = Modifier
                    .size(300.dp, 100.dp)
                    .offset {
                        val currentX = ((cloudOffset + i * 500) % 2000) - 400
                        IntOffset(currentX.dp.toPx().roundToInt(), (100 + i * 120).dp.toPx().roundToInt())
                    }
                    .alpha(0.04f)
                    .background(Color.White, RoundedCornerShape(100.dp))
            )
        }

        // Subtle floating particles to lift scene quality without noise.
        repeat(14) { i ->
            val drift = ((cloudOffset * 0.08f + i * 120f) % 1700f) - 300f
            Box(
                modifier = Modifier
                    .size((2 + (i % 3)).dp)
                    .offset {
                        IntOffset(
                            drift.dp.toPx().roundToInt(),
                            (40 + (i * 38 % 560)).dp.toPx().roundToInt()
                        )
                    }
                    .alpha(0.12f)
                    .background(Color.White, CircleShape)
            )
        }
    }
}
