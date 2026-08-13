package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val DEG2RAD = (PI / 180.0).toFloat()

// ──────────────────────────────────────────────────────────────────────────────
// Continent colors (file-level constants — never reallocated per frame)
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Fill color per polygon index, matching [GlobeContinentData.allPolygons] order:
 * 0=N.America, 1=S.America, 2=Europe, 3=Scandinavia, 4=Africa,
 * 5=Asia, 6=Asia-FE, 7=India, 8=Arabian, 9=Italy,
 * 10=Australia, 11=Greenland, 12=Japan, 13=NZ
 */
private val CONTINENT_FILLS = arrayOf(
    Color(0xFF4D9958),  // N. America — rich temperate forests
    Color(0xFF2C854D),  // S. America — lush rainforest
    Color(0xFF73AF62),  // Europe — lighter plains
    Color(0xFF6EAE62),  // Scandinavia — cool boreal green
    Color(0xFFA99747),  // Africa — warm savanna / dry grassland
    Color(0xFF4A7A4E),  // Asia main — mixed forest/plains
    Color(0xFF4E7D53),  // Asia far east
    Color(0xFF6D8C4B),  // India — subtropical plains
    Color(0xFFC4A055),  // Arabian Peninsula — warm desert sand
    Color(0xFF7AB362),  // Italy — Mediterranean green
    Color(0xFFC08E4A),  // Australia — warm desert earth
    Color(0xFFDCEBFA),  // Greenland — icy blue-white
    Color(0xFF4B7E4F),  // Japan — forested green
    Color(0xFF5C9A75)   // New Zealand — bright lush green
)

private val CONTINENT_COASTS = arrayOf(
    Color(0xFF2F6A45),  // N. America
    Color(0xFF1E693A),  // S. America — deep jungle coast
    Color(0xFF3F7A4B),  // Europe
    Color(0xFF3D7A4A),  // Scandinavia
    Color(0xFF6D6730),  // Africa — warm olive coast
    Color(0xFF2B6240),  // Asia main
    Color(0xFF2D6443),  // Asia far east
    Color(0xFF3D6C3C),  // India
    Color(0xFF8A7230),  // Arabian Peninsula — dry desert coast
    Color(0xFF4A8054),  // Italy — Mediterranean coast
    Color(0xFF8B6230),  // Australia — desert coastline
    Color(0xFFA5C9E0),  // Greenland — cool ice coast
    Color(0xFF2D6641),  // Japan
    Color(0xFF417954)   // New Zealand
)

private val CONTINENT_MOUNTAIN_TINT = arrayOf(
    Color(0x142A2F38), Color(0x10272F38), Color(0x162E333C), Color(0x182E343E),
    Color(0x1536342B), Color(0x1E2A2F38), Color(0x1C2A2F38), Color(0x1630322F),
    Color(0x143A3020), // Arabian Peninsula
    Color(0x182F353C), // Italy
    Color(0x123B2F26), Color(0x10F0F6FF), Color(0x1C2B313A), Color(0x162B323B)
)

private data class CloudBandSpec(
    val coords: FloatArray,
    val baseAlpha: Float,
    val tint: Color,
    val centroidXyz: FloatArray
)

private val CLOUD_BANDS: List<CloudBandSpec> = listOf(
    floatArrayOf(18f,-58f, 22f,-18f, 20f, 22f, 17f, 22f, 17f,-18f, 17f,-58f) to Pair(0.21f, Color(0xFFF4FBFF)),
    floatArrayOf(37f, 52f, 42f, 92f, 40f,128f, 36f,128f, 36f, 92f, 36f, 52f) to Pair(0.18f, Color(0xFFEAF7FF)),
    floatArrayOf(-13f, 28f,-10f, 75f,-14f,118f,-18f,118f,-18f, 75f,-18f, 28f) to Pair(0.16f, Color(0xFFE6F4FF)),
    floatArrayOf(52f,-133f,57f,-96f, 55f,-62f, 50f,-62f, 50f,-96f, 50f,-133f) to Pair(0.19f, Color(0xFFF0F9FF)),
    floatArrayOf(6f, 78f,10f,107f, 7f,132f, 3f,132f, 3f,107f, 3f, 78f)       to Pair(0.14f, Color(0xFFEAF6FF)),
    floatArrayOf(33f,-38f,38f,  0f,35f, 22f,31f, 22f,31f,  0f,31f,-38f)       to Pair(0.17f, Color(0xFFF4FAFF)),
    floatArrayOf(-28f,-62f,-24f,-28f,-29f, 8f,-34f, 8f,-34f,-28f,-34f,-62f)   to Pair(0.12f, Color(0xFFE5F3FF)),
    floatArrayOf(63f, 18f,68f, 62f,64f, 98f,60f, 98f,60f, 62f,60f, 18f)       to Pair(0.11f, Color(0xFFE7F5FF))
).map { (coords, style) ->
    var latSum = 0f
    var lonSum = 0f
    val n = coords.size / 2
    for (i in 0 until n) {
        latSum += coords[i * 2]
        lonSum += coords[i * 2 + 1]
    }
    val centroid = latLonToXyz(latSum / n, lonSum / n)
    CloudBandSpec(coords, style.first, style.second, centroid)
}

