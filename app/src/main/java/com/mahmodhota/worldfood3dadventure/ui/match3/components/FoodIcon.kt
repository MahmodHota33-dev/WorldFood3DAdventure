package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType

/**
 * Reusable component to render food icons using pure Canvas for performance.
 */
@Composable
fun FoodIcon(
    type: FoodTileType,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val s = this.size.width
        drawFoodIcon(type, s)
    }
}

private fun DrawScope.drawFoodIcon(type: FoodTileType, s: Float) {
    // Shared soft depth and gloss base.
    drawCircle(
        color = Color.Black.copy(alpha = 0.12f),
        radius = s * 0.42f,
        center = Offset(s * 0.54f, s * 0.58f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.06f),
        radius = s * 0.46f,
        center = Offset(s * 0.5f, s * 0.5f)
    )

    when (type) {
        // Group 1: Core
        FoodTileType.PIZZA -> drawPremiumPizza(s)
        FoodTileType.TOMATO -> drawPremiumTomato(s)
        FoodTileType.CHEESE -> drawPremiumCheese(s)
        FoodTileType.SUSHI -> drawPremiumSushi(s)
        FoodTileType.APPLE -> drawPremiumApple(s)
        FoodTileType.POTATO -> drawPremiumPotato(s)
        FoodTileType.BREAD -> drawPremiumBread(s)
        FoodTileType.PRETZEL -> drawPremiumPretzel(s)
        
        // Group 2: European
        FoodTileType.PASTA -> drawPremiumPasta(s)
        FoodTileType.GELATO -> drawPremiumGelato(s)
        FoodTileType.BASIL -> drawPremiumBasil(s)
        FoodTileType.SPAGHETTI -> drawPremiumSpaghetti(s)
        FoodTileType.LASAGNE -> drawPremiumLasagne(s)
        FoodTileType.RAVIOLI -> drawPremiumRavioli(s)
        FoodTileType.GNOCCHI -> drawPremiumGnocchi(s)
        FoodTileType.TIRAMISU -> drawPremiumTiramisu(s)
        FoodTileType.BRATWURST -> drawPremiumBratwurst(s)
        FoodTileType.BLACK_FOREST_CAKE -> drawPremiumCake(s)

        // Group 3: Regional A (France & Japan)
        FoodTileType.CROISSANT -> drawPremiumCroissant(s)
        FoodTileType.BAGUETTE -> drawPremiumBaguette(s)
        FoodTileType.FRENCH_CHEESE -> drawPremiumFrenchCheese(s)
        FoodTileType.CREPE -> drawPremiumCrepe(s)
        FoodTileType.MACARON -> drawPremiumMacaron(s)
        FoodTileType.RATATOUILLE -> drawPremiumRatatouille(s)
        FoodTileType.ECLAIR -> drawPremiumEclair(s)
        FoodTileType.SOUFFLE -> drawPremiumSouffle(s)
        FoodTileType.TARTE_TATIN -> drawPremiumTarteTatin(s)
        FoodTileType.RAMEN -> drawPremiumRamen(s)
        FoodTileType.TEMPURA -> drawPremiumTempura(s)
        FoodTileType.ONIGIRI -> drawPremiumOnigiri(s)
        FoodTileType.MOCHI -> drawPremiumMochi(s)
        FoodTileType.TAKOYAKI -> drawPremiumTakoyaki(s)
        FoodTileType.UDON -> drawPremiumUdon(s)
        FoodTileType.MATCHA -> drawPremiumMatcha(s)
        FoodTileType.DORAYAKI -> drawPremiumDorayaki(s)

        // Group 4: Regional B (Mexico & Sudan)
        FoodTileType.TACO -> drawPremiumTaco(s)
        FoodTileType.BURRITO -> drawPremiumBurrito(s)
        FoodTileType.GUACAMOLE -> drawPremiumGuacamole(s)
        FoodTileType.NACHOS -> drawPremiumNachos(s)
        FoodTileType.CHILI -> drawPremiumChili(s)
        FoodTileType.TAMALE -> drawPremiumTamale(s)
        FoodTileType.QUESADILLA -> drawPremiumQuesadilla(s)
        FoodTileType.CHURROS -> drawPremiumChurros(s)
        FoodTileType.POZOLE -> drawPremiumPozole(s)
        FoodTileType.KISRA -> drawPremiumKisra(s)
        FoodTileType.FUL_MEDAMES -> drawPremiumFul(s)
        FoodTileType.MULAH -> drawPremiumMulah(s)
        FoodTileType.TAGALIA -> drawPremiumTagalia(s)
        FoodTileType.AGASHE -> drawPremiumAgashe(s)
        FoodTileType.SAMBUSA -> drawPremiumSambusa(s)
        FoodTileType.SHAWAYA -> drawPremiumShawaya(s)
        FoodTileType.GURRASA -> drawPremiumGurrasa(s)
        FoodTileType.ASIDA -> drawPremiumAsida(s)
    }

    // Shared Premium Gloss Layer
    drawPremiumGloss(s)
}

