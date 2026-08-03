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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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
import kotlin.math.PI
import kotlin.math.sin

// ──────────────────────────────────────────────────────────────────────────────
// Country metadata (immutable file-level constants — never reallocated)
// ──────────────────────────────────────────────────────────────────────────────

private val TWO_PI = (PI * 2).toFloat()

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

private const val STAR_COUNT = 240

private val STAR_POSITIONS: FloatArray = FloatArray(STAR_COUNT * 2).also { arr ->
    var seed = 0x7F3A1C42L
    fun next(): Float {
        seed = (seed * 6364136223846793005L + 1442695040888963407L) and 0x7FFFFFFFFFFFFFFFL
        return (seed ushr 33).toFloat() / 0x3FFFFFFFL.toFloat()
    }
    for (i in arr.indices) arr[i] = next()
}

private val STAR_COLORS: Array<Color> = Array(STAR_COUNT) { i ->
    // Mix of warm and cool star tones for natural variety
    val b = when {
        i % 7 == 0 -> 0.95f  // rare bright
        i % 4 == 0 -> 0.75f  // medium-bright
        i % 3 == 0 -> 0.55f  // medium
        else       -> 0.38f  // faint
    }
    // Slight warm/cool tint on a small subset of stars
    when {
        i % 11 == 0 -> Color(b, b * 0.92f, b * 0.80f, b * 0.95f)  // warm yellow
        i % 13 == 0 -> Color(b * 0.85f, b * 0.92f, b, b * 0.95f)  // cool blue
        else        -> Color(b, b, b, b * 0.90f)
    }
}

// Tap hit-radius in dp
private const val HIT_RADIUS_DP = 44f

// ──────────────────────────────────────────────────────────────────────────────
// Main composable
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Production 3D Globe world screen.
 *
 * Shows the stylised spinning planet with country markers, country selection,
 * and the existing progression/level-selection flow.
 */
