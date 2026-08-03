package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// ──────────────────────────────────────────────────────────────────────────────
// Country data
// ──────────────────────────────────────────────────────────────────────────────

private data class GlobeCountry(
    val id: String,
    val displayName: String,
    val flag: String,
    val latDeg: Float,
    val lonDeg: Float
)

private val GLOBE_COUNTRIES = listOf(
    GlobeCountry("germany", "Germany", "🇩🇪",  51.1f,  10.4f),
    GlobeCountry("france",  "France",  "🇫🇷",  46.2f,   2.2f),
    GlobeCountry("italy",   "Italy",   "🇮🇹",  41.9f,  12.6f),
    GlobeCountry("sudan",   "Sudan",   "🇸🇩",  15.6f,  32.5f),
    GlobeCountry("japan",   "Japan",   "🇯🇵",  36.2f, 138.3f),
    GlobeCountry("mexico",  "Mexico",  "🇲🇽",  23.6f, -102.6f)
)

// ──────────────────────────────────────────────────────────────────────────────
// Pre-computed star field (no per-frame allocation)
// ──────────────────────────────────────────────────────────────────────────────

private const val STAR_COUNT = 180

private val STAR_POSITIONS: FloatArray = FloatArray(STAR_COUNT * 2).also { arr ->
    var seed = 0x7F3A1C42L
    fun nextFloat(): Float {
        seed = (seed * 6364136223846793005L + 1442695040888963407L) and 0x7FFFFFFFFFFFFFFFL
        return (seed ushr 33).toFloat() / 0x3FFFFFFFL.toFloat()
    }
    for (i in arr.indices) arr[i] = nextFloat()
}

private val STAR_COLORS: Array<Color> = Array(STAR_COUNT) { i ->
    val b = if (i % 4 == 0) 0.9f else if (i % 3 == 0) 0.6f else 0.4f
    Color(b, b, b, b * 0.9f)
}

// ──────────────────────────────────────────────────────────────────────────────
// Main composable
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Experimental 3D Globe world screen.
 * Only shown when [com.mahmodhota.worldfood3dadventure.world.WorldFeatureFlags.ENABLE_EXPERIMENTAL_GLOBE] is true.
 */
@Composable
fun Globe3DScreen(
    onLevelSelected: (String, Int) -> Unit,
    onTabSelected: (String) -> Unit
) {
    val camera = remember { GlobeCameraState() }
    var cloudRotY by remember { mutableFloatStateOf(0f) }
    var selectedCountry by remember { mutableStateOf<GlobeCountry?>(null) }

    // Frame loop — inertia + cloud drift (~60 fps via 16ms delay)
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(16L)
            camera.tickInertia()
            cloudRotY += 0.024f  // 1.5°/s at 60fps
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF040810))) {

        // ── Globe canvas ─────────────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val sens = 0.25f
                        camera.velY += pan.x * sens
                        camera.velX -= pan.y * sens
                        camera.applyZoom(zoom)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { camera.resetToDefault() }
                    ) {
                        selectedCountry = null
                    }
                }
        ) {
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

        // ── Marker dots (Canvas overlay) ─────────────────────────────────
        GlobeMarkersOverlay(
            camera = camera,
            selectedId = selectedCountry?.id,
            onCountryTap = { c ->
                selectedCountry = if (selectedCountry?.id == c.id) null else c
                camera.focusOn(c.latDeg, c.lonDeg)
            }
        )

        // ── Country card ──────────────────────────────────────────────────
        selectedCountry?.let { country ->
            GlobeCountryCard(
                country = country,
                onLevelSelected = { lvl -> onLevelSelected(country.id, lvl) },
                onDismiss = { selectedCountry = null }
            )
        }

        // ── HUD + nav ─────────────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
            TopStatusBar(onSettingsClick = {})
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            BottomNavigationBar(currentTab = "world", onTabSelected = onTabSelected)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Markers overlay
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun BoxScope.GlobeMarkersOverlay(
    camera: GlobeCameraState,
    selectedId: String?,
    @Suppress("UNUSED_PARAMETER") onCountryTap: (GlobeCountry) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas

        val cx   = size.width  * 0.5f
        val cy   = size.height * 0.46f
        val r    = (minOf(size.width, size.height) * 0.38f * camera.zoom).coerceAtLeast(1f)
        val rotY = camera.rotationY
        val rotX = camera.rotationX

        for (country in GLOBE_COUNTRIES) {
            val pos = projectLatLon(country.latDeg, country.lonDeg, rotY, rotX, cx, cy, r)
                ?: continue

            val isSelected = country.id == selectedId
            val dotR = if (isSelected) 14f else 10f
            val ring = if (isSelected) Color(0xFFFFD700) else Color(0xCCFFFFFF)
            val fill = if (isSelected) Color(0xFFFFD700) else Color(0x99FFFFFF)

            if (isSelected) {
                drawCircle(color = Color(0x55FFD700), radius = dotR * 2.5f, center = pos)
            }
            drawCircle(color = fill, radius = dotR, center = pos)
            drawCircle(color = ring, radius = dotR, center = pos, style = Stroke(width = 2f))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Country card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun BoxScope.GlobeCountryCard(
    country: GlobeCountry,
    @Suppress("UNUSED_PARAMETER") onLevelSelected: (Int) -> Unit,
    @Suppress("UNUSED_PARAMETER") onDismiss: () -> Unit
) {
    val state = ProgressionManager.getCountryProgress(country.id)

    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 96.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xF0151D2E),
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "${country.flag}  ${country.displayName}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = if (state.isUnlocked) "⭐ ${state.totalStars}" else "🔒 Locked",
                color = if (state.isUnlocked) Color(0xFFFFD700) else Color(0xFF8899AA),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp)
            )
        }
    }
}
