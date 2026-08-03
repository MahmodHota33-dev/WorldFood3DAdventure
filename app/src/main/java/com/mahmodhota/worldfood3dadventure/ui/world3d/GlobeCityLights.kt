package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

// ──────────────────────────────────────────────────────────────────────────────
// Sun direction (camera-space) — matches drawSunlight / drawNightSide offsets.
// Sun is at upper-left in screen: x_screen = cx - r*0.30, y_screen = cy - r*0.34
// In 3D camera space: screen_x = cx + x2*r  → x2 = -0.30
//                     screen_y = cy - y2*r  → y2 = +0.34
// z points toward viewer, sun is on the viewer's side.
// ──────────────────────────────────────────────────────────────────────────────
internal const val GLOBE_SUN_X = -0.30f
internal const val GLOBE_SUN_Y = +0.34f
internal const val GLOBE_SUN_Z = +0.88f
// Magnitude ≈ sqrt(0.09 + 0.1156 + 0.7744) = sqrt(0.98) ≈ 0.99 — treat as unit

private const val CL_DEG2RAD = (PI / 180.0).toFloat()

// Minimum night intensity before a city light becomes visible (0..1).
// Points nearer the terminator than this threshold are hidden.
internal const val CITY_LIGHT_THRESHOLD = 0.08f

// ──────────────────────────────────────────────────────────────────────────────
// City light data
// ──────────────────────────────────────────────────────────────────────────────

/**
 * A single procedural city-light point.
 * These are original game-art positions, NOT real satellite night-light data.
 * [brightness] 0..1 affects dot radius and alpha.
 */
internal data class CityLight(val lat: Float, val lon: Float, val brightness: Float)

/**
 * Immutable list of stylized city-light clusters.
 * Declared at file level and never reallocated during rendering.
 * Covers major populated regions for game visual effect only.
 */
