package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
//  Country-themed Match-3 game backgrounds — Phase 10.4
//
//  Design goals:
//   • Deep navy foundation with country-specific atmospheric palette
//   • Iconic geographic/architectural silhouette per country
//   • All silhouettes in lower 70–90 % of screen so they read behind the board
//   • Sky gradients dark at top for header text readability
//   • Zero per-frame allocation: drawWithCache rebuilds paths only on size change
//   • No animation — game cascades already animate; background stays still
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Germany — Bavarian Alps / Black Forest.
 * Layered alpine peaks fading into pine-tree silhouette at screen base.
 */
@Composable
fun GermanyBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        // Defensive Guard: Skip drawing if dimensions are invalid or non-renderable
        if (w <= 0f || h <= 0f || !w.isFinite() || !h.isFinite()) {
            return@drawWithCache onDrawBehind { }
        }

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF0A1520),
                0.50f to Color(0xFF132B1C),
                0.72f to Color(0xFF6B3E10),
                1.00f to Color(0xFF101808)
            ),
            startY = 0f, endY = h
        )

        val horizonGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFC07A1A).copy(alpha = 0.42f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.74f),
            radius = w * 0.68f
        )

        // Distant pale Alpine ridge
        val farPeaks = Path().apply {
            moveTo(0f, h * 0.74f)
            lineTo(w * 0.08f, h * 0.53f)
            lineTo(w * 0.20f, h * 0.63f)
            lineTo(w * 0.33f, h * 0.46f)
            lineTo(w * 0.46f, h * 0.57f)
            lineTo(w * 0.58f, h * 0.44f)
            lineTo(w * 0.68f, h * 0.55f)
            lineTo(w * 0.80f, h * 0.48f)
            lineTo(w * 0.91f, h * 0.60f)
            lineTo(w, h * 0.68f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Near dark alpine ridge
        val nearPeaks = Path().apply {
            moveTo(0f, h * 0.82f)
            lineTo(w * 0.07f, h * 0.64f)
            lineTo(w * 0.16f, h * 0.74f)
            lineTo(w * 0.27f, h * 0.58f)
            lineTo(w * 0.37f, h * 0.68f)
            lineTo(w * 0.49f, h * 0.54f)
            lineTo(w * 0.59f, h * 0.65f)
            lineTo(w * 0.70f, h * 0.55f)
            lineTo(w * 0.80f, h * 0.67f)
            lineTo(w * 0.90f, h * 0.59f)
            lineTo(w, h * 0.70f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Pine forest silhouette — row of narrow triangles
        val forest = Path().apply {
            val step = w * 0.075f
            val treeH = h * 0.14f

            // Bounded loop: ensure termination even with zero or non-finite step
            if (step > 0f && step.isFinite()) {
                var x = -step * 0.5f
                var iterations = 0
                while (x <= w + step && iterations < 200) {
                    moveTo(x, h * 0.88f)
                    lineTo(x + step * 0.44f, h * 0.88f - treeH)
                    lineTo(x + step * 0.88f, h * 0.88f)
                    close()
                    x += step
                    iterations++
                }
            }
        }

        val ground = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A1608), Color(0xFF07100A)),
            startY = h * 0.86f, endY = h
        )

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = horizonGlow)
            drawPath(farPeaks, Color(0xFF2C4A20).copy(alpha = 0.70f))
            drawPath(nearPeaks, Color(0xFF1A3010))
            drawPath(forest, Color(0xFF0D1F08))
            drawRect(brush = ground)
        }
    })
}

/**
 * Italy — Roman twilight / Tuscan countryside.
 * Simplified Colosseum arch row above rolling hills, cypress silhouettes.
 */