// --- Group 1: Core Foods ---

private fun DrawScope.drawPremiumPizza(s: Float) {
    drawCircle(brush = Brush.verticalGradient(listOf(Color(0xFFE67E22), Color(0xFFD35400))), radius = s * 0.46f)
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFF1C40F), Color(0xFFF39C12))), radius = s * 0.38f)
    val toppingColor = Color(0xFFC0392B)
    drawCircle(toppingColor, radius = s * 0.07f, center = Offset(s * 0.4f, s * 0.35f))
    drawCircle(toppingColor, radius = s * 0.07f, center = Offset(s * 0.65f, s * 0.45f))
    drawCircle(toppingColor, radius = s * 0.07f, center = Offset(s * 0.45f, s * 0.65f))
}

private fun DrawScope.drawPremiumTomato(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFFF5252), Color(0xFFD32F2F))), radius = s * 0.42f)
    val leafPath = Path().apply {
        moveTo(s * 0.5f, s * 0.1f)
        lineTo(s * 0.6f, s * 0.25f)
        lineTo(s * 0.4f, s * 0.25f)
        close()
    }
    drawPath(leafPath, Color(0xFF27AE60))
}

private fun DrawScope.drawPremiumCheese(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.2f, s * 0.75f)
        lineTo(s * 0.85f, s * 0.75f)
        lineTo(s * 0.55f, s * 0.25f)
        close()
    }
    drawPath(brush = Brush.verticalGradient(listOf(Color(0xFFF1C40F), Color(0xFFF39C12))), path = path)
    drawCircle(Color(0xFFD4AC0D), radius = s * 0.05f, center = Offset(s * 0.45f, s * 0.6f))
    drawCircle(Color(0xFFD4AC0D), radius = s * 0.07f, center = Offset(s * 0.65f, s * 0.65f))
}

private fun DrawScope.drawPremiumSushi(s: Float) {
    drawRoundRect(color = Color.White, topLeft = Offset(s * 0.15f, s * 0.45f), size = Size(s * 0.7f, s * 0.35f), cornerRadius = CornerRadius(s * 0.1f))
    drawRoundRect(brush = Brush.linearGradient(listOf(Color(0xFFE74C3C), Color(0xFFFF7675))), topLeft = Offset(s * 0.15f, s * 0.35f), size = Size(s * 0.7f, s * 0.25f), cornerRadius = CornerRadius(s * 0.05f))
    drawRect(color = Color(0xFF2D3436), topLeft = Offset(s * 0.42f, s * 0.35f), size = Size(s * 0.16f, s * 0.45f))
}