internal val CITY_LIGHTS: List<CityLight> = listOf(
    // ── Western Europe ───────────────────────────────────────────────────────
    CityLight( 51.5f,  -0.1f, 0.88f),  // London
    CityLight( 52.5f,  13.4f, 0.88f),  // Berlin / central Europe
    CityLight( 48.9f,   2.3f, 0.84f),  // Paris
    CityLight( 50.9f,   5.0f, 0.80f),  // Benelux / Rhine corridor
    CityLight( 53.4f,   8.0f, 0.68f),  // Hamburg / NW Germany
    CityLight( 45.5f,   9.2f, 0.82f),  // Milan / Po Valley
    CityLight( 41.9f,  12.5f, 0.65f),  // Rome
    CityLight( 40.4f,  -3.7f, 0.62f),  // Madrid
    CityLight( 41.4f,   2.2f, 0.60f),  // Barcelona
    CityLight( 59.3f,  18.1f, 0.52f),  // Stockholm
    // ── Eastern Europe / Russia ─────────────────────────────────────────────
    CityLight( 50.1f,  20.0f, 0.60f),  // Kraków / Poland
    CityLight( 47.5f,  19.0f, 0.62f),  // Budapest
    CityLight( 55.8f,  37.6f, 0.80f),  // Moscow
    CityLight( 59.9f,  30.3f, 0.60f),  // St. Petersburg
    // ── Middle East ─────────────────────────────────────────────────────────
    CityLight( 30.0f,  31.2f, 0.74f),  // Cairo / Nile Delta
    CityLight( 33.3f,  44.4f, 0.58f),  // Baghdad
    CityLight( 35.7f,  51.4f, 0.58f),  // Tehran
    CityLight( 24.7f,  46.7f, 0.65f),  // Riyadh
    CityLight( 25.2f,  55.3f, 0.68f),  // Dubai / UAE
    CityLight( 32.1f,  34.8f, 0.60f),  // Tel Aviv
    // ── India ───────────────────────────────────────────────────────────────
    CityLight( 28.7f,  77.1f, 0.82f),  // Delhi
    CityLight( 22.6f,  88.4f, 0.72f),  // Kolkata
    CityLight( 19.1f,  72.9f, 0.80f),  // Mumbai
    CityLight( 13.0f,  80.3f, 0.65f),  // Chennai
    CityLight( 12.9f,  77.6f, 0.62f),  // Bangalore
    CityLight( 24.0f,  82.0f, 0.48f),  // Ganges plain scatter
    // ── China ───────────────────────────────────────────────────────────────
    CityLight( 39.9f, 116.4f, 0.84f),  // Beijing
    CityLight( 31.2f, 121.5f, 0.92f),  // Shanghai
    CityLight( 23.1f, 113.3f, 0.86f),  // Guangzhou / Pearl River Delta
    CityLight( 30.6f, 104.1f, 0.68f),  // Chengdu
    CityLight( 32.0f, 118.8f, 0.70f),  // Nanjing area
    CityLight( 36.1f, 120.4f, 0.62f),  // Qingdao
    // ── Japan ───────────────────────────────────────────────────────────────
    CityLight( 35.7f, 139.7f, 0.95f),  // Tokyo Megalopolis
    CityLight( 34.7f, 135.5f, 0.90f),  // Osaka / Kansai
    CityLight( 33.6f, 130.4f, 0.70f),  // Fukuoka
    CityLight( 43.1f, 141.4f, 0.58f),  // Sapporo
    // ── Korea / SE Asia ─────────────────────────────────────────────────────
    CityLight( 37.6f, 127.0f, 0.84f),  // Seoul
    CityLight( 13.7f, 100.5f, 0.70f),  // Bangkok
    CityLight(  1.3f, 103.8f, 0.72f),  // Singapore
    CityLight( 14.6f, 121.1f, 0.60f),  // Manila
    CityLight( -6.2f, 106.8f, 0.65f),  // Jakarta
    // ── Australia ───────────────────────────────────────────────────────────
    CityLight(-33.9f, 151.2f, 0.65f),  // Sydney
    CityLight(-37.8f, 145.0f, 0.62f),  // Melbourne
    // ── Eastern North America ───────────────────────────────────────────────
    CityLight( 40.7f, -74.0f, 0.94f),  // New York area
    CityLight( 42.4f, -71.1f, 0.80f),  // Boston
    CityLight( 39.0f, -77.0f, 0.86f),  // Washington DC
    CityLight( 41.8f, -87.6f, 0.86f),  // Chicago
    CityLight( 43.7f, -79.4f, 0.70f),  // Toronto
    CityLight( 45.5f, -73.6f, 0.66f),  // Montréal
    CityLight( 33.7f, -84.4f, 0.68f),  // Atlanta
    CityLight( 25.8f, -80.2f, 0.64f),  // Miami
    CityLight( 29.8f, -95.4f, 0.78f),  // Houston
    CityLight( 32.8f, -96.8f, 0.72f),  // Dallas / Fort Worth
    // ── Western North America ───────────────────────────────────────────────
    CityLight( 34.1f,-118.2f, 0.86f),  // Los Angeles
    CityLight( 37.8f,-122.4f, 0.78f),  // San Francisco Bay
    CityLight( 47.6f,-122.3f, 0.70f),  // Seattle
    CityLight( 49.3f,-123.1f, 0.64f),  // Vancouver
    CityLight( 33.4f,-112.1f, 0.68f),  // Phoenix
    // ── Mexico ──────────────────────────────────────────────────────────────
    CityLight( 19.4f, -99.1f, 0.82f),  // Mexico City
    CityLight( 20.7f,-103.4f, 0.60f),  // Guadalajara
    CityLight( 25.7f,-100.3f, 0.60f),  // Monterrey
    // ── South America ───────────────────────────────────────────────────────
    CityLight(-23.5f, -46.6f, 0.84f),  // São Paulo
    CityLight(-22.9f, -43.2f, 0.76f),  // Rio de Janeiro
    CityLight(-34.6f, -58.4f, 0.70f),  // Buenos Aires
    CityLight(-12.0f, -77.0f, 0.54f),  // Lima
    CityLight(  4.7f, -74.1f, 0.50f),  // Bogotá
    // ── Africa ──────────────────────────────────────────────────────────────
    CityLight(-26.2f,  28.0f, 0.62f),  // Johannesburg
    CityLight(-33.9f,  18.4f, 0.50f),  // Cape Town
    CityLight(  6.4f,   3.4f, 0.58f),  // Lagos
    CityLight( 15.6f,  32.5f, 0.46f),  // Khartoum
    CityLight(  9.0f,  38.7f, 0.44f),  // Addis Ababa
)

/**
 * Pre-computed unit-sphere xyz for every city light.
 * Computed once at class load; never reallocated during rendering.
 * [0]=x, [1]=y, [2]=z  (same convention as [latLonToXyz])
 */
