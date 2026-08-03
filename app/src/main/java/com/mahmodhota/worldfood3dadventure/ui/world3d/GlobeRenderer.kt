package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ──────────────────────────────────────────────────────────────────────────────
// Pure math / projection helpers
// ──────────────────────────────────────────────────────────────────────────────

private const val DEG2RAD = (PI / 180.0).toFloat()

/**
 * Convert geographic coordinates to a unit sphere vector.
 * Returns a [FloatArray] of size 3: [x, y, z].
 * +x = right (east), +y = up (north), +z = towards viewer.
 */
fun latLonToXyz(latDeg: Float, lonDeg: Float): FloatArray {
    val lat = latDeg * DEG2RAD
    val lon = lonDeg * DEG2RAD
    val cosLat = cos(lat)
    return floatArrayOf(
        cosLat * sin(lon),   // x
        sin(lat),             // y
        cosLat * cos(lon)    // z
    )
}

/**
 * Rotate a unit-sphere point by the camera angles and project to canvas.
 *
 * [p]      – 3-element unit vector from [latLonToXyz]
 * [rotY]   – camera Y-rotation (longitude pan) in degrees
 * [rotX]   – camera X-rotation (latitude tilt) in degrees
 * [cx],[cy] – canvas centre pixels
 * [r]      – globe radius in pixels
 *
 * Returns the projected [Offset], or **null** if the point is on the far side.
 */
fun projectPoint(
    p: FloatArray,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? {
    // ── Rotate around Y axis (longitude pan) ──────────────────────────────
    val ry = rotY * DEG2RAD
    val cosRy = cos(ry); val sinRy = sin(ry)
    val x1 = p[0] * cosRy + p[2] * sinRy
    val y1 = p[1]
    val z1 = -p[0] * sinRy + p[2] * cosRy

    // ── Rotate around X axis (latitude tilt) ─────────────────────────────
    val rx = rotX * DEG2RAD
    val cosRx = cos(rx); val sinRx = sin(rx)
    val x2 = x1
    val y2 = y1 * cosRx - z1 * sinRx
    val z2 = y1 * sinRx + z1 * cosRx

    // Behind the sphere → invisible
    if (z2 <= -0.05f) return null

    return Offset(cx + x2 * r, cy - y2 * r)
}

/**
 * Convenience: project directly from lat/lon.
 */
fun projectLatLon(
    latDeg: Float, lonDeg: Float,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? = projectPoint(latLonToXyz(latDeg, lonDeg), rotY, rotX, cx, cy, r)

/**
 * Build a [Path] for one continent polygon.
 *
 * [coords] – interleaved (lat°, lon°) pairs
 * Returns null if every point is on the far side of the globe.
 */
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

    // Pre-project into unit-sphere vectors to avoid re-computing trig
    val n = coords.size / 2
    var i = 0
    while (i < n) {
        val lat = coords[i * 2]
        val lon = coords[i * 2 + 1]
        val pt = projectLatLon(lat, lon, rotY, rotX, cx, cy, r)
        if (pt != null) {
            anyVisible = true
            if (first) {
                path.moveTo(pt.x, pt.y)
                first = false
            } else {
                path.lineTo(pt.x, pt.y)
            }
        } else if (!first) {
            // Lift pen; resume on next visible point
            first = true
        }
        i++
    }
    path.close()
    return if (anyVisible) path else null
}

// ──────────────────────────────────────────────────────────────────────────────
// DrawScope extensions  (ALL guard radius <= 0)
// ──────────────────────────────────────────────────────────────────────────────

/** Deep-space gradient background with a sparse star field. */
fun DrawScope.drawSpaceBackground(stars: FloatArray, starColors: Array<Color>) {
    val w = size.width; val h = size.height
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0A1628), Color(0xFF040810)),
            center = Offset(w * 0.5f, h * 0.45f),
            radius = maxOf(w, h) * 0.75f
        )
    )

    // Stars: pre-computed in normalised coords [0..1]
    val n = minOf(stars.size / 2, starColors.size)
    for (i in 0 until n) {
        val sx = stars[i * 2]     * w
        val sy = stars[i * 2 + 1] * h
        drawCircle(color = starColors[i], radius = 1.2f, center = Offset(sx, sy))
    }
}

/** Soft blue atmosphere glow behind the globe. */
fun DrawScope.drawAtmosphereGlow(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x003399FF), Color(0x552266CC), Color(0x00000000)),
            center = Offset(cx, cy),
            radius = r * 1.35f
        ),
        radius = r * 1.35f,
        center = Offset(cx, cy)
    )
}

/** Ocean sphere with a simple radial gradient. */
fun DrawScope.drawOceanSphere(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF4AA8D8),   // highlight centre
                Color(0xFF1A6FA0),   // mid ocean
                Color(0xFF0D3A5C)    // deep ocean edge
            ),
            center = Offset(cx - r * 0.2f, cy - r * 0.2f),
            radius = r * 1.4f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
}