// Reused mutable paths to avoid per-frame Path allocations.
private val CONTINENT_PATH_CACHE = Array(GlobeContinentData.allPolygons.size) { Path() }
private val CLOUD_PATH_CACHE_A = Array(CLOUD_BANDS.size) { Path() }
private val CLOUD_PATH_CACHE_B = Array(CLOUD_BANDS.size) { Path() }
private val ICE_PATH_OUTER = Array(2) { Path() }
private val ICE_PATH_CORE = Array(2) { Path() }
private val ICE_CAP_BASE_LAT = floatArrayOf(70f, -60f)
private val ICE_CAP_CORE_SHIFT = floatArrayOf(9f, -9f)

// ──────────────────────────────────────────────────────────────────────────────
// Projection math — UNCHANGED from Phase 2
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Convert geographic coordinates to a unit sphere vector.
 * Returns [x, y, z] where +z faces the viewer.
 */
fun latLonToXyz(latDeg: Float, lonDeg: Float): FloatArray {
    val lat = latDeg * DEG2RAD
    val lon = lonDeg * DEG2RAD
    val cosLat = cos(lat)
    return floatArrayOf(
        cosLat * sin(lon),
        sin(lat),
        cosLat * cos(lon)
    )
}

/**
 * Rotate a unit-sphere point by camera angles and project to canvas.
 * Returns null when the point is on the far (invisible) side of the globe.
 */
fun projectPoint(
    p: FloatArray,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? {
    val ry = rotY * DEG2RAD
    val cosRy = cos(ry); val sinRy = sin(ry)
    val x1 = p[0] * cosRy + p[2] * sinRy
    val y1 = p[1]
    val z1 = -p[0] * sinRy + p[2] * cosRy

    val rx = rotX * DEG2RAD
    val cosRx = cos(rx); val sinRx = sin(rx)
    val x2 = x1
    val y2 = y1 * cosRx - z1 * sinRx
    val z2 = y1 * sinRx + z1 * cosRx

    if (z2 <= -0.05f) return null
    return Offset(cx + x2 * r, cy - y2 * r)
}

/** Project directly from latitude/longitude. */
fun projectLatLon(
    latDeg: Float, lonDeg: Float,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? = projectPoint(latLonToXyz(latDeg, lonDeg), rotY, rotX, cx, cy, r)

/** Build a Compose Path for one continent polygon. */
fun buildContinentPath(
    coords: FloatArray,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Path? {
    if (coords.size < 4) return null
    val path = Path()
    return if (buildContinentPathInto(coords, rotY, rotX, cx, cy, r, path)) path else null
}

fun buildContinentPathInto(
    coords: FloatArray,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float,
    outPath: Path
): Boolean {
    if (coords.size < 4) return false
    outPath.reset()
    var first = true
    var anyVisible = false
    val n = coords.size / 2
    var i = 0
    while (i < n) {
        val pt = projectLatLon(coords[i * 2], coords[i * 2 + 1], rotY, rotX, cx, cy, r)
        if (pt != null) {
            anyVisible = true
            if (first) { outPath.moveTo(pt.x, pt.y); first = false }
            else outPath.lineTo(pt.x, pt.y)
        } else if (!first) {
            first = true
        }
        i++
    }
    outPath.close()
    return anyVisible
}

// ──────────────────────────────────────────────────────────────────────────────
// Drawing functions — Phase 3 visual polish
// All guard size > 0 to prevent gradient-with-zero-radius crash.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Deep space background: dark radial vignette, subtle galaxy band,
 * two nebula blobs, and star field with brightness-derived size variation.
 */
fun DrawScope.drawSpaceBackground(stars: FloatArray, starColors: Array<Color>) {
    val w = size.width; val h = size.height

    // ── Base gradient ──────────────────────────────────────────────────────
    val bgR = maxOf(w, h) * 0.85f
    if (bgR > 0f) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF0D1E35), Color(0xFF060E1C), Color(0xFF030910)),
                center = Offset(w * 0.50f, h * 0.44f),
                radius = bgR
            )
        )
    }

    // ── Galaxy band — extremely faint diagonal smear ────────────────────────
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x07884480),
                Color(0x0CAA44AA),
                Color(0x07884480),
                Color.Transparent
            ),
            start = Offset(0f, h * 0.12f),
            end   = Offset(w,  h * 0.70f)
        )
    )

    // ── Nebula patches — visible only on OLED or in dark rooms ─────────────
    val nb1R = w * 0.22f
    if (nb1R > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x07502870), Color(0x04301040), Color.Transparent),
                center = Offset(w * 0.12f, h * 0.20f), radius = nb1R
            ),
            radius = nb1R, center = Offset(w * 0.12f, h * 0.20f)
        )
    }
    val nb2R = w * 0.17f
    if (nb2R > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x06203858), Color(0x03101C2C), Color.Transparent),
                center = Offset(w * 0.86f, h * 0.76f), radius = nb2R
            ),
            radius = nb2R, center = Offset(w * 0.86f, h * 0.76f)
        )
    }

    // ── Stars — radius varies with brightness ──────────────────────────────
    val n = minOf(stars.size / 2, starColors.size)
    for (i in 0 until n step 2) {
        val sx = stars[i * 2]     * w
        val sy = stars[i * 2 + 1] * h
        val brightness = starColors[i].red
        // Brightest stars (b≈0.9) get r≈1.9; faint ones (b≈0.4) get r≈0.8
        val starR = 0.55f + brightness * 1.5f
        drawCircle(color = starColors[i], radius = starR, center = Offset(sx, sy))
    }
}