@Composable
fun Globe3DScreen(
    onLevelSelected: (String, Int) -> Unit,
    onTabSelected: (String) -> Unit
) {
    val camera = remember { GlobeCameraState() }
    var cloudRotY  by remember { mutableFloatStateOf(0f) }
    var pulsePhase by remember { mutableFloatStateOf(0f) }
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

    // Single frame loop — inertia, fly-to, cloud drift and marker pulse
    // Frame rate: 60fps when camera is active; ~20fps when fully idle.
    LaunchedEffect(Unit) {
        while (isActive) {
            val motionActive = camera.isFlyingTo ||
                kotlin.math.abs(camera.velY) > 0.05f ||
                kotlin.math.abs(camera.velX) > 0.05f
            delay(if (motionActive) 16L else 48L)

            camera.tickInertia()
            camera.tickFlyTo()
            // Adjust step to keep real-time speed regardless of frame delay
            val step = if (motionActive) 1f else 3f
            cloudRotY  = (cloudRotY  + 0.024f * step) % 360f
            pulsePhase = (pulsePhase + 0.022f * step) % TWO_PI
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
            drawCityLights(rotY, rotX, cx, cy, r)
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

            // Pin dimensions (all in px)
            val pinNorm   = 10.dp.toPx()    // circle radius normal
            val pinSel    = 15.dp.toPx()    // circle radius selected
            val ptrNorm   = 7.dp.toPx()     // pointer length normal
            val ptrSel    = 10.dp.toPx()    // pointer length selected
            val strokeW   = 1.5.dp.toPx()
            val starGap   = 12.dp.toPx()

            val visibleIds = HashSet<String>(6)

            for (country in GLOBE_COUNTRIES) {
                // tipPos = geographic coordinate = bottom of pin pointer
                val tipPos = projectLatLon(country.latDeg, country.lonDeg, rotY, rotX, cx, cy, r)
                    ?: continue

                val isSelected  = country.id == selId
                val progress    = ProgressionManager.getCountryProgress(country.id)
                val pinR   = if (isSelected) pinSel  else pinNorm
                val ptrLen = if (isSelected) ptrSel  else ptrNorm

                // Circle center = above the geographic tip
                val circCenter = Offset(tipPos.x, tipPos.y - ptrLen - pinR)

                visibleIds.add(country.id)
                markerCache.update(country, circCenter)   // hit-test targets visible circle body

                // ── Pulse glow (unlocked, non-selected) ──────────────────────────
                if (progress.isUnlocked && !isSelected) {
                    val pulse = 0.13f + 0.18f * (0.5f + 0.5f * sin(pulsePhase))
                    val glowR  = pinR * 2.5f
                    if (glowR > 0f) drawCircle(Color(1f, 0.84f, 0f, pulse), radius = glowR, center = circCenter)
                }

                // ── Selected glow ────────────────────────────────────────────────
                if (isSelected) {
                    if (pinR * 3.5f > 0f) drawCircle(Color(0x44FFD700), radius = pinR * 3.5f, center = circCenter)
                    if (pinR * 2.0f > 0f) drawCircle(Color(0x66FFD700), radius = pinR * 2.0f, center = circCenter)
                }

                // ── Pin fill and stroke colors ────────────────────────────────────
                val pinFill = when {
                    isSelected          -> Color(0xFFFFE640)   // bright gold
                    progress.isUnlocked -> Color(0xFFEEF6FF)   // clean white-blue
                    else                -> Color(0xFF3C5470)   // muted slate-blue
                }
                val pinStroke = when {
                    isSelected          -> Color(0xFFFFD000)
                    progress.isUnlocked -> Color(0xFF7AB8F5)
                    else                -> Color(0xFF2A3C54)
                }

                // ── Pointer triangle ─────────────────────────────────────────────
                val halfBase = pinR * 0.54f
                val ptrBase  = circCenter.y + pinR * 0.70f
                if (ptrBase < tipPos.y) {  // safety: only draw if there's room
                    val ptrPath = Path().apply {
                        moveTo(circCenter.x - halfBase, ptrBase)
                        lineTo(circCenter.x,             tipPos.y)
                        lineTo(circCenter.x + halfBase,  ptrBase)
                        close()
                    }
                    drawPath(ptrPath, color = pinFill)
                    drawPath(ptrPath, color = pinStroke,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round))
                }

                // ── Circle body ───────────────────────────────────────────────────
                drawCircle(color = pinFill,   radius = pinR, center = circCenter)
                drawCircle(color = pinStroke, radius = pinR, center = circCenter,
                    style = Stroke(width = strokeW))

                // ── Label inside circle ───────────────────────────────────────────
                val labelText  = if (progress.isUnlocked) country.isoCode else "🔒"
                val labelPaint = if (progress.isUnlocked) codePaint else lockPaint
                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    circCenter.x,
                    circCenter.y + labelPaint.textSize * 0.36f,
                    labelPaint
                )

                // ── Star count below pin tip ──────────────────────────────────────
                if (progress.isUnlocked && progress.totalStars > 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "★${progress.totalStars}",
                        tipPos.x,
                        tipPos.y + starGap,
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
            .padding(horizontal = 12.dp)
            .padding(bottom = 96.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF2080F1E),
        tonalElevation = 16.dp
    ) {
        Column {
            // ── Header gradient banner ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0E1F40), Color(0xFF0A1830))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = country.flag,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = country.displayName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!progress.isUnlocked) {
                            Text(
                                text = "🔒  Locked destination",
                                color = Color(0xFF6688AA),
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "✈  Available for exploration",
                                color = Color(0xFF4AADCC),
                                fontSize = 12.sp
                            )
                        }
                    }
                    TextButton(
                        onClick = onDismiss,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text("✕", color = Color(0xFF556677), fontSize = 18.sp)
                    }
                }
            }

            // ── Divider line ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x1AFFFFFF))
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {

                // ── Travel description ─────────────────────────────────────
                val desc = metadata?.travelDescription?.takeIf { it.isNotBlank() }
                if (desc != null) {
                    Text(
                        text = desc,
                        color = Color(0xFF8EA8C0),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (progress.isUnlocked) {
                    // ── Progress stats ─────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Stars stat box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF101C2E),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text("⭐  Stars",  color = Color(0xFF5A7090), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "${progress.totalStars} / $maxStars",
                                    color = Color(0xFFFFD700),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // Levels stat box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF101C2E),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text("✓  Levels", color = Color(0xFF5A7090), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "$completedLevels / $totalLevels",
                                    color = Color(0xFF55D494),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ── Play button ────────────────────────────────────────
                    Button(
                        onClick = { onPlayLevel(nextLevel) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1856B8)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (completedLevels == 0) "▶  Start Adventure"
                                   else "▶  Continue · Level $nextLevel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {
                    // ── Locked state ───────────────────────────────────────
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF101824),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔒", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text("Destination locked", color = Color(0xFF6688AA), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Collect $starsNeeded ⭐ to unlock", color = Color(0xFF445566), fontSize = 12.sp)
                            }
                        }
                    }
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF1A2030)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("🔒  Locked", color = Color(0xFF3A4C64), fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