private fun DrawScope.drawPremiumApple(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFFF7675), Color(0xFFD63031))), radius = s * 0.42f)
    drawRect(Color(0xFF5D4037), topLeft = Offset(s * 0.48f, s * 0.1f), size = Size(s * 0.04f, s * 0.2f))
    val leafPath = Path().apply {
        moveTo(s * 0.5f, s * 0.15f)
        quadraticTo(s * 0.7f, s * 0.05f, s * 0.75f, s * 0.2f)
        quadraticTo(s * 0.6f, s * 0.25f, s * 0.5f, s * 0.15f)
    }
    drawPath(leafPath, Color(0xFF27AE60))
}

private fun DrawScope.drawPremiumPotato(s: Float) {
    drawOval(brush = Brush.linearGradient(listOf(Color(0xFFE1B12C), Color(0xFFBCAA10))), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.65f, s * 0.45f))
    drawCircle(Color(0xFF8D6E63), radius = s * 0.02f, center = Offset(s * 0.4f, s * 0.45f))
    drawCircle(Color(0xFF8D6E63), radius = s * 0.02f, center = Offset(s * 0.65f, s * 0.55f))
}

private fun DrawScope.drawPremiumBread(s: Float) {
    drawRoundRect(brush = Brush.verticalGradient(listOf(Color(0xFFF39C12), Color(0xFFD35400))), topLeft = Offset(s * 0.15f, s * 0.4f), size = Size(s * 0.7f, s * 0.3f), cornerRadius = CornerRadius(s * 0.1f))
    repeat(3) { i ->
        drawRect(Color.White.copy(alpha = 0.3f), topLeft = Offset(s * (0.3f + i * 0.15f), s * 0.45f), size = Size(s * 0.05f, s * 0.2f))
    }
}

private fun DrawScope.drawPremiumPretzel(s: Float) {
    drawCircle(color = Color(0xFF8B4513), radius = s * 0.35f, style = Stroke(width = s * 0.12f))
    drawCircle(Color.White, radius = s * 0.02f, center = Offset(s * 0.5f, s * 0.15f))
    drawCircle(Color.White, radius = s * 0.02f, center = Offset(s * 0.3f, s * 0.4f))
    drawCircle(Color.White, radius = s * 0.02f, center = Offset(s * 0.7f, s * 0.4f))
}

// --- Group 2: European Foods ---

private fun DrawScope.drawPremiumPasta(s: Float) {
    val yellow = Color(0xFFF1C40F)
    repeat(3) { i ->
        drawRoundRect(color = yellow, topLeft = Offset(s * 0.2f, s * (0.3f + i * 0.15f)), size = Size(s * 0.6f, s * 0.1f), cornerRadius = CornerRadius(s * 0.02f))
    }
}

private fun DrawScope.drawPremiumGelato(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFF06292), Color(0xFFE91E63))), radius = s * 0.25f, center = Offset(s * 0.5f, s * 0.35f))
    val conePath = Path().apply {
        moveTo(s * 0.35f, s * 0.45f)
        lineTo(s * 0.65f, s * 0.45f)
        lineTo(s * 0.5f, s * 0.85f)
        close()
    }
    drawPath(conePath, Color(0xFFD2691E))
}

private fun DrawScope.drawPremiumBasil(s: Float) {
    val leafPath = Path().apply {
        moveTo(s * 0.5f, s * 0.8f)
        quadraticTo(s * 0.2f, s * 0.5f, s * 0.5f, s * 0.2f)
        quadraticTo(s * 0.8f, s * 0.5f, s * 0.5f, s * 0.8f)
    }
    drawPath(leafPath, Color(0xFF27AE60))
}

private fun DrawScope.drawPremiumSpaghetti(s: Float) {
    val color = Color(0xFFF4D03F)
    repeat(5) { i ->
        drawLine(color = color, start = Offset(s * 0.2f, s * (0.3f + i * 0.1f)), end = Offset(s * 0.8f, s * (0.3f + i * 0.1f)), strokeWidth = s * 0.03f)
    }
}