/**
 * Multi-layer atmospheric halo: wide outer haze + tight bright horizon ring
 * + subtle warm tint on the sun-facing (upper-left) side.
 */
fun DrawScope.drawAtmosphereGlow(
    cx: Float,
    cy: Float,
    r: Float,
    lighting: GlobeLightingState
) {
    if (r <= 0f) return

    // Layer 1: Wide diffuse blue haze
    val haloR = r * 1.58f
    if (haloR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f      to Color.Transparent,
                0.72f   to Color.Transparent,
                0.86f   to Color(0x1C2E96EE),
                0.93f   to Color(0x3A4BAFFF),
                0.98f   to Color(0x1F66C4FF),
                1f      to Color.Transparent,
                center = Offset(cx, cy), radius = haloR
            ),
            radius = haloR, center = Offset(cx, cy)
        )
    }

    // Layer 2: Bright inner horizon ring
    val innerR = r * 1.13f
    if (innerR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f    to Color.Transparent,
                0.86f to Color.Transparent,
                0.93f to Color(0x5887D9FF),
                0.98f to Color(0x3398E4FF),
                1f    to Color.Transparent,
                center = Offset(cx, cy), radius = innerR
            ),
            radius = innerR, center = Offset(cx, cy)
        )
    }

    // Layer 3: Warm yellow tint on sunlit side
    val warmR = r * 0.96f
    if (warmR > 0f) {
        val wx = cx + r * lighting.sunX * 0.98f
        val wy = cy - r * lighting.sunY * 0.98f
        if (!wx.isFinite() || !wy.isFinite()) return
        drawCircle(
            brush = Brush.radialGradient(
                0f    to Color(0x16FFE38F),
                0.52f to Color(0x0AFFD975),
                1f    to Color.Transparent,
                center = Offset(wx, wy), radius = warmR
            ),
            radius = r, center = Offset(cx, cy)
        )
    }
}

/**
 * Premium ocean sphere: deep navy-blue base with subtle lighting gradient.
 * Avoids overly cyan tropical tones in favour of a more refined deep-sea palette.
 */
