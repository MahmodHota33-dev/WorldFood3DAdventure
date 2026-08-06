package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawFlightPath(
    animator: FlightAnimator,
    rotY: Float,
    rotX: Float,
    cx: Float,
    cy: Float,
    r: Float,
    reusablePath: Path
) {
    if (r <= 0f || !animator.hasVisiblePath) return
    val alpha = animator.pathAlpha
    if (alpha <= 0f) return

    reusablePath.reset()
    var hasSegment = false
    var firstPoint = true
    val segments = 28
    for (i in 0..segments) {
        val t = i.toFloat() / segments.toFloat()
        val sample = animator.sampleAt(t)
        val routeR = r * (1f + sample.altitudeNorm)
        val p = projectLatLon(sample.latDeg, sample.lonDeg, rotY, rotX, cx, cy, routeR)
        if (p != null) {
            if (firstPoint) {
                reusablePath.moveTo(p.x, p.y)
                firstPoint = false
            } else {
                reusablePath.lineTo(p.x, p.y)
            }
            hasSegment = true
        } else {
            firstPoint = true
        }
    }
    if (!hasSegment) return

    drawPath(
        path = reusablePath,
        color = Color(1f, 0.84f, 0.40f, 0.45f * alpha),
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
    drawPath(
        path = reusablePath,
        color = Color(1f, 0.95f, 0.72f, 0.22f * alpha),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )

    animator.currentSample()?.let { sample ->
        val head = projectLatLon(sample.latDeg, sample.lonDeg, rotY, rotX, cx, cy, r * (1f + sample.altitudeNorm))
        if (head != null) {
            drawCircle(
                color = Color(1f, 0.94f, 0.72f, 0.35f * alpha),
                radius = 8f,
                center = head
            )
            drawCircle(
                color = Color(1f, 0.98f, 0.88f, 0.78f * alpha),
                radius = 3.2f,
                center = head
            )
        }
    }
}

internal fun DrawScope.drawFlightAirplane(
    animator: FlightAnimator,
    rotY: Float,
    rotX: Float,
    cx: Float,
    cy: Float,
    r: Float,
    reusablePlanePath: Path
) {
    val samples = animator.currentDirectionSample() ?: return
    val current = samples.first
    val next = samples.second

    val p0 = projectLatLon(current.latDeg, current.lonDeg, rotY, rotX, cx, cy, r * (1f + current.altitudeNorm)) ?: return
    val p1 = projectLatLon(next.latDeg, next.lonDeg, rotY, rotX, cx, cy, r * (1f + next.altitudeNorm)) ?: return

    val dirX = p1.x - p0.x
    val dirY = p1.y - p0.y
    val len2 = dirX * dirX + dirY * dirY
    if (!len2.isFinite() || len2 <= 0.0001f) return
    val ang = atan2(dirY, dirX)

    val base = 8f
    val wing = 5f
    val tail = 6f
    val cosA = cos(ang)
    val sinA = sin(ang)

    fun rotate(dx: Float, dy: Float): Offset {
        return Offset(
            x = p0.x + dx * cosA - dy * sinA,
            y = p0.y + dx * sinA + dy * cosA
        )
    }

    val nose = rotate(base, 0f)
    val back = rotate(-tail, 0f)
    val wingTop = rotate(-1.5f, -wing)
    val wingBot = rotate(-1.5f, wing)
    val tailTop = rotate(-tail + 1.8f, -2.1f)
    val tailBot = rotate(-tail + 1.8f, 2.1f)

    reusablePlanePath.reset()
    reusablePlanePath.moveTo(nose.x, nose.y)
    reusablePlanePath.lineTo(wingTop.x, wingTop.y)
    reusablePlanePath.lineTo(tailTop.x, tailTop.y)
    reusablePlanePath.lineTo(back.x, back.y)
    reusablePlanePath.lineTo(tailBot.x, tailBot.y)
    reusablePlanePath.lineTo(wingBot.x, wingBot.y)
    reusablePlanePath.close()

    drawPath(reusablePlanePath, color = Color(0xE6FFF4D8))
    drawPath(
        reusablePlanePath,
        color = Color(0xC79A7A3A),
        style = Stroke(width = 1.2f, cap = StrokeCap.Round)
    )
    drawCircle(color = Color(0x44FFE5A8), radius = 7f, center = p0)
}