@Composable
fun ItalyBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF0C1420),
                0.48f to Color(0xFF1E1808),
                0.72f to Color(0xFF904A18),
                1.00f to Color(0xFF3A1C08)
            ),
            startY = 0f, endY = h
        )

        val horizonGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFD08030).copy(alpha = 0.48f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.72f),
            radius = w * 0.72f
        )

        // Colosseum — platform + row of arch pillars
        val colosseum = Path().apply {
            val baseY = h * 0.65f
            val archH = h * 0.13f
            val archW = w * 0.08f
            val archCount = 7
            val totalW = archW * archCount
            val startX = (w - totalW) * 0.5f
            val pilW = archW * 0.20f

            // Base platform
            moveTo(startX - pilW, baseY)
            lineTo(startX + totalW + pilW, baseY)
            lineTo(startX + totalW + pilW, baseY + h * 0.022f)
            lineTo(startX - pilW, baseY + h * 0.022f)
            close()

            // Arch pillars + arch openings
            for (i in 0 until archCount) {
                val ax = startX + i * archW
                val cx = ax + archW * 0.5f
                val springY = baseY - archH * 0.55f  // top of pillar before arch

                // Left pillar fill
                moveTo(ax, baseY)
                lineTo(ax + pilW, baseY)
                lineTo(ax + pilW, springY)
                // Semicircle approximated by 6 lineTo steps
                val r = (archW - 2f * pilW) * 0.5f
                for (seg in 0..6) {
                    val a = PI.toFloat() * (1f - seg.toFloat() / 6f)
                    lineTo(cx + r * cos(a), springY - r * sin(a))
                }
                // Right pillar fill
                lineTo(ax + archW - pilW, springY)
                lineTo(ax + archW - pilW, baseY)
                close()
            }
        }

        // Far Tuscan hills
        val farHill = Path().apply {
            moveTo(0f, h * 0.73f)
            cubicTo(w * 0.22f, h * 0.62f, w * 0.48f, h * 0.66f, w * 0.72f, h * 0.60f)
            cubicTo(w * 0.84f, h * 0.57f, w * 0.93f, h * 0.64f, w, h * 0.70f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Near rolling hill
        val nearHill = Path().apply {
            moveTo(0f, h * 0.84f)
            cubicTo(w * 0.28f, h * 0.74f, w * 0.55f, h * 0.78f, w * 0.78f, h * 0.72f)
            cubicTo(w * 0.90f, h * 0.68f, w * 0.96f, h * 0.76f, w, h * 0.82f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Narrow cypress silhouettes
        val cypress = Path().apply {
            listOf(0.07f, 0.18f, 0.80f, 0.91f).forEach { fx ->
                val cx = w * fx
                val cy = h * 0.78f
                val cH = h * 0.09f
                val cW = w * 0.015f
                moveTo(cx, cy - cH)
                lineTo(cx + cW, cy - cH * 0.6f)
                lineTo(cx + cW * 0.6f, cy)
                lineTo(cx - cW * 0.6f, cy)
                lineTo(cx - cW, cy - cH * 0.6f)
                close()
            }
        }

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = horizonGlow)
            drawPath(farHill, Color(0xFF4A2A10).copy(alpha = 0.72f))
            drawPath(colosseum, Color(0xFF381C06))
            drawPath(nearHill, Color(0xFF281408))
            drawPath(cypress, Color(0xFF1A2808))
        }
    })
}

/**
 * France — Paris dusk / Eiffel Tower silhouette.
 * Deep blue-violet sky, warm city-light dots, tower silhouette, dark skyline.
 */
