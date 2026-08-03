package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.ui.geometry.Offset
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
 * 5=Asia, 6=Asia-FE, 7=India, 8=Australia, 9=Greenland, 10=Japan, 11=NZ
 */
private val CONTINENT_FILLS = arrayOf(
    Color(0xFF4A8F50),  // N. America — temperate forest/plains mix
    Color(0xFF237840),  // S. America — dense tropical
    Color(0xFF5C9E52),  // Europe — lighter temperate green
    Color(0xFF5CA055),  // Scandinavia — boreal/conifer
    Color(0xFF8F8C2A),  // Africa — warm savanna/grassland
    Color(0xFF376040),  // Asia main — mixed forest
    Color(0xFF376040),  // Asia far east — mixed forest
    Color(0xFF4E7C38),  // India — subtropical
    Color(0xFFB8843A),  // Australia — arid red-ochre
    Color(0xFFD4E8F5),  // Greenland — glacial blue-white
    Color(0xFF376040),  // Japan
    Color(0xFF50906A)   // New Zealand — lush green
)

private val CONTINENT_COASTS = arrayOf(
    Color(0xFF28603A),  // N. America
    Color(0xFF14582C),  // S. America — dark jungle
    Color(0xFF347040),  // Europe
    Color(0xFF347040),  // Scandinavia
    Color(0xFF5E5C1A),  // Africa — ochre-olive coast
    Color(0xFF1A5228),  // Asia main — deep jungle coast
    Color(0xFF1A5228),  // Asia far east
    Color(0xFF285E2C),  // India
    Color(0xFF7A5828),  // Australia — burnt ochre coast
    Color(0xFFA0C4DC),  // Greenland — faint blue coast
    Color(0xFF1A5228),  // Japan
    Color(0xFF347040)   // New Zealand
)

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
    var first = true
    var anyVisible = false
    val n = coords.size / 2
    var i = 0
    while (i < n) {
        val pt = projectLatLon(coords[i * 2], coords[i * 2 + 1], rotY, rotX, cx, cy, r)
        if (pt != null) {
            anyVisible = true
            if (first) { path.moveTo(pt.x, pt.y); first = false }
            else path.lineTo(pt.x, pt.y)
        } else if (!first) {
            first = true
        }
        i++
    }
    path.close()
    return if (anyVisible) path else null
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
    for (i in 0 until n) {
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
fun DrawScope.drawAtmosphereGlow(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return

    // Layer 1: Wide diffuse blue haze
    val haloR = r * 1.50f
    if (haloR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f      to Color.Transparent,
                0.76f   to Color.Transparent,
                0.87f   to Color(0x203399EE),
                0.94f   to Color(0x3855BBFF),
                1f      to Color.Transparent,
                center = Offset(cx, cy), radius = haloR
            ),
            radius = haloR, center = Offset(cx, cy)
        )
    }

    // Layer 2: Bright inner horizon ring
    val innerR = r * 1.11f
    if (innerR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f    to Color.Transparent,
                0.87f to Color.Transparent,
                0.94f to Color(0x5577DDFF),
                1f    to Color.Transparent,
                center = Offset(cx, cy), radius = innerR
            ),
            radius = innerR, center = Offset(cx, cy)
        )
    }

    // Layer 3: Warm yellow tint on sunlit side (upper-left)
    val warmR = r * 0.88f
    if (warmR > 0f) {
        val wx = cx - r * 0.28f; val wy = cy - r * 0.30f
        drawCircle(
            brush = Brush.radialGradient(
                0f    to Color(0x14FFE080),
                0.55f to Color(0x08FFD060),
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
fun DrawScope.drawOceanSphere(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return

    // Base ocean — highlight offset creates natural top-left sun illumination
    val hlOffset = Offset(cx - r * 0.20f, cy - r * 0.26f)
    val gradR = r * 1.60f
    if (gradR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4AAAD8),   // Illuminated surface (moderate cyan-blue)
                    Color(0xFF1870AA),   // Mid-depth navy blue
                    Color(0xFF0A3D6E),   // Deep ocean
                    Color(0xFF051828)    // Dark abyss / night edge
                ),
                center = hlOffset, radius = gradR
            ),
            radius = r, center = Offset(cx, cy)
        )
    }

    // Subtle coastal tint — very light, only noticeable near illuminated shore
    val coastR = r * 0.60f
    if (coastR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x0C40BBDD),
                    Color(0x0F2896BE),
                    Color(0x00000000)
                ),
                center = Offset(cx - r * 0.10f, cy - r * 0.12f), radius = coastR
            ),
            radius = r, center = Offset(cx, cy)
        )
    }
}