fun DrawScope.drawOceanSphere(
    cx: Float,
    cy: Float,
    r: Float,
    lighting: GlobeLightingState,
    oceanPhase: Float
) {
    if (r <= 0f) return

    val driftX = sin(oceanPhase) * r * 0.04f
    val driftY = cos(oceanPhase * 0.83f) * r * 0.03f

    // Base ocean — highlight offset tracks sun direction with subtle drift.
    val hlOffset = Offset(
        cx + r * lighting.sunX * 0.75f + driftX,
        cy - r * lighting.sunY * 0.77f + driftY
    )
    if (!hlOffset.x.isFinite() || !hlOffset.y.isFinite()) return
    val gradR = r * 1.85f
    if (gradR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF59B0E3),   // specular ocean highlight
                    0.22f to Color(0xFF2279B5),   // sunlit shallow
                    0.48f to Color(0xFF0D4C82),   // primary ocean blue
                    0.75f to Color(0xFF062A45),   // deep basin
                    0.92f to Color(0xFF031826),   // abyss
                    1.00f to Color(0xFF02101A)    // outer edge shadow
                ),
                center = hlOffset, radius = gradR
            ),
            radius = r, center = Offset(cx, cy)
        )
    }

    // Subtle depth variation to avoid flat plastic look.
    val depthR = r * 1.05f
    if (depthR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x0D0A3560),
                    Color(0x1A041D36),
                    Color.Transparent
                ),
                center = Offset(cx + r * 0.26f, cy + r * 0.28f),
                radius = depthR
            ),
            radius = r,
            center = Offset(cx, cy)
        )
    }

    // Soft elongated reflective streak. Drawn before continents so land stays readable.
    val reflW = r * 0.78f
    val reflH = r * 0.24f
    val reflR = reflW * 0.54f
    val reflCenter = Offset(
        cx + r * lighting.sunX * 0.48f + driftX * 0.45f,
        cy - r * lighting.sunY * 0.50f + driftY * 0.45f
    )
    if (reflW > 0f && reflH > 0f && reflR > 0f && reflCenter.x.isFinite() && reflCenter.y.isFinite()) {
        drawOval(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to Color(0x46F8FFFF),
                    0.42f to Color(0x20B9F8FF),
                    0.82f to Color(0x0A65CDE8),
                    1f to Color.Transparent
                ),
                center = reflCenter,
                radius = reflR
            ),
            topLeft = Offset(reflCenter.x - reflW * 0.5f, reflCenter.y - reflH * 0.5f),
            size = Size(reflW, reflH)
        )
    }

    // Additional very low-amplitude movement pattern to suggest slow water light motion.
    val flowR = r * 0.40f
    if (flowR > 0f) {
        val flowCenter = Offset(
            cx + sin(oceanPhase * 0.41f) * r * 0.22f,
            cy + cos(oceanPhase * 0.37f) * r * 0.16f
        )
        if (flowCenter.x.isFinite() && flowCenter.y.isFinite()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x0D63E5EF),
                        Color(0x0754C9DD),
                        Color.Transparent
                    ),
                    center = flowCenter,
                    radius = flowR
                ),
                radius = r,
                center = Offset(cx, cy)
            )
        }
    }
}

/**
 * Two-layer sunlight: outer wide glow + inner specular hotspot (upper-left).
 */
fun DrawScope.drawSunlight(cx: Float, cy: Float, r: Float, lighting: GlobeLightingState) {
    if (r <= 0f) return
    val sx = cx + r * lighting.sunX
    val sy = cy - r * lighting.sunY
    if (!sx.isFinite() || !sy.isFinite()) return

    // Outer wide glow
    val outerR = r * 0.68f
    if (outerR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x3AFFFFFF), Color(0x10FFFFFF), Color.Transparent),
                center = Offset(sx, sy), radius = outerR
            ),
            radius = outerR, center = Offset(sx, sy)
        )
    }

    // Inner specular highlight
    val specR = r * 0.28f
    if (specR > 0f) {
        val specX = sx + r * 0.05f; val specY = sy + r * 0.03f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x6AFFFFFF), Color(0x22BCE8FF), Color.Transparent),
                center = Offset(specX, specY), radius = specR
            ),
            radius = specR, center = Offset(specX, specY)
        )
    }
}

/**
 * Night-side hemisphere shadow with a clear, soft day/night terminator.
 *
 * Two-pass strategy:
 * 1. Primary shadow — large radial covering the night hemisphere; deepest at
 *    the anti-sun centre, fading to transparent near the terminator line.
 * 2. Terminator accent — a narrow annular ring at the transition boundary
 *    adds a subtle blue tint that sharpens the perceived terminator edge
 *    without a harsh step.
 *
 * Continent and marker visibility on the night side is preserved because the
 * maximum opacity is moderate (~0x82), leaving enough contrast in the dark
 * hemisphere for coloured shapes to still read.
 *
 * Sun is fixed at upper-left: shadow centre is lower-right (+x, +y in screen).
 */