private fun DrawScope.drawPremiumLasagne(s: Float) {
    drawRect(Color(0xFF8B4513), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.4f))
    drawRect(Color(0xFFFDFEFE), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.1f))
    drawRect(Color(0xFFC0392B), topLeft = Offset(s * 0.2f, s * 0.5f), size = Size(s * 0.6f, s * 0.1f))
}

private fun DrawScope.drawPremiumRavioli(s: Float) {
    drawRoundRect(color = Color(0xFFFEF9E7), topLeft = Offset(s * 0.25f, s * 0.25f), size = Size(s * 0.5f, s * 0.5f), cornerRadius = CornerRadius(s * 0.05f))
    drawCircle(Color(0xFFF4D03F), radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumGnocchi(s: Float) {
    repeat(2) { i ->
        drawOval(color = Color(0xFFF9E79F), topLeft = Offset(s * (0.3f + i * 0.25f), s * 0.35f), size = Size(s * 0.15f, s * 0.3f))
    }
}

private fun DrawScope.drawPremiumTiramisu(s: Float) {
    drawRect(Color(0xFF5D4037), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.1f))
    drawRect(Color(0xFFFEF9E7), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.15f))
    drawRect(Color(0xFFDC7633), topLeft = Offset(s * 0.2f, s * 0.55f), size = Size(s * 0.6f, s * 0.15f))
}