internal val CITY_LIGHT_XYZ: Array<FloatArray> = Array(CITY_LIGHTS.size) { i ->
    val lat = CITY_LIGHTS[i].lat * CL_DEG2RAD
    val lon = CITY_LIGHTS[i].lon * CL_DEG2RAD
    val cosLat = cos(lat)
    floatArrayOf(cosLat * sin(lon), sin(lat).toFloat(), cosLat * cos(lon))
}

// ──────────────────────────────────────────────────────────────────────────────
// Night intensity math
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Compute night intensity for a camera-space point (x2, y2, z2).
 *
 * Result: 0.0 = full day side, 1.0 = deepest night side.
 * Returns 0 safely for NaN or Infinity inputs.
 *
 * Sun direction (camera space): (GLOBE_SUN_X, GLOBE_SUN_Y, GLOBE_SUN_Z) — upper-left of screen.
 * Night = dot product with sun is negative → nightIntensity = clamp(-dot, 0, 1).
 */
fun nightIntensity(x2: Float, y2: Float, z2: Float): Float {
    if (!x2.isFinite() || !y2.isFinite() || !z2.isFinite()) return 0f
    val dot = x2 * GLOBE_SUN_X + y2 * GLOBE_SUN_Y + z2 * GLOBE_SUN_Z
    return (-dot).coerceIn(0f, 1f)
}

// ──────────────────────────────────────────────────────────────────────────────
// City-light rendering
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Draw stylized procedural city lights on the night side of the globe.
 *
 * Rendering rules:
 * - Back-side points (z2 ≤ −0.05) are skipped before any draw call.
 * - Day-side points (nightIntensity < [CITY_LIGHT_THRESHOLD]) are skipped.
 * - Alpha fades smoothly from the terminator into deep night.
 * - Dot radius scales with per-city brightness (1.4 – 3.5 px).
 * - Warm amber/gold hue only; no per-city animation or blur.
 * - No per-frame list, brush, or gradient allocation.
 * - Must be called AFTER [drawNightSide] so lights appear above the shadow overlay.
 * - Markers are drawn in a separate Canvas layer above the globe Canvas, so they
 *   are always on top of city lights without any z-order intervention here.
 */
fun DrawScope.drawCityLights(
    rotY: Float, rotX: Float,
    cx: Float,   cy: Float,
    r: Float
) {
    if (r <= 0f) return

    val ryRad = rotY * CL_DEG2RAD
    val cosRy = cos(ryRad); val sinRy = sin(ryRad)
    val rxRad = rotX * CL_DEG2RAD
    val cosRx = cos(rxRad); val sinRx = sin(rxRad)

    for (i in CITY_LIGHT_XYZ.indices) {
        val xyz = CITY_LIGHT_XYZ[i]
        val px = xyz[0]; val py = xyz[1]; val pz = xyz[2]

        // Rotate Y axis (longitude pan) — same math as projectPoint
        val x1 = px * cosRy + pz * sinRy
        val z1 = -px * sinRy + pz * cosRy

        // Rotate X axis (latitude tilt)
        val x2 = x1
        val y2 = py * cosRx - z1 * sinRx
        val z2 = py * sinRx + z1 * cosRx

        // Skip back-facing (invisible) points
        if (!z2.isFinite() || z2 <= -0.05f) continue

        // Night intensity — skip day side
        val ni = nightIntensity(x2, y2, z2)
        if (ni < CITY_LIGHT_THRESHOLD) continue

        // Project to screen space
        val sx = cx + x2 * r
        val sy = cy - y2 * r
        if (!sx.isFinite() || !sy.isFinite()) continue

        // Fade in smoothly from the terminator; deepen toward full night
        val fadeFactor = ((ni - CITY_LIGHT_THRESHOLD) / (1f - CITY_LIGHT_THRESHOLD))
            .coerceIn(0f, 1f)

        val light = CITY_LIGHTS[i]
        // Alpha: max ~0.78 for brightest city at deepest night
        val alpha = (fadeFactor * light.brightness * 0.78f).coerceIn(0f, 0.82f)

        // Dot radius: 1.4 px base + up to 2.1 px for the brightest cities
        val dotR = 1.4f + light.brightness * 2.1f

        // Warm amber/gold — slight green variation by brightness adds variety
        val warmG = 0.78f + light.brightness * 0.14f  // 0.78..0.92
        val warmB = 0.22f + light.brightness * 0.16f  // 0.22..0.38
        drawCircle(
            color  = Color(1f, warmG, warmB, alpha),
            radius = dotR,
            center = Offset(sx, sy)
        )
    }
}