fun DrawScope.drawNightSide(cx: Float, cy: Float, r: Float, lighting: GlobeLightingState) {
    if (r <= 0f) return
    val nx = cx - r * lighting.sunX * 1.04f
    val ny = cy + r * lighting.sunY * 1.04f
    if (!nx.isFinite() || !ny.isFinite()) return

    // Pass 1 — primary hemisphere shadow
    // Non-linear colour stops push the dark region further from the terminator,
    // creating a cleaner bright-to-dark boundary.
    val nr = r * 1.05f
    if (nr > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color(0x00000000),
                    0.28f to Color(0x16000B1E),
                    0.50f to Color(0x38000C22),
                    0.68f to Color(0x58000A1C),
                    0.82f to Color(0x72000816),
                    0.93f to Color(0x82000612),
                    1.00f to Color(0x00000000)
                ),
                center = Offset(nx, ny), radius = nr
            ),
            radius = nr, center = Offset(nx, ny)
        )
    }

    // Pass 2 — narrow terminator-zone accent (subtle blue tint ring)
    val tr = nr * 0.82f
    if (tr > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color.Transparent,
                    0.68f to Color.Transparent,
                    0.80f to Color(0x1A000840),
                    0.90f to Color(0x0E000530),
                    1.00f to Color.Transparent
                ),
                center = Offset(nx, ny), radius = tr
            ),
            radius = tr, center = Offset(nx, ny)
        )
    }
}

/**
 * Four-layer atmospheric rim: outer haze → mid band → sharp limb → warm lit edge.
 */
fun DrawScope.drawAtmosphereRim(cx: Float, cy: Float, r: Float, lighting: GlobeLightingState) {
    if (r <= 0f) return
    val c = Offset(cx, cy)
    // Outermost faint haze
    drawCircle(
        brush = Brush.radialGradient(
            0.90f to Color.Transparent,
            0.95f to Color(0x123A8EEA),
            1.00f to Color.Transparent,
            center = c, radius = r + 30f
        ),
        radius = r + 30f, center = c
    )
    // Mid atmosphere band
    drawCircle(
        color = Color(0x285FAEFF),
        radius = r + 6f,
        center = c,
        style = Stroke(width = 8f)
    )
    // Sharp bright limb
    drawCircle(
        color = Color(0x6A9EE7FF),
        radius = r,
        center = c,
        style = Stroke(width = 2.5f)
    )
    // Warm highlight arc on sunlit side
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x35FFE39B), Color.Transparent),
            center = Offset(cx + lighting.sunX * r * 0.95f, cy - lighting.sunY * r * 0.95f),
            radius = r * 0.45f
        ),
        radius = r + 2f,
        center = c,
        style = Stroke(width = 3.5f)
    )
}

/**
 * Continents with per-polygon biome colors: forest, savanna, desert, ice.
 * Paths are rebuilt each frame (polygons are small; no caching needed).
 */
fun DrawScope.drawContinents(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float,
    lighting: GlobeLightingState
) {
    if (r <= 0f) return
    val sunDx = lighting.sunX * r * 1.8f
    val sunDy = -lighting.sunY * r * 1.8f
    val sunBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.00f to Color(0x24FFF6DD),
            0.30f to Color(0x12FFF4D1),
            0.70f to Color.Transparent,
            1.00f to Color.Transparent
        ),
        start = Offset(cx + sunDx, cy + sunDy),
        end = Offset(cx - sunDx * 0.72f, cy - sunDy * 0.72f)
    )
    GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
        val path = CONTINENT_PATH_CACHE[idx]
        if (!buildContinentPathInto(poly, rotY, rotX, cx, cy, r, path)) return@forEachIndexed
        val fill  = CONTINENT_FILLS.getOrElse(idx)  { Color(0xFF4D8E52) }
        val coast = CONTINENT_COASTS.getOrElse(idx) { Color(0xFF2A6A38) }
        val mountain = CONTINENT_MOUNTAIN_TINT.getOrElse(idx) { Color(0x142B313A) }
        drawPath(path, color = fill)
        drawPath(path, brush = sunBrush)
        // Mountain/highland shadow overlay — darkens high-terrain regions slightly
        drawPath(path, color = mountain)
        drawPath(path, color = coast, style = Stroke(width = 1.2f, cap = StrokeCap.Round))
    }
}