/** Sunlight highlight — placed upper-left of the globe. */
fun DrawScope.drawSunlight(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val hlx = cx - r * 0.3f
    val hly = cy - r * 0.35f
    val hlr = r * 0.55f
    if (hlr <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x30FFFFFF), Color(0x00FFFFFF)),
            center = Offset(hlx, hly),
            radius = hlr
        ),
        radius = hlr,
        center = Offset(hlx, hly)
    )
}

/** Dark-side shadow — placed lower-right. */
fun DrawScope.drawNightSide(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    val shx = cx + r * 0.28f
    val shy = cy + r * 0.28f
    val shr = r * 0.75f
    if (shr <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x00000000), Color(0x55000020)),
            center = Offset(shx, shy),
            radius = shr
        ),
        radius = shr,
        center = Offset(shx, shy)
    )
}

/** Thin rim light (atmospheric edge scattering). */
fun DrawScope.drawAtmosphereRim(cx: Float, cy: Float, r: Float) {
    if (r <= 0f) return
    drawCircle(
        color = Color(0x445599FF),
        radius = r + 4f,
        center = Offset(cx, cy),
        style = Stroke(width = 8f)
    )
}

/**
 * Draw continent fills + coastline strokes for all visible polygons.
 * Paths are rebuilt every frame (the polygon data is simple enough that
 * rebuilding is cheaper than state-comparison for 60 fps on this device).
 */
fun DrawScope.drawContinents(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
) {
    if (r <= 0f) return
    val fill  = Color(0xFF3D9E5A)
    val coast = Color(0xFF2A7A44)

    for (poly in GlobeContinentData.allPolygons) {
        val path = buildContinentPath(poly, rotY, rotX, cx, cy, r) ?: continue
        drawPath(path = path, color = fill)
        drawPath(path = path, color = coast, style = Stroke(width = 1.2f, cap = StrokeCap.Round))
    }
}

/**
 * Polar ice caps at latitude > 72° and < -62°.
 */
fun DrawScope.drawIceCaps(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
) {
    if (r <= 0f) return
    val ice = Color(0xCCEEF6FF)

    for ((latBase, latStep, count) in listOf(
        Triple(72f,  1.5f, 12),   // Arctic
        Triple(-62f, -1.5f, 10)   // Antarctic
    )) {
        val path = Path()
        var first = true
        for (lon in 0..360 step 20) {
            val lat = latBase + latStep * (lon / 360f)
            val pt = projectLatLon(lat, lon.toFloat(), rotY, rotX, cx, cy, r) ?: continue
            if (first) { path.moveTo(pt.x, pt.y); first = false }
            else        path.lineTo(pt.x, pt.y)
        }
        path.close()
        if (!first) drawPath(path, color = ice)
    }
}

/**
 * Semi-transparent cloud wisp polygons (a second globe layer).
 * [cloudRotY] is the cloud-sphere Y-rotation offset from the main globe rotation.
 */
fun DrawScope.drawCloudLayer(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float,
    cloudRotY: Float
) {
    if (r <= 0f) return
    val cloudColor = Color(0x44FFFFFF)

    // A few simple cloud-band polygons at different latitudes
    val cloudBands = listOf(
        floatArrayOf(20f, -60f,  22f, -20f,  20f,  20f,  18f,  20f,  18f, -20f,  18f, -60f),
        floatArrayOf(40f,  60f,  42f,  90f,  40f, 120f,  38f, 120f,  38f,  90f,  38f,  60f),
        floatArrayOf(-15f, 30f, -13f,  70f, -15f, 110f, -17f, 110f, -17f,  70f, -17f,  30f),
        floatArrayOf(55f, -130f, 57f, -100f,  55f, -70f,  53f, -70f,  53f,-100f,  53f,-130f)
    )

    for (band in cloudBands) {
        val path = buildContinentPath(band, rotY + cloudRotY, rotX, cx, cy, r + 2f) ?: continue
        drawPath(path, color = cloudColor)
    }
}

/** Equator & tropics as faint overlay lines (optional; calls can be gated). */
fun DrawScope.drawGraticule(
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
) {
    if (r <= 0f) return
    val gridColor = Color(0x15FFFFFF)
    // Equator
    buildContinentPath(
        floatArrayOf(0f,-180f, 0f,-90f, 0f,0f, 0f,90f, 0f,179f),
        rotY, rotX, cx, cy, r
    )?.let { drawPath(it, color = gridColor, style = Stroke(1f)) }
}

// ──────────────────────────────────────────────────────────────────────────────
// Country marker helpers
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Project a country position and return its screen [Offset], or null if hidden.
 */
fun projectCountry(
    latDeg: Float, lonDeg: Float,
    rotY: Float, rotX: Float,
    cx: Float, cy: Float,
    r: Float
): Offset? = projectLatLon(latDeg, lonDeg, rotY, rotX, cx, cy, r)

/** Euclidean distance between two [Offset] values. */
fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return sqrt(dx * dx + dy * dy)
}
