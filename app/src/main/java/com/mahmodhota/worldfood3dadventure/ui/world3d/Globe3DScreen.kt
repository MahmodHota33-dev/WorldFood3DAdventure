package com.mahmodhota.worldfood3dadventure.ui.world3d

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// ──────────────────────────────────────────────────────────────────────────────
// Country metadata (immutable file-level constants — never reallocated)
// ──────────────────────────────────────────────────────────────────────────────

internal data class GlobeCountry(
    val id: String,
    val displayName: String,
    val flag: String,
    val isoCode: String,
    val latDeg: Float,
    val lonDeg: Float
)

internal val GLOBE_COUNTRIES = listOf(
    GlobeCountry("germany", "Germany", "🇩🇪", "DE",  51.1f,  10.4f),
    GlobeCountry("france",  "France",  "🇫🇷", "FR",  46.2f,   2.2f),
    GlobeCountry("italy",   "Italy",   "🇮🇹", "IT",  41.9f,  12.6f),
    GlobeCountry("sudan",   "Sudan",   "🇸🇩", "SD",  15.6f,  32.5f),
    GlobeCountry("japan",   "Japan",   "🇯🇵", "JP",  36.2f, 138.3f),
    GlobeCountry("mexico",  "Mexico",  "🇲🇽", "MX",  23.6f,-102.6f)
)

// ──────────────────────────────────────────────────────────────────────────────
// Marker position cache
// Written during Canvas draw, read during tap detection — both on main thread.
// Not Compose state: no recomposition triggered by cache writes.
// ──────────────────────────────────────────────────────────────────────────────

internal class MarkerPositionCache {
    private val _entries = ArrayList<Pair<GlobeCountry, Offset>>(6)
    val entries: List<Pair<GlobeCountry, Offset>> get() = _entries

    fun update(country: GlobeCountry, screenPos: Offset) {
        val idx = _entries.indexOfFirst { it.first.id == country.id }
        if (idx >= 0) _entries[idx] = country to screenPos
        else _entries.add(country to screenPos)
    }

    fun removeHidden(visibleIds: Set<String>) {
        _entries.removeAll { it.first.id !in visibleIds }
    }

    fun clear() = _entries.clear()

