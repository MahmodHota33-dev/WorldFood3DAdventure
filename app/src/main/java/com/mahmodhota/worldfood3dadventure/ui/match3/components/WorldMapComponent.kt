package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
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

// ─── Static brush/paint constants — allocated once, never re-allocated per frame ────────────────

/** Land fill: natural forest green, north-to-south tonal shift. */
private val LandGradientBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF3A9C68), Color(0xFF1E5E45))
)

/** Ocean background: deep polar blue to rich mid-ocean to shallow coastal. */
private val OceanGradientBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF1A4A74),
        0.40f to Color(0xFF0E2C4A),
        1.00f to Color(0xFF041326)
    )
)

/** Atmospheric globe glow drawn over the land layer (logical 1000×500 coordinates). */
private val LandAtmosphereGlow = Brush.radialGradient(
    colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent),
    center = Offset(WorldMapGeometry.MAP_WIDTH / 2f, WorldMapGeometry.MAP_HEIGHT / 2f),
    radius = WorldMapGeometry.MAP_WIDTH * 0.55f
)

/** Edge vignette — static, never recomposed. */
private val VignetteBrush = Brush.radialGradient(
    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.30f)),
    radius = 1600f
)

/**
 * Cloud strip definitions: [xPhaseOffset, yDp, widthDp, heightDp].
 * Y positions are chosen to land in the Arctic strip (y ≤ 60 dp) or
 * the Southern Ocean band (y ≥ 310 dp), safely away from all country markers
 * which sit between ~143–265 dp on a 576×1280 device at displayScaleMultiplier=3.0.
 */
private val CloudDefs = arrayOf(
    floatArrayOf(   0f,   4f, 260f, 40f),
    floatArrayOf( 860f,  26f, 195f, 30f),
    floatArrayOf( 400f, 318f, 235f, 36f),
    floatArrayOf(1120f, 350f, 180f, 28f),
)

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
        // Base ocean — static gradient, no per-frame allocation
        drawRect(brush = OceanGradientBrush)

        // Latitude tonal depth variation — 3 static bands (no animation)
        // Polar caps: slightly cooler/darker
        drawRect(
            color = Color(0xFF001845).copy(alpha = 0.10f),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height * 0.10f)
        )
        drawRect(
            color = Color(0xFF001845).copy(alpha = 0.10f),
            topLeft = Offset(0f, size.height * 0.88f),
            size = Size(size.width, size.height * 0.12f)
        )
        // Equatorial band: very slightly warmer blue
        drawRect(
            color = Color(0xFF1565C0).copy(alpha = 0.06f),
            topLeft = Offset(0f, size.height * 0.38f),
            size = Size(size.width, size.height * 0.22f)
        )

        // Single gentle shimmer ribbon — replaces the 8-stripe pattern
        val shimmerY = ((phase * 0.55f) % 1.08f) * size.height - size.height * 0.04f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.016f),
            topLeft = Offset(-size.width * 0.08f, shimmerY),
            size = Size(size.width * 1.16f, size.height * 0.022f),
            cornerRadius = CornerRadius(size.width, size.width)
        )
    }
}

private fun DrawScope.drawPremiumContinents(paths: List<Path>) {
    val center = Offset(WorldMapGeometry.MAP_WIDTH / 2f, WorldMapGeometry.MAP_HEIGHT / 2f)

    scale(1.07f, pivot = center) {
        paths.forEach { path ->
            // Drop shadow (subtle)
            translate(2.5f, 3.5f) {
                drawPath(path, Color.Black.copy(alpha = 0.20f))
            }
            // Coastal shoreline highlight — softer teal, reduced alpha
            drawPath(path, Color(0xFF5DD4A4).copy(alpha = 0.18f), style = Stroke(width = 2.5f))
            // Base land fill (static brush — no per-draw allocation)
            drawPath(path, LandGradientBrush)
            // Interior terrain texture whisper
            drawPath(path, Color.White.copy(alpha = 0.04f), style = Stroke(width = 1f))
        }
    }

    // Atmospheric globe glow (static brush — no per-draw allocation)
    drawCircle(
        brush = LandAtmosphereGlow,
        radius = WorldMapGeometry.MAP_WIDTH * 0.55f,
        center = center
    )
}

private fun DrawScope.drawTerrainFeatures() {
    // Sahara Desert — subtle atmospheric tint only, Northern Africa band
    drawRect(
        color = PremiumColors.TerrainDesert.copy(alpha = 0.10f),
        topLeft = Offset(460f, 168f),
        size = Size(105f, 38f)
    )
    // Forest dots and mountain triangles removed: at displayScaleMultiplier=3.0 they
    // appear disconnected from their continents in the default Europe/Africa view.
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
    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
        VignetteOverlay()
        CloudLayer()
    }
}

/**
 * Static edge vignette — no state, no animation, recomposes at most once.
 */
@Composable
private fun VignetteOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VignetteBrush)
    )
}

/**
 * Slow-drifting clouds — isolated so only this subtree recomposes on each animation tick.
 * Clouds are clamped to two safe horizontal bands (Arctic top / Southern Ocean bottom)
 * so they never overlap country markers.
 */
@Composable
private fun CloudLayer() {
    val cloudTransition = rememberInfiniteTransition(label = "clouds")
    val cloudDrift by cloudTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1800f,
        animationSpec = infiniteRepeatable(
            animation = tween(200_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudDrift"
    )

    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
        CloudDefs.forEach { def ->
            val xDp = ((cloudDrift + def[0]) % 2100f) - 350f
            Box(
                modifier = Modifier
                    .width(def[2].dp)
                    .height(def[3].dp)
                    .offset(x = xDp.dp, y = def[1].dp)
                    .alpha(0.055f)
                    .background(Color.White, RoundedCornerShape(100.dp))
            )
        }
    }
}