@Composable
fun FranceBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF04060E),
                0.40f to Color(0xFF0C0A1E),
                0.68f to Color(0xFF160E2C),
                1.00f to Color(0xFF080610)
            ),
            startY = 0f, endY = h
        )

        val cityGlow = Brush.radialGradient(
            colors = listOf(Color(0xFF4030A0).copy(alpha = 0.38f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.74f),
            radius = w * 0.82f
        )

        // Eiffel Tower silhouette
        val tower = Path().apply {
            val cx = w * 0.50f
            val baseY = h * 0.82f
            val baseHalfW = w * 0.11f
            val lp1Y = h * 0.62f   // lower platform
            val lp1HW = w * 0.045f // lower platform half-width
            val up2Y = h * 0.48f   // upper platform
            val up2HW = w * 0.028f
            val topY = h * 0.24f

            // Left silhouette edge (bottom to top)
            moveTo(cx - baseHalfW, baseY)
            lineTo(cx - lp1HW - w * 0.008f, lp1Y)      // left leg top
            lineTo(cx - lp1HW - w * 0.018f, lp1Y)       // lower platform left extent
            lineTo(cx - lp1HW - w * 0.018f, lp1Y - h * 0.018f)
            lineTo(cx - up2HW - w * 0.004f, up2Y)        // upper shaft left
            lineTo(cx - up2HW - w * 0.014f, up2Y)
            lineTo(cx - up2HW - w * 0.014f, up2Y - h * 0.014f)
            lineTo(cx - w * 0.006f, topY + h * 0.02f)   // narrow to peak
            lineTo(cx, topY)                              // peak
            // Right side mirrored
            lineTo(cx + w * 0.006f, topY + h * 0.02f)
            lineTo(cx + up2HW + w * 0.014f, up2Y - h * 0.014f)
            lineTo(cx + up2HW + w * 0.014f, up2Y)
            lineTo(cx + up2HW + w * 0.004f, up2Y)
            lineTo(cx + lp1HW + w * 0.018f, lp1Y - h * 0.018f)
            lineTo(cx + lp1HW + w * 0.018f, lp1Y)
            lineTo(cx + lp1HW + w * 0.008f, lp1Y)
            lineTo(cx + baseHalfW, baseY)
            // Base arch between legs
            cubicTo(cx + baseHalfW * 0.55f, baseY + h * 0.022f,
                    cx + baseHalfW * 0.15f, baseY + h * 0.028f,
                    cx, baseY + h * 0.012f)
            cubicTo(cx - baseHalfW * 0.15f, baseY + h * 0.028f,
                    cx - baseHalfW * 0.55f, baseY + h * 0.022f,
                    cx - baseHalfW, baseY)
            close()
        }

        // City skyline at base — varied building heights
        val skyline = Path().apply {
            val bldgs = listOf(
                0.00f to 0.84f, 0.04f to 0.78f, 0.08f to 0.84f, 0.11f to 0.76f,
                0.15f to 0.82f, 0.19f to 0.79f, 0.24f to 0.84f, 0.27f to 0.77f,
                0.31f to 0.84f, 0.35f to 0.80f, 0.39f to 0.84f,
                // tower gap (0.40 – 0.60)
                0.61f to 0.84f, 0.65f to 0.80f, 0.69f to 0.84f,
                0.72f to 0.77f, 0.76f to 0.82f, 0.80f to 0.76f,
                0.84f to 0.82f, 0.88f to 0.79f, 0.92f to 0.84f,
                0.96f to 0.80f, 1.00f to 0.84f
            )
            moveTo(0f, h)
            lineTo(0f, h * 0.84f)
            for ((xf, yf) in bldgs) { lineTo(w * xf, h * yf) }
            lineTo(w, h)
            close()
        }

        // Warm city light dots — static positions
        val lights = listOf(
            0.04f to 0.70f, 0.09f to 0.72f, 0.15f to 0.69f, 0.21f to 0.71f,
            0.28f to 0.70f, 0.35f to 0.72f, 0.65f to 0.70f, 0.72f to 0.71f,
            0.78f to 0.69f, 0.84f to 0.72f, 0.90f to 0.70f, 0.96f to 0.71f,
            0.06f to 0.74f, 0.14f to 0.75f, 0.23f to 0.74f, 0.30f to 0.76f,
            0.68f to 0.74f, 0.76f to 0.75f, 0.83f to 0.73f, 0.92f to 0.76f
        ).map { (xf, yf) -> Offset(w * xf, h * yf) }

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = cityGlow)
            // Warm city lights before skyline
            lights.forEachIndexed { i, pos ->
                drawCircle(Color(0xFFFFCC44).copy(alpha = 0.30f + (i % 5) * 0.06f),
                    radius = 2.0f, center = pos)
            }
            drawPath(skyline, Color(0xFF06040C))
            drawPath(tower, Color(0xFF0E0B1A))
        }
    })
}

/**
 * Spain — Moorish sunset / Mediterranean warmth.
 * Deep navy→vermillion sky, horseshoe-arch row silhouette, low rolling hills.
 */