    /**
     * Return the nearest country whose projected position is within [hitRadius] pixels
     * of [tap], or null if nothing is close enough.
     * When two markers overlap, the closer one wins.
     */
    fun findNearest(tap: Offset, hitRadius: Float): GlobeCountry? {
        val r2 = hitRadius * hitRadius
        var bestDist2 = r2
        var best: GlobeCountry? = null
        for ((country, pos) in _entries) {
            val dx = pos.x - tap.x
            val dy = pos.y - tap.y
            val d2 = dx * dx + dy * dy
            if (d2 < bestDist2) {
                bestDist2 = d2
                best = country
            }
        }
        return best
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Pre-computed star field (no per-frame allocation)
// ──────────────────────────────────────────────────────────────────────────────

private const val STAR_COUNT = 180

private val STAR_POSITIONS: FloatArray = FloatArray(STAR_COUNT * 2).also { arr ->
    var seed = 0x7F3A1C42L
    fun next(): Float {
        seed = (seed * 6364136223846793005L + 1442695040888963407L) and 0x7FFFFFFFFFFFFFFFL
        return (seed ushr 33).toFloat() / 0x3FFFFFFFL.toFloat()
    }
    for (i in arr.indices) arr[i] = next()
}

private val STAR_COLORS: Array<Color> = Array(STAR_COUNT) { i ->
    val b = if (i % 4 == 0) 0.9f else if (i % 3 == 0) 0.6f else 0.4f
    Color(b, b, b, b * 0.9f)
}

// Tap hit-radius in dp
private const val HIT_RADIUS_DP = 44f

// ──────────────────────────────────────────────────────────────────────────────
// Main composable
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Experimental 3D Globe world screen — Phase 2.
 *
 * Only rendered when [com.mahmodhota.worldfood3dadventure.world.WorldFeatureFlags.ENABLE_EXPERIMENTAL_GLOBE]
 * is true. The production 2D World Map is unmodified and remains the default.
 */
@Composable
fun Globe3DScreen(
    onLevelSelected: (String, Int) -> Unit,
    onTabSelected: (String) -> Unit
) {
    val camera = remember { GlobeCameraState() }
    var cloudRotY by remember { mutableFloatStateOf(0f) }
    var selectedCountry by remember { mutableStateOf<GlobeCountry?>(null) }
    val markerCache = remember { MarkerPositionCache() }

    // Pre-allocate Paint objects for marker labels — never reallocated after first composition
    val codePaint = remember {
        Paint().apply {
            isAntiAlias = true
            textSize = 30f
            color = android.graphics.Color.WHITE
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            setShadowLayer(6f, 0f, 2f, android.graphics.Color.argb(180, 0, 0, 0))
        }
    }
    val lockPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textSize = 26f
            color = android.graphics.Color.argb(210, 170, 185, 220)
            textAlign = Paint.Align.CENTER
            setShadowLayer(4f, 0f, 1f, android.graphics.Color.argb(120, 0, 0, 0))
        }
    }
    val starPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textSize = 22f
            color = android.graphics.Color.rgb(255, 218, 0)
            textAlign = Paint.Align.CENTER
            setShadowLayer(4f, 0f, 1f, android.graphics.Color.argb(100, 80, 60, 0))
        }
    }

    // Single frame loop — inertia, fly-to, and cloud drift
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(16L)
            camera.tickInertia()
            camera.tickFlyTo()
            cloudRotY += 0.024f   // 1.5°/s at 60 fps
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF040810))) {

        // ── Globe rendering canvas ────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width <= 0f || size.height <= 0f) return@Canvas
            val cx   = size.width  * 0.5f
            val cy   = size.height * 0.46f
            val r    = (minOf(size.width, size.height) * 0.38f * camera.zoom).coerceAtLeast(1f)
            val rotY = camera.rotationY
            val rotX = camera.rotationX

            drawSpaceBackground(STAR_POSITIONS, STAR_COLORS)
            drawAtmosphereGlow(cx, cy, r)
            drawOceanSphere(cx, cy, r)
            drawContinents(rotY, rotX, cx, cy, r)
            drawIceCaps(rotY, rotX, cx, cy, r)
            drawCloudLayer(rotY, rotX, cx, cy, r, cloudRotY)
            drawSunlight(cx, cy, r)
            drawNightSide(cx, cy, r)
            drawAtmosphereRim(cx, cy, r)
        }

        // ── Marker rendering canvas (also writes to markerCache) ──────────
        val selId = selectedCountry?.id
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width <= 0f || size.height <= 0f) return@Canvas

            val cx   = size.width  * 0.5f
            val cy   = size.height * 0.46f
            val r    = (minOf(size.width, size.height) * 0.38f * camera.zoom).coerceAtLeast(1f)
            val rotY = camera.rotationY
            val rotX = camera.rotationX

            val dotNorm   = 12.dp.toPx()
            val dotSel    = 18.dp.toPx()
            val strokeW   = 2.dp.toPx()
            val labelGap  = 4.dp.toPx()
            val starGap   = 16.dp.toPx()
            val glowScale = 2.6f

            val visibleIds = HashSet<String>(6)

            for (country in GLOBE_COUNTRIES) {
                val pos = projectLatLon(country.latDeg, country.lonDeg, rotY, rotX, cx, cy, r)
                    ?: continue

                visibleIds.add(country.id)
                markerCache.update(country, pos)

                val isSelected  = country.id == selId
                val progress    = ProgressionManager.getCountryProgress(country.id)
                val dotR        = if (isSelected) dotSel else dotNorm

                // Glow ring for selected marker
                if (isSelected) {
                    val glowR = dotR * glowScale
                    if (glowR > 0f) {
                        drawCircle(color = Color(0x44FFD700), radius = glowR, center = pos)
                    }
                }

                // Marker fill + stroke
                val fillColor = when {
                    isSelected          -> Color(0xFFFFE040)
                    progress.isUnlocked -> Color(0xCCFFFFFF)
                    else                -> Color(0x77AABBCC)
                }
                val ringColor = if (isSelected) Color(0xFFFFD700) else Color(0xAAFFFFFF)

                drawCircle(color = fillColor, radius = dotR, center = pos)
                drawCircle(color = ringColor, radius = dotR, center = pos,
                    style = Stroke(width = strokeW))

                // Label above marker: ISO code (unlocked) or lock emoji (locked)
                val labelText = if (progress.isUnlocked) country.isoCode else "🔒"
                val labelPaint = if (progress.isUnlocked) codePaint else lockPaint
                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    pos.x,
                    pos.y - dotR - labelGap,
                    labelPaint
                )

                // Star count below marker for countries with any progress
                if (progress.isUnlocked && progress.totalStars > 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "★${progress.totalStars}",
                        pos.x,
                        pos.y + dotR + starGap,
                        starPaint
                    )
                }
            }

            // Evict markers that went behind the globe this frame
            markerCache.removeHidden(visibleIds)
        }

        // ── Gesture overlay (transparent — handles all touch input) ────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Any drag cancels the fly-to so the user is in control
                        camera.cancelFlyTo()
                        camera.velY += pan.x * 0.25f
                        camera.velX -= pan.y * 0.25f
                        camera.applyZoom(zoom)
                    }
                }
                .pointerInput(Unit) {
                    val hitR = HIT_RADIUS_DP * density
                    detectTapGestures(
                        onDoubleTap = {
                            camera.resetToDefault()
                            selectedCountry = null
                        }
                    ) { tapPos ->
                        val hit = markerCache.findNearest(tapPos, hitR)
                        if (hit != null) {
                            // Toggle: tapping the already-selected country deselects it
                            selectedCountry = if (selectedCountry?.id == hit.id) null else hit
                            selectedCountry?.let { c -> camera.startFlyTo(c.latDeg, c.lonDeg) }
                        } else {
                            selectedCountry = null
                        }
                    }
                }
        )

        // ── Country info card ─────────────────────────────────────────────
        selectedCountry?.let { country ->
            GlobeCountryCard(
                country       = country,
                onPlayLevel   = { lvl -> onLevelSelected(country.id, lvl) },
                onDismiss     = { selectedCountry = null }
            )
        }

        // ── Top HUD ───────────────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
            TopStatusBar(onSettingsClick = {})
        }

        // ── Bottom navigation ──────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            BottomNavigationBar(currentTab = "world", onTabSelected = onTabSelected)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Country information card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun BoxScope.GlobeCountryCard(
    country: GlobeCountry,
    onPlayLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val progress  = ProgressionManager.getCountryProgress(country.id)
    val spec      = remember(country.id) { CountryProgressionChain.getSpec(country.id) }
    val metadata  = remember(country.id) { LevelRegistry.getCountry(country.id)?.metadata }

    val totalLevels     = spec?.totalLevels ?: maxOf(progress.levels.size, 1)
    val completedLevels = progress.levels.count { it.isCompleted }
    val maxStars        = totalLevels * 3
    val starsNeeded     = spec?.requiredStarsToUnlock ?: 0

    // First incomplete level (1-based); fall back to level 1 if all done
    val nextLevel = if (progress.isUnlocked) {
        progress.levels.indexOfFirst { !it.isCompleted }.let { if (it >= 0) it + 1 else 1 }
    } else 1

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xF0101825),
        tonalElevation = 12.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${country.flag}  ${country.displayName}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss, contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)) {
                    Text("✕", color = Color(0xFF7788AA), fontSize = 18.sp)
                }
            }

            // ── Travel description ─────────────────────────────────────────
            val desc = metadata?.travelDescription?.takeIf { it.isNotBlank() }
            if (desc != null) {
                Text(
                    text = desc,
                    color = Color(0xFFAABBCC),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            } else {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }

            if (progress.isUnlocked) {
                // ── Progress stats ────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    Column {
                        Text("⭐ Stars",  color = Color(0xFF7788AA), fontSize = 11.sp)
                        Text(
                            "${progress.totalStars} / $maxStars",
                            color = Color(0xFFFFD700),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column {
                        Text("✓ Levels", color = Color(0xFF7788AA), fontSize = 11.sp)
                        Text(
                            "$completedLevels / $totalLevels",
                            color = Color(0xFF88DDAA),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // ── Play button ───────────────────────────────────────────
                Button(
                    onClick = { onPlayLevel(nextLevel) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A4A99)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (completedLevels == 0) "▶  Start Adventure"
                               else "▶  Continue · Level $nextLevel",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {
                // ── Locked state ──────────────────────────────────────────
                Text(
                    text = "🔒  Requires $starsNeeded ⭐ to unlock",
                    color = Color(0xFF7788AA),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF222A38)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("🔒  Locked", color = Color(0xFF445566), fontSize = 15.sp)
                }
            }
        }
    }
}