/**
 * Polar ice caps with smoother outlines (8° step) and slight blue-white gradient.
 * Two passes: outer blue-tinted ice, inner bright white core.
 */
fun DrawScope.drawIceCaps(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
) {
    if (r <= 0f) return

    for (capIdx in 0..1) {
        val latBase = ICE_CAP_BASE_LAT[capIdx]
        val coreShift = ICE_CAP_CORE_SHIFT[capIdx]
        val outerPath = ICE_PATH_OUTER[capIdx]
        val corePath = ICE_PATH_CORE[capIdx]
        outerPath.reset()
        corePath.reset()
        var firstO = true
        var firstC = true

        for (deg in 0..360 step 8) {
            val lonF = deg.toFloat()
            // Slightly irregular outer boundary
            val wobble = sin(lonF * DEG2RAD * 2.3f) * 2f
            val latO = latBase + wobble
            val latC = latBase + coreShift + wobble * 0.4f

            val ptO = projectLatLon(latO, lonF, rotY, rotX, cx, cy, r)
            val ptC = projectLatLon(latC, lonF, rotY, rotX, cx, cy, r)

            if (ptO != null) {
                if (firstO) { outerPath.moveTo(ptO.x, ptO.y); firstO = false }
                else outerPath.lineTo(ptO.x, ptO.y)
            }
            if (ptC != null) {
                if (firstC) { corePath.moveTo(ptC.x, ptC.y); firstC = false }
                else corePath.lineTo(ptC.x, ptC.y)
            }
        }
        outerPath.close(); corePath.close()

        if (!firstO) drawPath(outerPath, color = Color(0xCCCCE8F8))  // Blue-white outer
        if (!firstC) drawPath(corePath,  color = Color(0xECEEF8FF))  // Bright inner core
    }
}

/**
 * Cloud layer: 8 irregular bands on a slightly larger transparent sphere,
 * rotating independently via [cloudRotY] offset.
 */
fun DrawScope.drawCloudLayer(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float,
    cloudRotY: Float,
    lighting: GlobeLightingState
) {
    if (r <= 0f) return

    val rx = rotX * DEG2RAD
    val cosRx = cos(rx); val sinRx = sin(rx)

    for (i in CLOUD_BANDS.indices) {
        val band = CLOUD_BANDS[i]
        val p = band.centroidXyz

        // Layer A (nearer cloud deck)
        val ryA = (rotY + cloudRotY) * DEG2RAD
        val cosRyA = cos(ryA); val sinRyA = sin(ryA)
        val x1A = p[0] * cosRyA + p[2] * sinRyA
        val z1A = -p[0] * sinRyA + p[2] * cosRyA
        val y2A = p[1] * cosRx - z1A * sinRx
        val z2A = p[1] * sinRx + z1A * cosRx
        val cloudNightA = nightIntensity(x1A, y2A, z2A, lighting)
        val pathA = CLOUD_PATH_CACHE_A[i]
        if (buildContinentPathInto(band.coords, rotY + cloudRotY, rotX, cx, cy, r + 1.9f, pathA)) {
            val litA = 1f - cloudNightA
            val alphaA = (band.baseAlpha * (0.58f + litA * 0.56f)).coerceIn(0.05f, 0.26f)
            val tintA = Color(
                red = band.tint.red,
                green = band.tint.green,
                blue = band.tint.blue,
                alpha = alphaA
            )
            drawPath(pathA, color = tintA)
        }

        // Layer B intentionally removed for lower draw-call budget on low-end GPUs.
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Helpers — unchanged
// ──────────────────────────────────────────────────────────────────────────────

/** Project country lat/lon to screen. Alias of [projectLatLon]. */
fun projectCountry(
    latDeg: Float, lonDeg: Float,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? = projectLatLon(latDeg, lonDeg, rotY, rotX, cx, cy, r)

/**
 * Get the rotated Z-coordinate (depth) of a geographic point.
 * +z is towards the viewer.
 */
fun getZDepth(
    latDeg: Float, lonDeg: Float,
    rotY: Float, rotX: Float
): Float {
    val p = latLonToXyz(latDeg, lonDeg)
    
    val ry = rotY * DEG2RAD
    val cosRy = cos(ry); val sinRy = sin(ry)
    // Rotate Y
    val z1 = -p[0] * sinRy + p[2] * cosRy
    val y1 = p[1]

    val rx = rotX * DEG2RAD
    val cosRx = cos(rx); val sinRx = sin(rx)
    // Rotate X
    return y1 * sinRx + z1 * cosRx
}