@Composable
fun SpainBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF08090E),
                0.44f to Color(0xFF180808),
                0.70f to Color(0xFF8C2A0A),
                1.00f to Color(0xFF3A1408)
            ),
            startY = 0f, endY = h
        )

        val sunsetGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFCC4A18).copy(alpha = 0.50f), Color.Transparent),
            center = Offset(w * 0.50f, h * 0.70f),
            radius = w * 0.72f
        )

        // Moorish horseshoe-arch colonnade
        val arches = Path().apply {
            val baseY = h * 0.64f
            val archW = w * 0.11f
            val archH = h * 0.12f
            val count = 6
            val totalW = archW * count
            val startX = (w - totalW) * 0.5f
            val pilW = archW * 0.18f

            // Base band
            moveTo(startX - pilW, baseY)
            lineTo(startX + totalW + pilW, baseY)
            lineTo(startX + totalW + pilW, baseY + h * 0.020f)
            lineTo(startX - pilW, baseY + h * 0.020f)
            close()

            for (i in 0 until count) {
                val ax = startX + i * archW
                val cx = ax + archW * 0.5f
                val r = (archW * 0.5f - pilW) * 1.08f   // slightly wider than true semi
                val springY = baseY - archH * 0.52f

                moveTo(ax, baseY)
                lineTo(ax + pilW, baseY)
                lineTo(ax + pilW, springY)
                // Horseshoe arc: start below centre, sweep over
                for (seg in 0..8) {
                    val a = PI.toFloat() * (1.05f - seg.toFloat() * 1.10f / 8f)
                    lineTo(cx + r * cos(a), springY - r * 0.90f * sin(a) - r * 0.08f)
                }
                lineTo(ax + archW - pilW, springY)
                lineTo(ax + archW - pilW, baseY)
                close()
            }
        }

        // Far Mediterranean hill
        val hillFar = Path().apply {
            moveTo(0f, h * 0.73f)
            cubicTo(w * 0.28f, h * 0.62f, w * 0.58f, h * 0.66f, w, h * 0.71f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Near ground
        val hillNear = Path().apply {
            moveTo(0f, h * 0.84f)
            cubicTo(w * 0.35f, h * 0.76f, w * 0.68f, h * 0.80f, w, h * 0.84f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = sunsetGlow)
            drawPath(hillFar, Color(0xFF3C1808).copy(alpha = 0.80f))
            drawPath(arches, Color(0xFF2A0E04))
            drawPath(hillNear, Color(0xFF180C04))
        }
    })
}

/**
 * Japan — Mount Fuji / Evening indigo.
 * Deep indigo sky, iconic Fuji cone with snow cap, soft moon, static sakura dots.
 * No animation — game screen already animates heavily.
 */
@Composable
fun JapanBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF04061A),
                0.42f to Color(0xFF0E1030),
                0.70f to Color(0xFF1C1438),
                1.00f to Color(0xFF08080E)
            ),
            startY = 0f, endY = h
        )

        val moonGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFD8B4F0).copy(alpha = 0.28f), Color.Transparent),
            center = Offset(w * 0.80f, h * 0.14f),
            radius = w * 0.30f
        )

        // Mount Fuji — iconic gently concave upper slopes, wider base
        val fuji = Path().apply {
            val px = w * 0.50f
            val py = h * 0.28f
            val bY = h * 0.68f
            val bHW = w * 0.42f
            val shoulderInset = w * 0.06f  // subtle shoulder break
            val shoulderY = py + (bY - py) * 0.25f

            moveTo(px, py)
            // Left slope with slight shoulder concavity
            lineTo(px - shoulderInset, shoulderY)
            lineTo(px - bHW, bY)
            lineTo(0f, bY)
            lineTo(0f, h)
            lineTo(w, h)
            lineTo(w, bY)
            lineTo(px + bHW, bY)
            lineTo(px + shoulderInset, shoulderY)
            close()
        }

        // Snow cap
        val snow = Path().apply {
            val px = w * 0.50f
            val py = h * 0.28f
            val bY = h * 0.68f
            val capBase = py + (bY - py) * 0.18f
            moveTo(px, py)
            lineTo(px - w * 0.055f, capBase)
            lineTo(px + w * 0.055f, capBase)
            close()
        }

        // Foreground dark landscape base
        val landscape = Path().apply {
            moveTo(0f, h * 0.68f)
            lineTo(w * 0.15f, h * 0.64f)
            lineTo(w * 0.30f, h * 0.66f)
            lineTo(w * 0.70f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.63f)
            lineTo(w, h * 0.67f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Static sakura petal positions — pre-computed fractions
        val sakura = listOf(
            0.06f to 0.08f, 0.14f to 0.12f, 0.22f to 0.06f, 0.30f to 0.16f,
            0.38f to 0.10f, 0.46f to 0.18f, 0.54f to 0.07f, 0.62f to 0.14f,
            0.70f to 0.09f, 0.78f to 0.17f, 0.86f to 0.11f, 0.93f to 0.05f,
            0.10f to 0.22f, 0.26f to 0.20f, 0.58f to 0.22f, 0.82f to 0.19f
        ).mapIndexed { i, (xf, yf) -> Triple(w * xf, h * yf, 0.12f + (i % 5) * 0.04f) }

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = moonGlow)
            // Moon disc
            drawCircle(Color(0xFFE0C8F8).copy(alpha = 0.38f),
                radius = w * 0.055f, center = Offset(w * 0.80f, h * 0.14f))
            // Static sakura dots
            sakura.forEach { (sx, sy, alpha) ->
                drawCircle(Color(0xFFE8A0C4).copy(alpha = alpha),
                    radius = w * 0.007f, center = Offset(sx, sy))
            }
            drawPath(landscape, Color(0xFF0C0A18))
            drawPath(fuji, Color(0xFF1A1630))
            drawPath(snow, Color(0xFFCCB8E0).copy(alpha = 0.68f))
        }
    })
}