/**
 * Two-layer sunlight: outer wide glow + inner specular hotspot (upper-left).
 */
fun DrawScope.drawSunlight(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val sx = cx - r * 0.30f
    val sy = cy - r * 0.34f

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
    val specR = r * 0.26f
    if (specR > 0f) {
        val specX = sx + r * 0.06f; val specY = sy + r * 0.04f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x55FFFFFF), Color(0x18FFFFFF), Color.Transparent),
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
fun DrawScope.drawNightSide(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val nx = cx + r * 0.32f
    val ny = cy + r * 0.30f

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
fun DrawScope.drawAtmosphereRim(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val c = Offset(cx, cy)
    // Outermost faint haze
    drawCircle(color = Color(0x1455AAFF), radius = r + 12f, center = c, style = Stroke(width = 24f))
    // Mid atmosphere band
    drawCircle(color = Color(0x2A6EC2FF), radius = r + 4f,  center = c, style = Stroke(width = 9f))
    // Sharp bright limb
    drawCircle(color = Color(0x4890DEFF), radius = r,       center = c, style = Stroke(width = 2.8f))
    // Warm highlight arc on sunlit side — approximately upper-left 120° arc
    // Approximated as a slightly offset lighter circle with a clipping mask effect
    drawCircle(color = Color(0x22FFDC80), radius = r + 1f,  center = Offset(cx - 2f, cy - 2f), style = Stroke(width = 2.8f))
}

/**
 * Continents with per-polygon biome colors: forest, savanna, desert, ice.
 * Paths are rebuilt each frame (polygons are small; no caching needed).
 */
fun DrawScope.drawContinents(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
) {
    if (r <= 0f) return
    GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
        val path = buildContinentPath(poly, rotY, rotX, cx, cy, r) ?: return@forEachIndexed
        val fill  = CONTINENT_FILLS.getOrElse(idx)  { Color(0xFF4D8E52) }
        val coast = CONTINENT_COASTS.getOrElse(idx) { Color(0xFF2A6A38) }
        drawPath(path, color = fill)
        drawPath(path, color = coast, style = Stroke(width = 1.4f, cap = StrokeCap.Round))
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

    data class IceCap(val latBase: Float, val irregular: Float, val coreShift: Float)
    val caps = listOf(
        IceCap(70f, +4f, +9f),   // Arctic — above 70°, core at +9°
        IceCap(-60f, -4f, -9f)   // Antarctic — below -60°, core at -9°
    )

    for (cap in caps) {
        val outerPath = Path(); var firstO = true
        val corePath  = Path(); var firstC = true

        for (deg in 0..360 step 8) {
            val lonF = deg.toFloat()
            // Slightly irregular outer boundary
            val wobble = sin(lonF * DEG2RAD * 2.3f) * 2f
            val latO = cap.latBase + wobble
            val latC = cap.latBase + cap.coreShift + wobble * 0.4f

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
    cloudRotY: Float
) {
    if (r <= 0f) return

    // (polygon coords, alpha as Float)
    val bands = listOf(
        floatArrayOf(18f,-58f, 22f,-18f, 20f, 22f, 17f, 22f, 17f,-18f, 17f,-58f) to 0.22f,
        floatArrayOf(37f, 52f, 42f, 92f, 40f,128f, 36f,128f, 36f, 92f, 36f, 52f) to 0.18f,
        floatArrayOf(-13f, 28f,-10f, 75f,-14f,118f,-18f,118f,-18f, 75f,-18f, 28f) to 0.16f,
        floatArrayOf(52f,-133f,57f,-96f, 55f,-62f, 50f,-62f, 50f,-96f, 50f,-133f) to 0.20f,
        floatArrayOf(6f, 78f,10f,107f, 7f,132f, 3f,132f, 3f,107f, 3f, 78f)       to 0.15f,
        floatArrayOf(33f,-38f,38f,  0f,35f, 22f,31f, 22f,31f,  0f,31f,-38f)       to 0.17f,
        floatArrayOf(-28f,-62f,-24f,-28f,-29f, 8f,-34f, 8f,-34f,-28f,-34f,-62f)   to 0.12f,
        floatArrayOf(63f, 18f,68f, 62f,64f, 98f,60f, 98f,60f, 62f,60f, 18f)       to 0.11f
    )

    for ((coords, alpha) in bands) {
        val path = buildContinentPath(coords, rotY + cloudRotY, rotX, cx, cy, r + 1.8f) ?: continue
        drawPath(path, color = Color(1f, 1f, 1f, alpha))
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

/** Euclidean distance between two offsets. */
fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x; val dy = y - other.y
    return sqrt(dx * dx + dy * dy)
}