private fun DrawScope.drawPremiumBratwurst(s: Float) {
    drawRoundRect(brush = Brush.linearGradient(listOf(Color(0xFF6E2C00), Color(0xFF3E2723))), topLeft = Offset(s * 0.1f, s * 0.4f), size = Size(s * 0.8f, s * 0.2f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumCake(s: Float) {
    drawRect(Color(0xFF2C3E50), topLeft = Offset(s * 0.25f, s * 0.4f), size = Size(s * 0.5f, s * 0.3f))
    drawRect(Color.White, topLeft = Offset(s * 0.25f, s * 0.45f), size = Size(s * 0.5f, s * 0.05f))
    drawCircle(Color.Red, radius = s * 0.08f, center = Offset(s * 0.5f, s * 0.3f))
}

// --- Group 3: France & Japan ---

private fun DrawScope.drawPremiumCroissant(s: Float) {
    drawRoundRect(brush = Brush.horizontalGradient(listOf(Color(0xFFFFD93D), Color(0xFFF39C12))), topLeft = Offset(s * 0.2f, s * 0.35f), size = Size(s * 0.6f, s * 0.3f), cornerRadius = CornerRadius(s * 0.15f))
}

private fun DrawScope.drawPremiumBaguette(s: Float) {
    drawRoundRect(color = Color(0xFFD2691E), topLeft = Offset(s * 0.1f, s * 0.4f), size = Size(s * 0.8f, s * 0.2f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumFrenchCheese(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.2f, s * 0.7f)
        lineTo(s * 0.8f, s * 0.7f)
        lineTo(s * 0.7f, s * 0.3f)
        lineTo(s * 0.3f, s * 0.3f)
        close()
    }
    drawPath(path, Color(0xFFFCF3CF))
}

private fun DrawScope.drawPremiumCrepe(s: Float) {
    drawCircle(Color(0xFFFEF9E7), radius = s * 0.35f)
}

private fun DrawScope.drawPremiumMacaron(s: Float) {
    drawRoundRect(Color(0xFFF48FB1), topLeft = Offset(s * 0.25f, s * 0.3f), size = Size(s * 0.5f, s * 0.15f), cornerRadius = CornerRadius(s * 0.05f))
    drawRect(Color.White, topLeft = Offset(s * 0.25f, s * 0.45f), size = Size(s * 0.5f, s * 0.05f))
    drawRoundRect(Color(0xFFF48FB1), topLeft = Offset(s * 0.25f, s * 0.5f), size = Size(s * 0.5f, s * 0.15f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumRatatouille(s: Float) {
    drawCircle(Color(0xFFE74C3C), radius = s * 0.35f)
    drawCircle(Color(0xFF2ECC71), radius = s * 0.15f)
}

private fun DrawScope.drawPremiumEclair(s: Float) {
    drawRoundRect(Color(0xFF3E2723), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f), cornerRadius = CornerRadius(s * 0.05f))
    drawRect(Color(0xFFFFF9C4), topLeft = Offset(s * 0.2f, s * 0.48f), size = Size(s * 0.6f, s * 0.04f))
}

private fun DrawScope.drawPremiumSouffle(s: Float) {
    drawRect(Color(0xFFFFF9C4), topLeft = Offset(s * 0.3f, s * 0.3f), size = Size(s * 0.4f, s * 0.4f))
    drawRect(Color.White, topLeft = Offset(s * 0.3f, s * 0.65f), size = Size(s * 0.4f, s * 0.1f))
}

private fun DrawScope.drawPremiumTarteTatin(s: Float) {
    drawCircle(Color(0xFFD35400), radius = s * 0.35f)
    drawCircle(Color(0xFFF39C12), radius = s * 0.25f)
}

private fun DrawScope.drawPremiumRamen(s: Float) {
    drawCircle(Color(0xFFE67E22), radius = s * 0.4f)
    drawCircle(Color(0xFFF4D03F), radius = s * 0.3f)
}

private fun DrawScope.drawPremiumTempura(s: Float) {
    drawRoundRect(Color(0xFFF39C12), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.3f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumOnigiri(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.2f)
        lineTo(s * 0.8f, s * 0.7f)
        lineTo(s * 0.2f, s * 0.7f)
        close()
    }
    drawPath(path, Color.White)
    drawRect(Color(0xFF2D3436), topLeft = Offset(s * 0.4f, s * 0.6f), size = Size(s * 0.2f, s * 0.15f))
}

private fun DrawScope.drawPremiumMochi(s: Float) {
    repeat(3) { i ->
        val color = when(i) { 0 -> Color(0xFFF48FB1); 1 -> Color.White; else -> Color(0xFFC5E1A5) }
        drawCircle(color, radius = s * 0.12f, center = Offset(s * (0.3f + i * 0.2f), s * 0.5f))
    }
}

private fun DrawScope.drawPremiumTakoyaki(s: Float) {
    drawCircle(Color(0xFFD35400), radius = s * 0.3f)
    drawPath(Path().apply { addOval(Rect(s * 0.3f, s * 0.3f, s * 0.7f, s * 0.7f)) }, Color(0xFF8B4513).copy(alpha = 0.4f))
}

private fun DrawScope.drawPremiumUdon(s: Float) {
    drawCircle(Color(0xFF2C3E50), radius = s * 0.4f)
    drawCircle(Color(0xFFFDFEFE), radius = s * 0.3f)
}

private fun DrawScope.drawPremiumMatcha(s: Float) {
    drawRoundRect(Color(0xFF27AE60), topLeft = Offset(s * 0.3f, s * 0.4f), size = Size(s * 0.4f, s * 0.3f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumDorayaki(s: Float) {
    drawOval(Color(0xFFD35400), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.2f))
    drawOval(Color(0xFFD35400), topLeft = Offset(s * 0.25f, s * 0.5f), size = Size(s * 0.5f, s * 0.2f))
}

// --- Group 4: Mexico & Sudan ---

private fun DrawScope.drawPremiumTaco(s: Float) {
    drawArc(Color(0xFFF1C40F), startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(s * 0.1f, s * 0.3f), size = Size(s * 0.8f, s * 0.4f))
    drawCircle(Color.Red, radius = s * 0.05f, center = Offset(s * 0.4f, s * 0.35f))
}

private fun DrawScope.drawPremiumBurrito(s: Float) {
    drawRoundRect(Color(0xFFF5CBA7), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumGuacamole(s: Float) {
    drawCircle(Color(0xFF27AE60), radius = s * 0.35f)
    drawCircle(Color(0xFF1D8348), radius = s * 0.1f, center = Offset(s * 0.4f, s * 0.4f))
}

private fun DrawScope.drawPremiumNachos(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.3f)
        lineTo(s * 0.7f, s * 0.7f)
        lineTo(s * 0.3f, s * 0.7f)
        close()
    }
    drawPath(path, Color(0xFFF4D03F))
}

private fun DrawScope.drawPremiumChili(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.2f)
        quadraticTo(s * 0.8f, s * 0.5f, s * 0.5f, s * 0.8f)
        quadraticTo(s * 0.2f, s * 0.5f, s * 0.5f, s * 0.2f)
    }
    drawPath(path, Color(0xFFC0392B))
}

private fun DrawScope.drawPremiumTamale(s: Float) {
    drawRoundRect(Color(0xFFF9E79F), topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.3f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumQuesadilla(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.2f, s * 0.2f)
        lineTo(s * 0.8f, s * 0.2f)
        lineTo(s * 0.2f, s * 0.8f)
        close()
    }
    drawPath(path, Color(0xFFF7DC6F))
}

private fun DrawScope.drawPremiumChurros(s: Float) {
    repeat(2) { i ->
        drawRect(Color(0xFFBA4A00), topLeft = Offset(s * (0.35f + i * 0.15f), s * 0.25f), size = Size(s * 0.1f, s * 0.5f))
    }
}

private fun DrawScope.drawPremiumPozole(s: Float) {
    drawCircle(Color(0xFFC0392B), radius = s * 0.35f)
    drawCircle(Color.White, radius = s * 0.1f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumKisra(s: Float) {
    drawRect(Color(0xFFFBFCFC), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f))
}

private fun DrawScope.drawPremiumFul(s: Float) {
    drawCircle(Color(0xFF6E2C00), radius = s * 0.35f)
    drawCircle(Color(0xFF27AE60), radius = s * 0.1f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumMulah(s: Float) {
    drawCircle(Color(0xFF1D8348), radius = s * 0.35f)
}

private fun DrawScope.drawPremiumTagalia(s: Float) {
    drawRoundRect(Color(0xFF7B241C), topLeft = Offset(s * 0.3f, s * 0.3f), size = Size(s * 0.4f, s * 0.4f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumAgashe(s: Float) {
    repeat(2) { i ->
        drawRect(Color(0xFF422222), topLeft = Offset(s * (0.4f + i * 0.1f), s * 0.2f), size = Size(s * 0.05f, s * 0.6f))
    }
}

private fun DrawScope.drawPremiumSambusa(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.3f)
        lineTo(s * 0.8f, s * 0.7f)
        lineTo(s * 0.2f, s * 0.7f)
        close()
    }
    drawPath(path, Color(0xFFD4AC0D))
}

private fun DrawScope.drawPremiumShawaya(s: Float) {
    drawRoundRect(Color(0xFF6E2C00), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.3f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumGurrasa(s: Float) {
    drawCircle(Color(0xFFFEF9E7), radius = s * 0.35f)
    drawCircle(Color(0xFFF4D03F), radius = s * 0.3f, style = Stroke(width = 2f))
}

private fun DrawScope.drawPremiumAsida(s: Float) {
    drawCircle(Color(0xFFFBFCFC), radius = s * 0.35f)
    drawCircle(Color(0xFF7B241C), radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.5f))
}

// --- Shared Helpers ---

private fun DrawScope.drawPremiumGloss(s: Float) {
    drawArc(
        color = Color.White.copy(alpha = 0.3f),
        startAngle = -120f,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(s * 0.2f, s * 0.2f),
        size = Size(s * 0.6f, s * 0.6f),
        style = Stroke(width = s * 0.04f)
    )
}