/**
 * Mexico — Mayan pyramid / coral desert sunset.
 * Deep teal→coral sky, step-pyramid silhouette, agave shapes, desert dunes.
 */
@Composable
fun MexicoBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF04080E),
                0.40f to Color(0xFF081414),
                0.66f to Color(0xFF8A2C10),
                1.00f to Color(0xFF1A0C06)
            ),
            startY = 0f, endY = h
        )

        val sunsetGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFCC4A18).copy(alpha = 0.52f), Color.Transparent),
            center = Offset(w * 0.62f, h * 0.68f),
            radius = w * 0.65f
        )

        // Mayan step pyramid (Chichen Itza style) — filled solid, step ledges lighter
        val pyramid = Path().apply {
            val cx = w * 0.35f
            val bW = w * 0.50f
            val bY = h * 0.76f
            val tY = h * 0.44f
            val steps = 5
            val stepH = (bY - tY) / steps

            // Solid silhouette
            moveTo(cx - bW * 0.5f, bY)
            lineTo(cx + bW * 0.5f, bY)
            lineTo(cx + bW * 0.10f, tY)
            lineTo(cx - bW * 0.10f, tY)
            close()

            // Step ledge overhangs — slightly lighter will be drawn on top
            for (i in 1 until steps) {
                val frac = i.toFloat() / steps
                val sw = bW * (1f - frac * 0.80f)
                val sy = bY - i * stepH
                // Horizontal ledge band
                moveTo(cx - sw * 0.5f - bW * 0.012f, sy)
                lineTo(cx + sw * 0.5f + bW * 0.012f, sy)
                lineTo(cx + sw * 0.5f + bW * 0.012f, sy + stepH * 0.14f)
                lineTo(cx - sw * 0.5f - bW * 0.012f, sy + stepH * 0.14f)
                close()
            }
        }

        // Temple top on pyramid
        val temple = Path().apply {
            val cx = w * 0.35f
            val tY = h * 0.44f
            val tW = w * 0.080f
            val tH = h * 0.060f
            moveTo(cx - tW * 0.5f, tY)
            lineTo(cx + tW * 0.5f, tY)
            lineTo(cx + tW * 0.5f, tY - tH)
            lineTo(cx - tW * 0.5f, tY - tH)
            close()
        }

        // Agave silhouettes (spiky leaf fans)
        val agave = Path().apply {
            listOf(w * 0.78f to h * 0.70f, w * 0.86f to h * 0.73f, w * 0.10f to h * 0.74f).forEach { (px, py) ->
                listOf(-55f, -30f, 0f, 30f, 55f).forEach { deg ->
                    val rad = deg * PI.toFloat() / 180f
                    val lL = h * 0.065f
                    val lW = w * 0.010f
                    moveTo(px, py)
                    lineTo(px + cos(rad - 0.12f) * lL, py - sin(rad - 0.12f) * lL)
                    lineTo(px + cos(rad) * lL * 1.10f, py - sin(rad) * lL * 1.10f)
                    lineTo(px + cos(rad + 0.12f) * lL, py - sin(rad + 0.12f) * lL)
                    close()
                }
            }
        }

        // Desert ground
        val desert = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.30f, h * 0.76f, w * 0.65f, h * 0.80f, w, h * 0.82f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        onDrawBehind {
            drawRect(brush = sky)
            drawRect(brush = sunsetGlow)
            drawPath(desert, Color(0xFF180E06))
            drawPath(pyramid, Color(0xFF140A04))
            drawPath(temple, Color(0xFF100804))
            drawPath(agave, Color(0xFF0A1006))
        }
    })
}

/**
 * Sudan — Nubian pyramids / desert twilight.
 * Most dramatic sky (finale country): wide amber glow, 3 steep Nubian pyramids,
 * subtle Nile ribbon, star field, sand dunes.
 */
