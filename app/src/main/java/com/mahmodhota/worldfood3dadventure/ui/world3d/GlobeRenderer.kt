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
    Color(0xFF4D8E52),  // N. America — temperate forest
    Color(0xFF2A8440),  // S. America — tropical
    Color(0xFF6AA85E),  // Europe — lighter temperate
    Color(0xFF6CB062),  // Scandinavia — boreal
    Color(0xFF9A983A),  // Africa — savanna/bush (olive)
    Color(0xFF3E8848),  // Asia main
    Color(0xFF3E8848),  // Asia far east
    Color(0xFF5A8C48),  // India — subtropical
    Color(0xFFC09040),  // Australia — arid ochre
    Color(0xFFD8ECF5),  // Greenland — ice white
    Color(0xFF3E8848),  // Japan
    Color(0xFF5A9858)   // New Zealand
)

private val CONTINENT_COASTS = arrayOf(
    Color(0xFF2A6A38),  // N. America
    Color(0xFF18602E),  // S. America
    Color(0xFF3A7840),  // Europe
    Color(0xFF3A7840),  // Scandinavia
    Color(0xFF6A6820),  // Africa — olive coast
    Color(0xFF1C5E30),  // Asia main
    Color(0xFF1C5E30),  // Asia far east
    Color(0xFF2C6A36),  // India
    Color(0xFF8C6E28),  // Australia — ochre coast
    Color(0xFFAAC8DC),  // Greenland — ice coast
    Color(0xFF1C5E30),  // Japan
    Color(0xFF3A7840)   // New Zealand
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
 * Multi-layer atmospheric halo: wide outer haze + tight bright horizon ring.
 */
fun DrawScope.drawAtmosphereGlow(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return

    // Layer 1: Wide diffuse haze
    val haloR = r * 1.46f
    if (haloR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f      to Color.Transparent,
                0.78f   to Color.Transparent,
                0.88f   to Color(0x1C3399DD),
                0.95f   to Color(0x3344AAFE),
                1f      to Color.Transparent,
                center = Offset(cx, cy), radius = haloR
            ),
            radius = haloR, center = Offset(cx, cy)
        )
    }

    // Layer 2: Bright inner horizon glow
    val innerR = r * 1.13f
    if (innerR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                0f    to Color.Transparent,
                0.86f to Color.Transparent,
                0.94f to Color(0x4A66CCFF),
                1f    to Color.Transparent,
                center = Offset(cx, cy), radius = innerR
            ),
            radius = innerR, center = Offset(cx, cy)
        )
    }
}

/**
 * Premium ocean sphere: deep-blue base + tropical highlight + subtle coastal tint.
 */
fun DrawScope.drawOceanSphere(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return

    // Base ocean — off-centre highlight creates natural top-left lighting
    val hlOffset = Offset(cx - r * 0.18f, cy - r * 0.22f)
    val gradR = r * 1.55f
    if (gradR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF5DCCE8),   // Tropical bright
                    Color(0xFF2282BB),   // Mid-depth blue
                    Color(0xFF0E4C78),   // Deep ocean
                    Color(0xFF071C30)    // Abyss (night edge)
                ),
                center = hlOffset, radius = gradR
            ),
            radius = r, center = Offset(cx, cy)
        )
    }

    // Coastal warm-blue tint overlay (tropical shallows)
    val coastR = r * 0.75f
    if (coastR > 0f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x0830CCEE), Color(0x1240AACC), Color(0x00000000)),
                center = Offset(cx + r * 0.08f, cy - r * 0.05f), radius = coastR
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
 * Night-side hemisphere shadow (lower-right) with soft terminator.
 */
fun DrawScope.drawNightSide(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val nx = cx + r * 0.30f
    val ny = cy + r * 0.28f
    val nr = r * 0.95f
    if (nr <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x00000000),
                Color(0x15000818),
                Color(0x40000C22),
                Color(0x60000814)
            ),
            center = Offset(nx, ny), radius = nr
        ),
        radius = nr, center = Offset(nx, ny)
    )
}

/**
 * Three-layer atmospheric rim: faint outer haze → mid ring → sharp inner edge.
 */
fun DrawScope.drawAtmosphereRim(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val c = Offset(cx, cy)
    // Outermost faint haze ring
    drawCircle(color = Color(0x1855AAFF), radius = r + 10f, center = c, style = Stroke(width = 20f))
    // Mid atmosphere band
    drawCircle(color = Color(0x2C77BBFF), radius = r + 3f,  center = c, style = Stroke(width = 7f))
    // Sharp limb line
    drawCircle(color = Color(0x4088DDFF), radius = r,       center = c, style = Stroke(width = 2.5f))
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