@Composable
fun SudanBackground(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxSize().drawWithCache {
        val w = size.width
        val h = size.height

        val sky = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF030608),
                0.36f to Color(0xFF0C0A08),
                0.62f to Color(0xFF7A3E10),
                0.82f to Color(0xFFC86A1A),
                1.00f to Color(0xFF3A1E08)
            ),
            startY = 0f, endY = h
        )

        val wideGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFCC8020).copy(alpha = 0.58f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.80f),
            radius = w * 0.95f
        )

        // 3 Nubian pyramids — tall and steep (height ≈ 2× base half-width)
        val pyramids = listOf(
            Triple(w * 0.26f, w * 0.13f, h * 0.48f),  // left
            Triple(w * 0.54f, w * 0.17f, h * 0.40f),  // centre (tallest)
            Triple(w * 0.78f, w * 0.11f, h * 0.52f)   // right
        )
        val pyramidPath = Path().apply {
            pyramids.forEach { (cx, bHW, topY) ->
                val baseY = topY + bHW * 2.1f   // Nubian: steep, ~2× height ratio
                moveTo(cx - bHW, baseY)
                lineTo(cx, topY)
                lineTo(cx + bHW, baseY)
                close()
            }
        }

        // Pyramid right-face shadow (adds 3D depth cue)
        val pyramidShadow = Path().apply {
            pyramids.forEach { (cx, bHW, topY) ->
                val baseY = topY + bHW * 2.1f
                moveTo(cx, topY)
                lineTo(cx + bHW, baseY)
                lineTo(cx, baseY)
                close()
            }
        }

        // Far sand dune ridge
        val duneFar = Path().apply {
            moveTo(0f, h * 0.80f)
            cubicTo(w * 0.18f, h * 0.74f, w * 0.42f, h * 0.78f, w * 0.60f, h * 0.72f)
            cubicTo(w * 0.75f, h * 0.68f, w * 0.88f, h * 0.76f, w, h * 0.80f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Near dune
        val duneNear = Path().apply {
            moveTo(0f, h * 0.88f)
            cubicTo(w * 0.28f, h * 0.82f, w * 0.58f, h * 0.86f, w * 0.76f, h * 0.80f)
            cubicTo(w * 0.88f, h * 0.76f, w * 0.95f, h * 0.83f, w, h * 0.88f)
            lineTo(w, h); lineTo(0f, h); close()
        }

        // Nile ribbon — horizontal translucent band
        val nileBrush = Brush.horizontalGradient(
            listOf(Color.Transparent, Color(0xFF0A3050).copy(alpha = 0.55f),
                   Color(0xFF082A44).copy(alpha = 0.65f), Color.Transparent)
        )

        // Stars — static scattered dots in upper sky
        val stars = listOf(
            0.05f to 0.06f, 0.12f to 0.10f, 0.19f to 0.04f, 0.27f to 0.14f,
            0.34f to 0.08f, 0.41f to 0.17f, 0.50f to 0.05f, 0.58f to 0.11f,
            0.66f to 0.07f, 0.74f to 0.15f, 0.82f to 0.04f, 0.89f to 0.09f,
            0.96f to 0.13f, 0.08f to 0.20f, 0.32f to 0.22f, 0.62f to 0.19f
        ).mapIndexed { i, (xf, yf) -> Triple(w * xf, h * yf, 0.38f + (i % 6) * 0.08f) }

        onDrawBehind {
            drawRect(brush = sky)
            // Stars visible in upper sky
            stars.forEach { (sx, sy, a) ->
                drawCircle(Color.White.copy(alpha = a), radius = 1.4f, center = Offset(sx, sy))
            }
            drawRect(brush = wideGlow)
            drawPath(duneFar, Color(0xFF2A1A08).copy(alpha = 0.88f))
            drawRect(
                brush = nileBrush,
                topLeft = Offset(0f, h * 0.81f),
                size = Size(w, h * 0.055f)
            )
            drawPath(pyramidPath, Color(0xFF1C1008))
            drawPath(pyramidShadow, Color(0xFF0C0806).copy(alpha = 0.70f))
            drawPath(duneNear, Color(0xFF0E0A04))
        }
    })
}

/**
 * Pure logic helper to verify the forest iteration termination in unit tests.
 * Mirrors the safety logic in [GermanyBackground].
 */
internal fun verifyGermanyForestTermination(width: Float, step: Float): Int {
    if (width <= 0f || step <= 0f || !width.isFinite() || !step.isFinite()) return 0
    var x = -step * 0.5f
    var count = 0
    while (x <= width + step && count < 500) {
        count++
        x += step
    }
    return count
}
