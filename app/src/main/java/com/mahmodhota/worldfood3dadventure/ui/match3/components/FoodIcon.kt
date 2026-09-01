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
import androidx.compose.ui.graphics.BlendMode
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
    // Directional drop shadow (bottom-right offset for 3-D lift).
    drawCircle(
        color = Color.Black.copy(alpha = 0.22f),
        radius = s * 0.41f,
        center = Offset(s * 0.56f, s * 0.60f)
    )
    // Ambient occlusion rim (soft glow around the whole icon area).
    drawCircle(
        color = Color.Black.copy(alpha = 0.08f),
        radius = s * 0.47f,
        center = Offset(s * 0.5f, s * 0.5f)
    )
    // Subtle cool-rim light on the lower edge (counterlight to the warm background).
    drawCircle(
        color = Color(0xFFB3D4FF).copy(alpha = 0.05f),
        radius = s * 0.44f,
        center = Offset(s * 0.44f, s * 0.56f)
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

        // Group 5: Spain
        FoodTileType.PAELLA -> drawPremiumPaella(s)
        FoodTileType.TORTILLA_ESPANOLA -> drawPremiumTortillaEspanola(s)
        FoodTileType.JAMON_IBERICO -> drawPremiumJamonIberico(s)
        FoodTileType.GAZPACHO -> drawPremiumGazpacho(s)
        FoodTileType.CROQUETAS -> drawPremiumCroquetas(s)
        FoodTileType.PATATAS_BRAVAS -> drawPremiumPatatasBravas(s)
        FoodTileType.PULPO_A_LA_GALLEGA -> drawPremiumPulpo(s)
        FoodTileType.SANGRIA -> drawPremiumSangria(s)
        FoodTileType.CREMA_CATALANA -> drawPremiumCremaCatalana(s)

        // NEW Global Expansion
        FoodTileType.BURGER -> drawPremiumBurger(s)
        FoodTileType.FRIES -> drawPremiumFries(s)
        FoodTileType.HOT_DOG -> drawPremiumHotDog(s)
        FoodTileType.DONUT -> drawPremiumDonut(s)
        FoodTileType.PANCAKE -> drawPremiumPancake(s)
        FoodTileType.STEAK -> drawPremiumSteak(s)
        FoodTileType.FISH_AND_CHIPS -> drawPremiumFishAndChips(s)
        FoodTileType.SCONE -> drawPremiumScone(s)
        FoodTileType.TEA -> drawPremiumTea(s)
        FoodTileType.POT_PIE -> drawPremiumPotPie(s)
        FoodTileType.DIM_SUM -> drawPremiumDimSum(s)
        FoodTileType.DUMPLING -> drawPremiumDumpling(s)
        FoodTileType.FRIED_RICE -> drawPremiumFriedRice(s)
        FoodTileType.PEKING_DUCK -> drawPremiumDuck(s)
        FoodTileType.SPRING_ROLL -> drawPremiumSpringRoll(s)
        FoodTileType.CURRY -> drawPremiumCurry(s)
        FoodTileType.NAAN -> drawPremiumNaan(s)
        FoodTileType.BIRYANI -> drawPremiumBiryani(s)
        FoodTileType.TANDOORI_CHICKEN -> drawPremiumTandoori(s)
        FoodTileType.GULAB_JAMUN -> drawPremiumGulabJamun(s)
        FoodTileType.FEIJOADA -> drawPremiumFeijoada(s)
        FoodTileType.BRIGADEIRO -> drawPremiumBrigadeiro(s)
        FoodTileType.COXINHA -> drawPremiumCoxinha(s)
        FoodTileType.PADE_QUEIJO -> drawPremiumPaoDeQueijo(s)
        FoodTileType.KOSHARY -> drawPremiumKoshary(s)
        FoodTileType.FALAFEL -> drawPremiumFalafel(s)
        FoodTileType.SHAWARMA -> drawPremiumShawarma(s)
        FoodTileType.BAKLAVA -> drawPremiumBaklava(s)
        FoodTileType.GYROS -> drawPremiumGyros(s)
        FoodTileType.MOUSSAKA -> drawPremiumMoussaka(s)
        FoodTileType.FETA -> drawPremiumFeta(s)
        FoodTileType.OLIVES -> drawPremiumOlives(s)
        FoodTileType.PAD_THAI -> drawPremiumPadThai(s)
        FoodTileType.TOM_YUM -> drawPremiumTomYum(s)
        FoodTileType.MANGO_STICKY_RICE -> drawPremiumMangoRice(s)
        FoodTileType.KIMCHI -> drawPremiumKimchi(s)
        FoodTileType.BIBIMBAP -> drawPremiumBibimbap(s)
        FoodTileType.BULGOGI -> drawPremiumBulgogi(s)
        FoodTileType.KEBAB -> drawPremiumKebab(s)
        FoodTileType.TURKISH_DELIGHT -> drawPremiumTurkishDelight(s)
        FoodTileType.KOFTE -> drawPremiumKofte(s)
        FoodTileType.COFFEE -> drawPremiumCoffee(s)
        FoodTileType.MILK -> drawPremiumMilk(s)
        FoodTileType.EGG -> drawPremiumEgg(s)
        FoodTileType.CORN -> drawPremiumCorn(s)
        FoodTileType.FISH -> drawPremiumFish(s)
        FoodTileType.CHICKEN -> drawPremiumChicken(s)
        FoodTileType.RICE -> drawPremiumRice(s)
    }

    // Shared Premium Gloss Layer
    drawPremiumGloss(s)
}

// --- NEW Expansion Drawing Methods ---

private fun DrawScope.drawPremiumBurger(s: Float) {
    drawRoundRect(Color(0xFF8D6E63), topLeft = Offset(s * 0.2f, s * 0.55f), size = Size(s * 0.6f, s * 0.2f), cornerRadius = CornerRadius(s * 0.05f))
    drawRoundRect(Color(0xFFFFA000), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.25f), cornerRadius = CornerRadius(s * 0.15f))
    drawRect(Color(0xFF4CAF50), topLeft = Offset(s * 0.2f, s * 0.5f), size = Size(s * 0.6f, s * 0.05f))
}

private fun DrawScope.drawPremiumFries(s: Float) {
    drawRect(Color(0xFFE53935), topLeft = Offset(s * 0.25f, s * 0.5f), size = Size(s * 0.5f, s * 0.35f))
    repeat(4) { i ->
        drawRect(Color(0xFFFFEB3B), topLeft = Offset(s * (0.3f + i * 0.1f), s * 0.2f), size = Size(s * 0.08f, s * 0.4f))
    }
}

private fun DrawScope.drawPremiumHotDog(s: Float) {
    drawRoundRect(Color(0xFFD2691E), topLeft = Offset(s * 0.15f, s * 0.35f), size = Size(s * 0.7f, s * 0.3f), cornerRadius = CornerRadius(s * 0.1f))
    drawRoundRect(Color(0xFF8B0000), topLeft = Offset(s * 0.1f, s * 0.45f), size = Size(s * 0.8f, s * 0.1f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumDonut(s: Float) {
    drawCircle(Color(0xFFE91E63), radius = s * 0.4f)
    drawCircle(Color.Black, radius = s * 0.12f, blendMode = BlendMode.Clear)
}

private fun DrawScope.drawPremiumPancake(s: Float) {
    repeat(3) { i ->
        drawCircle(Color(0xFFF5DEB3), radius = s * 0.35f, center = Offset(s * 0.5f, s * (0.4f + i * 0.05f)))
    }
}

private fun DrawScope.drawPremiumSteak(s: Float) {
    drawOval(Color(0xFF5D4037), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.4f))
    drawLine(Color.White.copy(alpha = 0.2f), start = Offset(s * 0.3f, s * 0.4f), end = Offset(s * 0.7f, s * 0.6f), strokeWidth = 2f)
}

private fun DrawScope.drawPremiumFishAndChips(s: Float) {
    drawRoundRect(Color(0xFFD2691E), topLeft = Offset(s * 0.2f, s * 0.35f), size = Size(s * 0.4f, s * 0.3f), cornerRadius = CornerRadius(s * 0.05f))
    drawRect(Color(0xFFFFEB3B), topLeft = Offset(s * 0.65f, s * 0.4f), size = Size(s * 0.1f, s * 0.3f))
}

private fun DrawScope.drawPremiumScone(s: Float) {
    drawCircle(Color(0xFFF5DEB3), radius = s * 0.3f)
    drawCircle(Color(0xFFE91E63), radius = s * 0.1f, center = Offset(s * 0.5f, s * 0.45f))
}

private fun DrawScope.drawPremiumTea(s: Float) {
    drawArc(Color.White, startAngle = 0f, sweepAngle = 180f, useCenter = true, topLeft = Offset(s * 0.25f, s * 0.4f), size = Size(s * 0.5f, s * 0.4f))
    drawRect(Color(0xFF5D4037), topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.05f))
}

private fun DrawScope.drawPremiumPotPie(s: Float) {
    drawCircle(Color(0xFFD2691E), radius = s * 0.4f)
    drawLine(Color.Black.copy(alpha = 0.2f), start = Offset(s * 0.3f, s * 0.5f), end = Offset(s * 0.7f, s * 0.5f))
}

private fun DrawScope.drawPremiumDimSum(s: Float) {
    drawCircle(Color(0xFFFDFEFE), radius = s * 0.35f)
    repeat(3) { i -> drawCircle(Color(0xFFFDFEFE), radius = s * 0.12f, center = Offset(s * (0.4f + i * 0.1f), s * 0.5f)) }
}

private fun DrawScope.drawPremiumDumpling(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.3f, s * 0.6f)
        quadraticTo(s * 0.5f, s * 0.2f, s * 0.7f, s * 0.6f)
        close()
    }
    drawPath(path, Color(0xFFFDFEFE))
}

private fun DrawScope.drawPremiumFriedRice(s: Float) {
    drawCircle(Color(0xFFFFF9C4), radius = s * 0.35f)
    drawCircle(Color(0xFF4CAF50), radius = s * 0.05f, center = Offset(s * 0.4f, s * 0.4f))
}

private fun DrawScope.drawPremiumDuck(s: Float) {
    drawOval(Color(0xFF8B4513), topLeft = Offset(s * 0.2f, s * 0.35f), size = Size(s * 0.6f, s * 0.3f))
}

private fun DrawScope.drawPremiumSpringRoll(s: Float) {
    drawRoundRect(Color(0xFFD4AC0D), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumCurry(s: Float) {
    drawCircle(Color(0xFFD35400), radius = s * 0.38f)
    drawCircle(Color(0xFFF39C12), radius = s * 0.3f)
}

private fun DrawScope.drawPremiumNaan(s: Float) {
    drawOval(Color(0xFFFEF9E7), topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.4f))
}

private fun DrawScope.drawPremiumBiryani(s: Float) {
    drawCircle(Color(0xFFF4D03F), radius = s * 0.35f)
    drawCircle(Color(0xFFBA4A00), radius = s * 0.1f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumTandoori(s: Float) {
    drawRoundRect(Color(0xFFC0392B), topLeft = Offset(s * 0.3f, s * 0.3f), size = Size(s * 0.4f, s * 0.4f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumGulabJamun(s: Float) {
    drawCircle(Color(0xFF6E2C00), radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumFeijoada(s: Float) {
    drawCircle(Color(0xFF1C1C1C), radius = s * 0.38f)
}

private fun DrawScope.drawPremiumBrigadeiro(s: Float) {
    drawCircle(Color(0xFF3E2723), radius = s * 0.2f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumCoxinha(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.5f, s * 0.2f)
        lineTo(s * 0.7f, s * 0.7f)
        lineTo(s * 0.3f, s * 0.7f)
        close()
    }
    drawPath(path, Color(0xFFD4AC0D))
}

private fun DrawScope.drawPremiumPaoDeQueijo(s: Float) {
    drawCircle(Color(0xFFFFF9C4), radius = s * 0.25f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumKoshary(s: Float) {
    drawCircle(Color(0xFFE59866), radius = s * 0.38f)
    drawRect(Color(0xFFC0392B), topLeft = Offset(s * 0.4f, s * 0.3f), size = Size(s * 0.2f, s * 0.1f))
}

private fun DrawScope.drawPremiumFalafel(s: Float) {
    repeat(3) { i -> drawCircle(Color(0xFF6E2C00), radius = s * 0.12f, center = Offset(s * (0.35f + i * 0.15f), s * 0.5f)) }
}

private fun DrawScope.drawPremiumShawarma(s: Float) {
    drawRoundRect(Color(0xFFF5DEB3), topLeft = Offset(s * 0.3f, s * 0.25f), size = Size(s * 0.4f, s * 0.5f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumBaklava(s: Float) {
    drawRect(Color(0xFFD4AC0D), topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.3f))
}

private fun DrawScope.drawPremiumGyros(s: Float) {
    drawArc(Color(0xFFF5DEB3), startAngle = 0f, sweepAngle = 180f, useCenter = true, topLeft = Offset(s * 0.2f, s * 0.3f), size = Size(s * 0.6f, s * 0.4f))
}

private fun DrawScope.drawPremiumMoussaka(s: Float) {
    drawRect(Color(0xFF5D4037), topLeft = Offset(s * 0.25f, s * 0.3f), size = Size(s * 0.5f, s * 0.4f))
}

private fun DrawScope.drawPremiumFeta(s: Float) {
    drawRect(Color.White, topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.3f))
}

private fun DrawScope.drawPremiumOlives(s: Float) {
    drawCircle(Color(0xFF1B5E20), radius = s * 0.1f, center = Offset(s * 0.4f, s * 0.5f))
    drawCircle(Color(0xFF1B5E20), radius = s * 0.1f, center = Offset(s * 0.6f, s * 0.5f))
}

private fun DrawScope.drawPremiumPadThai(s: Float) {
    drawCircle(Color(0xFFE59866), radius = s * 0.35f)
}

private fun DrawScope.drawPremiumTomYum(s: Float) {
    drawCircle(Color(0xFFC0392B), radius = s * 0.38f)
}

private fun DrawScope.drawPremiumMangoRice(s: Float) {
    drawRect(Color.White, topLeft = Offset(s * 0.3f, s * 0.5f), size = Size(s * 0.4f, s * 0.2f))
    drawCircle(Color(0xFFFFC107), radius = s * 0.15f, center = Offset(s * 0.5f, s * 0.4f))
}

private fun DrawScope.drawPremiumKimchi(s: Float) {
    drawRect(Color(0xFFC0392B), topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.4f))
}

private fun DrawScope.drawPremiumBibimbap(s: Float) {
    drawCircle(Color(0xFFFDFEFE), radius = s * 0.38f)
}

private fun DrawScope.drawPremiumBulgogi(s: Float) {
    drawRoundRect(Color(0xFF5D4037), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.3f), cornerRadius = CornerRadius(s * 0.05f))
}

private fun DrawScope.drawPremiumKebab(s: Float) {
    repeat(3) { i -> drawCircle(Color(0xFF5D4037), radius = s * 0.1f, center = Offset(s * 0.5f, s * (0.3f + i * 0.2f))) }
}

private fun DrawScope.drawPremiumTurkishDelight(s: Float) {
    drawRect(Color(0xFFF48FB1), topLeft = Offset(s * 0.35f, s * 0.35f), size = Size(s * 0.3f, s * 0.3f))
}

private fun DrawScope.drawPremiumKofte(s: Float) {
    drawOval(Color(0xFF5D4037), topLeft = Offset(s * 0.3f, s * 0.35f), size = Size(s * 0.4f, s * 0.2f))
}

private fun DrawScope.drawPremiumCoffee(s: Float) {
    drawRect(Color(0xFF5D4037), topLeft = Offset(s * 0.35f, s * 0.4f), size = Size(s * 0.3f, s * 0.35f))
}

private fun DrawScope.drawPremiumMilk(s: Float) {
    drawRect(Color.White, topLeft = Offset(s * 0.35f, s * 0.3f), size = Size(s * 0.3f, s * 0.5f))
}

private fun DrawScope.drawPremiumEgg(s: Float) {
    drawCircle(Color.White, radius = s * 0.3f, center = Offset(s * 0.5f, s * 0.5f))
    drawCircle(Color(0xFFFFC107), radius = s * 0.12f, center = Offset(s * 0.5f, s * 0.5f))
}

private fun DrawScope.drawPremiumCorn(s: Float) {
    drawRoundRect(Color(0xFFF4D03F), topLeft = Offset(s * 0.4f, s * 0.25f), size = Size(s * 0.2f, s * 0.5f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumFish(s: Float) {
    drawOval(Color(0xFF85C1E9), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f))
}

private fun DrawScope.drawPremiumChicken(s: Float) {
    drawOval(Color(0xFFE59866), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.3f))
}

private fun DrawScope.drawPremiumRice(s: Float) {
    drawCircle(Color(0xFFFDFEFE), radius = s * 0.35f)
}

// --- Group 1: Core Foods ---

private fun DrawScope.drawPremiumPizza(s: Float) {
    // Crust ring
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFD4875A), Color(0xFFAA5E2C))), radius = s * 0.46f)
    // Cheese base
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFF5D96B), Color(0xFFD4A82A))), radius = s * 0.37f)
    // Sauce peek
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFE85D34), Color(0xFFC0392B))), radius = s * 0.28f)
    // More cheese specks
    drawCircle(Color(0xFFF9F0C0), radius = s * 0.12f, center = Offset(s * 0.48f, s * 0.48f))
    // Toppings
    val pepperoni = Color(0xFFC0392B)
    drawCircle(pepperoni, radius = s * 0.065f, center = Offset(s * 0.39f, s * 0.34f))
    drawCircle(pepperoni, radius = s * 0.065f, center = Offset(s * 0.64f, s * 0.44f))
    drawCircle(pepperoni, radius = s * 0.065f, center = Offset(s * 0.44f, s * 0.64f))
    // Crust edge highlight
    drawCircle(color = Color.White.copy(alpha = 0.18f), radius = s * 0.44f, style = Stroke(width = s * 0.03f))
}

private fun DrawScope.drawPremiumTomato(s: Float) {
    // Body with richer radial gradient
    drawCircle(brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF6B6B), Color(0xFFE53935), Color(0xFFB71C1C)),
        center = Offset(s * 0.45f, s * 0.44f),
        radius = s * 0.45f
    ), radius = s * 0.42f)
    // Stem
    drawRoundRect(Color(0xFF2E7D32), topLeft = Offset(s * 0.472f, s * 0.10f), size = Size(s * 0.055f, s * 0.18f), cornerRadius = CornerRadius(s * 0.02f))
    // Small calyx leaves
    val leafPath = Path().apply {
        moveTo(s * 0.50f, s * 0.18f)
        quadraticTo(s * 0.64f, s * 0.10f, s * 0.66f, s * 0.24f)
        quadraticTo(s * 0.56f, s * 0.24f, s * 0.50f, s * 0.18f)
    }
    drawPath(leafPath, Color(0xFF388E3C))
    val leafPath2 = Path().apply {
        moveTo(s * 0.50f, s * 0.18f)
        quadraticTo(s * 0.36f, s * 0.10f, s * 0.34f, s * 0.24f)
        quadraticTo(s * 0.44f, s * 0.24f, s * 0.50f, s * 0.18f)
    }
    drawPath(leafPath2, Color(0xFF43A047))
    // Highlight spot
    drawCircle(Color.White.copy(alpha = 0.28f), radius = s * 0.10f, center = Offset(s * 0.38f, s * 0.36f))
}

private fun DrawScope.drawPremiumCheese(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.18f, s * 0.76f)
        lineTo(s * 0.86f, s * 0.76f)
        lineTo(s * 0.86f, s * 0.52f)
        lineTo(s * 0.56f, s * 0.22f)
        lineTo(s * 0.18f, s * 0.52f)
        close()
    }
    drawPath(brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF9D84C), Color(0xFFE8B420), Color(0xFFCC9A0A)),
        startY = s * 0.22f, endY = s * 0.76f
    ), path = path)
    // Holes
    drawCircle(Color(0xFFD4A82A), radius = s * 0.055f, center = Offset(s * 0.44f, s * 0.60f))
    drawCircle(Color(0xFFD4A82A), radius = s * 0.07f, center = Offset(s * 0.65f, s * 0.66f))
    drawCircle(Color(0xFFD4A82A), radius = s * 0.045f, center = Offset(s * 0.36f, s * 0.50f))
    // Edge highlight
    drawPath(path = path, color = Color.White.copy(alpha = 0.15f), style = Stroke(width = s * 0.025f))
}

private fun DrawScope.drawPremiumSushi(s: Float) {
    // Rice base
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFFF8F8F2), Color(0xFFE8E8DC))),
        topLeft = Offset(s * 0.14f, s * 0.44f), size = Size(s * 0.72f, s * 0.36f),
        cornerRadius = CornerRadius(s * 0.10f)
    )
    // Fish topping (salmon)
    drawRoundRect(
        brush = Brush.linearGradient(listOf(Color(0xFFFF8C69), Color(0xFFE55B3C))),
        topLeft = Offset(s * 0.14f, s * 0.30f), size = Size(s * 0.72f, s * 0.24f),
        cornerRadius = CornerRadius(s * 0.07f)
    )
    // Nori (seaweed band)
    drawRect(
        color = Color(0xFF1B3A2D),
        topLeft = Offset(s * 0.40f, s * 0.30f), size = Size(s * 0.20f, s * 0.50f)
    )
    // Fish highlight
    drawLine(Color.White.copy(alpha = 0.3f), start = Offset(s * 0.22f, s * 0.36f), end = Offset(s * 0.55f, s * 0.36f), strokeWidth = s * 0.025f)
}

private fun DrawScope.drawPremiumApple(s: Float) {
    // Body with warm red gradient
    drawCircle(brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF8A80), Color(0xFFE53935), Color(0xFFC62828)),
        center = Offset(s * 0.45f, s * 0.45f),
        radius = s * 0.46f
    ), radius = s * 0.42f)
    // Stem
    drawRoundRect(Color(0xFF5D4037), topLeft = Offset(s * 0.476f, s * 0.10f), size = Size(s * 0.048f, s * 0.22f), cornerRadius = CornerRadius(s * 0.02f))
    // Leaf
    val leafPath = Path().apply {
        moveTo(s * 0.50f, s * 0.16f)
        quadraticTo(s * 0.70f, s * 0.06f, s * 0.74f, s * 0.20f)
        quadraticTo(s * 0.62f, s * 0.28f, s * 0.50f, s * 0.16f)
    }
    drawPath(brush = Brush.linearGradient(listOf(Color(0xFF43A047), Color(0xFF2E7D32))), path = leafPath)
    // Highlight spot
    drawCircle(Color.White.copy(alpha = 0.30f), radius = s * 0.11f, center = Offset(s * 0.36f, s * 0.36f))
    // Small secondary highlight
    drawCircle(Color.White.copy(alpha = 0.15f), radius = s * 0.055f, center = Offset(s * 0.28f, s * 0.44f))
}

private fun DrawScope.drawPremiumPotato(s: Float) {
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFF0C84C), Color(0xFFC9A020), Color(0xFF9E7A10)),
            center = Offset(s * 0.44f, s * 0.46f),
            radius = s * 0.38f
        ),
        topLeft = Offset(s * 0.18f, s * 0.28f), size = Size(s * 0.66f, s * 0.46f)
    )
    // Eyes
    drawCircle(Color(0xFF8D6E63), radius = s * 0.028f, center = Offset(s * 0.38f, s * 0.44f))
    drawCircle(Color(0xFF8D6E63), radius = s * 0.022f, center = Offset(s * 0.60f, s * 0.54f))
    drawCircle(Color(0xFF8D6E63), radius = s * 0.024f, center = Offset(s * 0.48f, s * 0.60f))
    // Skin highlight
    drawOval(color = Color.White.copy(alpha = 0.22f), topLeft = Offset(s * 0.26f, s * 0.30f), size = Size(s * 0.32f, s * 0.14f))
}

private fun DrawScope.drawPremiumBread(s: Float) {
    // Loaf body
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF5A623), Color(0xFFD4700A), Color(0xFFAA5000)),
            startY = s * 0.36f, endY = s * 0.76f
        ),
        topLeft = Offset(s * 0.13f, s * 0.38f), size = Size(s * 0.74f, s * 0.38f),
        cornerRadius = CornerRadius(s * 0.14f)
    )
    // Top crust dome
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFCB6B), Color(0xFFD4700A)),
            center = Offset(s * 0.5f, s * 0.38f), radius = s * 0.38f
        ),
        topLeft = Offset(s * 0.13f, s * 0.28f), size = Size(s * 0.74f, s * 0.28f)
    )
    // Score lines (cuts on top)
    repeat(2) { i ->
        drawLine(Color(0xFF8B4513).copy(alpha = 0.55f),
            start = Offset(s * (0.34f + i * 0.16f), s * 0.34f),
            end = Offset(s * (0.38f + i * 0.16f), s * 0.60f),
            strokeWidth = s * 0.028f)
    }
    // Top highlight
    drawOval(color = Color.White.copy(alpha = 0.20f), topLeft = Offset(s * 0.22f, s * 0.30f), size = Size(s * 0.32f, s * 0.12f))
}

private fun DrawScope.drawPremiumPretzel(s: Float) {
    val brown = Color(0xFF8B4513)
    val lightBrown = Color(0xFFAD6528)
    // Main body ring (lower loops)
    drawCircle(color = brown, radius = s * 0.22f, center = Offset(s * 0.38f, s * 0.58f), style = Stroke(width = s * 0.12f))
    drawCircle(color = brown, radius = s * 0.22f, center = Offset(s * 0.62f, s * 0.58f), style = Stroke(width = s * 0.12f))
    // Top arch
    drawArc(color = lightBrown, startAngle = 200f, sweepAngle = -220f, useCenter = false,
        topLeft = Offset(s * 0.22f, s * 0.18f), size = Size(s * 0.56f, s * 0.56f),
        style = Stroke(width = s * 0.12f))
    // Salt dots
    drawCircle(Color.White, radius = s * 0.025f, center = Offset(s * 0.38f, s * 0.30f))
    drawCircle(Color.White, radius = s * 0.020f, center = Offset(s * 0.55f, s * 0.26f))
    drawCircle(Color.White, radius = s * 0.022f, center = Offset(s * 0.64f, s * 0.38f))
    drawCircle(Color.White, radius = s * 0.020f, center = Offset(s * 0.30f, s * 0.50f))
}

// --- Group 2: European Foods ---

private fun DrawScope.drawPremiumPasta(s: Float) {
    // Bowl
    drawArc(
        brush = Brush.verticalGradient(listOf(Color(0xFFE8EAED), Color(0xFFBDC3C7))),
        startAngle = 0f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(s * 0.14f, s * 0.44f), size = Size(s * 0.72f, s * 0.44f)
    )
    // Pasta noodles (penne-style)
    val yellow = Color(0xFFF5D76E)
    val yellowDark = Color(0xFFD4A520)
    repeat(3) { i ->
        val yOff = s * (0.36f + i * 0.12f)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(yellow, yellowDark), startY = yOff, endY = yOff + s * 0.10f),
            topLeft = Offset(s * 0.22f, yOff),
            size = Size(s * 0.56f, s * 0.10f),
            cornerRadius = CornerRadius(s * 0.03f)
        )
    }
    // Sauce dot
    drawCircle(Color(0xFFE74C3C), radius = s * 0.06f, center = Offset(s * 0.62f, s * 0.40f))
}

private fun DrawScope.drawPremiumGelato(s: Float) {
    // Waffle cone
    val conePath = Path().apply {
        moveTo(s * 0.34f, s * 0.46f)
        lineTo(s * 0.66f, s * 0.46f)
        lineTo(s * 0.50f, s * 0.88f)
        close()
    }
    drawPath(
        brush = Brush.verticalGradient(listOf(Color(0xFFE8B56A), Color(0xFFA0622A)),
            startY = s * 0.46f, endY = s * 0.88f),
        path = conePath
    )
    // Cone grid lines
    repeat(3) { i ->
        drawLine(Color(0xFF8B5E3C).copy(alpha = 0.35f),
            start = Offset(s * 0.36f, s * (0.50f + i * 0.10f)),
            end = Offset(s * (0.64f - i * 0.08f), s * (0.50f + i * 0.10f)),
            strokeWidth = s * 0.018f)
    }
    // Two scoops
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFF8BBD0), Color(0xFFE91E63))),
        radius = s * 0.22f, center = Offset(s * 0.40f, s * 0.34f)
    )
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFA5D6A7), Color(0xFF388E3C))),
        radius = s * 0.20f, center = Offset(s * 0.60f, s * 0.33f)
    )
    // Scoop highlights
    drawCircle(Color.White.copy(alpha = 0.28f), radius = s * 0.065f, center = Offset(s * 0.34f, s * 0.26f))
    drawCircle(Color.White.copy(alpha = 0.24f), radius = s * 0.055f, center = Offset(s * 0.54f, s * 0.25f))
}

private fun DrawScope.drawPremiumBasil(s: Float) {
    // Main leaf (big, centered)
    val mainLeaf = Path().apply {
        moveTo(s * 0.50f, s * 0.82f)
        quadraticTo(s * 0.12f, s * 0.54f, s * 0.50f, s * 0.18f)
        quadraticTo(s * 0.88f, s * 0.54f, s * 0.50f, s * 0.82f)
    }
    drawPath(brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF66BB6A), Color(0xFF388E3C), Color(0xFF1B5E20)),
        startY = s * 0.18f, endY = s * 0.82f
    ), path = mainLeaf)
    // Stem
    drawLine(Color(0xFF2E7D32), start = Offset(s * 0.50f, s * 0.82f), end = Offset(s * 0.50f, s * 0.90f), strokeWidth = s * 0.04f)
    // Vein
    drawLine(Color(0xFF81C784).copy(alpha = 0.55f), start = Offset(s * 0.50f, s * 0.22f), end = Offset(s * 0.50f, s * 0.78f), strokeWidth = s * 0.025f)
    // Side veins
    drawLine(Color(0xFF81C784).copy(alpha = 0.35f), start = Offset(s * 0.50f, s * 0.44f), end = Offset(s * 0.26f, s * 0.54f), strokeWidth = s * 0.018f)
    drawLine(Color(0xFF81C784).copy(alpha = 0.35f), start = Offset(s * 0.50f, s * 0.44f), end = Offset(s * 0.74f, s * 0.54f), strokeWidth = s * 0.018f)
}

private fun DrawScope.drawPremiumSpaghetti(s: Float) {
    // Bowl
    drawArc(
        brush = Brush.verticalGradient(listOf(Color(0xFFECF0F1), Color(0xFFBDC3C7))),
        startAngle = 0f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(s * 0.14f, s * 0.42f), size = Size(s * 0.72f, s * 0.46f)
    )
    // Spaghetti strands (wavy)
    val yellow = Color(0xFFF4D03F)
    repeat(5) { i ->
        val yBase = s * (0.30f + i * 0.08f)
        drawLine(yellow,
            start = Offset(s * 0.20f, yBase),
            end = Offset(s * 0.48f, yBase + s * 0.06f),
            strokeWidth = s * 0.028f
        )
        drawLine(yellow,
            start = Offset(s * 0.48f, yBase + s * 0.06f),
            end = Offset(s * 0.80f, yBase),
            strokeWidth = s * 0.028f
        )
    }
    // Sauce
    drawCircle(Color(0xFFE74C3C), radius = s * 0.08f, center = Offset(s * 0.52f, s * 0.40f))
}

private fun DrawScope.drawPremiumLasagne(s: Float) {
    // Baking dish outline
    drawRoundRect(Color(0xFF7B4A12), topLeft = Offset(s * 0.16f, s * 0.26f), size = Size(s * 0.68f, s * 0.52f), cornerRadius = CornerRadius(s * 0.06f))
    // Pasta layers
    val layers = listOf(
        Color(0xFFF5CBA7),
        Color(0xFFE8A87C),
        Color(0xFFC0392B),
        Color(0xFFFEFEFE),
        Color(0xFF9B4021)
    )
    layers.forEachIndexed { i, color ->
        drawRect(color, topLeft = Offset(s * 0.18f, s * (0.30f + i * 0.08f)), size = Size(s * 0.64f, s * 0.07f))
    }
    // Cheese topping highlight
    drawRoundRect(Color(0xFFF9E79F).copy(alpha = 0.80f), topLeft = Offset(s * 0.18f, s * 0.30f), size = Size(s * 0.64f, s * 0.06f), cornerRadius = CornerRadius(s * 0.02f))
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
    // Sausage body with rich gradient
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF8D4004), Color(0xFF5C2700), Color(0xFF3E1A00)),
            start = Offset(s * 0.10f, s * 0.38f), end = Offset(s * 0.10f, s * 0.62f)
        ),
        topLeft = Offset(s * 0.08f, s * 0.38f), size = Size(s * 0.84f, s * 0.24f),
        cornerRadius = CornerRadius(s * 0.12f)
    )
    // Grill marks
    repeat(3) { i ->
        drawLine(Color.Black.copy(alpha = 0.30f),
            start = Offset(s * (0.26f + i * 0.18f), s * 0.38f),
            end = Offset(s * (0.28f + i * 0.18f), s * 0.62f),
            strokeWidth = s * 0.04f)
    }
    // Top highlight
    drawRoundRect(color = Color(0xFFD4700A).copy(alpha = 0.40f),
        topLeft = Offset(s * 0.10f, s * 0.38f), size = Size(s * 0.80f, s * 0.08f),
        cornerRadius = CornerRadius(s * 0.04f))
}

private fun DrawScope.drawPremiumCake(s: Float) {
    // Cake body (Black Forest)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF4A235A), Color(0xFF1A0A28)),
            startY = s * 0.34f, endY = s * 0.76f
        ),
        topLeft = Offset(s * 0.20f, s * 0.34f), size = Size(s * 0.60f, s * 0.42f),
        cornerRadius = CornerRadius(s * 0.06f)
    )
    // Cream layer
    drawRect(Color.White.copy(alpha = 0.88f), topLeft = Offset(s * 0.20f, s * 0.46f), size = Size(s * 0.60f, s * 0.06f))
    // Dark chocolate layer
    drawRect(Color(0xFF1C0A00), topLeft = Offset(s * 0.20f, s * 0.56f), size = Size(s * 0.60f, s * 0.06f))
    // Cherry on top
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFE53935), Color(0xFF880E2F))), radius = s * 0.09f, center = Offset(s * 0.50f, s * 0.28f))
    // Cherry stem
    drawLine(Color(0xFF2E7D32), start = Offset(s * 0.50f, s * 0.20f), end = Offset(s * 0.50f, s * 0.34f), strokeWidth = s * 0.025f)
    // Cherry highlight
    drawCircle(Color.White.copy(alpha = 0.30f), radius = s * 0.03f, center = Offset(s * 0.46f, s * 0.24f))
}

// --- Group 3: France & Japan ---

private fun DrawScope.drawPremiumCroissant(s: Float) {
    // Crescent main body
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFE082), Color(0xFFFFAB00), Color(0xFFE65100)),
            start = Offset(s * 0.20f, s * 0.30f), end = Offset(s * 0.80f, s * 0.70f)
        ),
        topLeft = Offset(s * 0.14f, s * 0.30f), size = Size(s * 0.72f, s * 0.38f),
        cornerRadius = CornerRadius(s * 0.19f)
    )
    // Score / layering lines
    repeat(4) { i ->
        drawLine(Color(0xFFBF360C).copy(alpha = 0.30f),
            start = Offset(s * (0.22f + i * 0.14f), s * 0.30f),
            end = Offset(s * (0.20f + i * 0.14f), s * 0.68f),
            strokeWidth = s * 0.022f)
    }
    // Top highlight
    drawOval(color = Color.White.copy(alpha = 0.24f), topLeft = Offset(s * 0.22f, s * 0.30f), size = Size(s * 0.40f, s * 0.12f))
}

private fun DrawScope.drawPremiumBaguette(s: Float) {
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFE0B2), Color(0xFFD2691E), Color(0xFF8D4004)),
            start = Offset(s * 0.10f, s * 0.38f), end = Offset(s * 0.10f, s * 0.62f)
        ),
        topLeft = Offset(s * 0.08f, s * 0.38f), size = Size(s * 0.84f, s * 0.24f),
        cornerRadius = CornerRadius(s * 0.08f)
    )
    // Score cuts on top
    repeat(4) { i ->
        drawLine(Color(0xFF5D3A1A).copy(alpha = 0.45f),
            start = Offset(s * (0.22f + i * 0.16f), s * 0.38f),
            end = Offset(s * (0.28f + i * 0.16f), s * 0.50f),
            strokeWidth = s * 0.025f)
    }
    // Top highlight
    drawRoundRect(Color.White.copy(alpha = 0.20f), topLeft = Offset(s * 0.10f, s * 0.38f), size = Size(s * 0.80f, s * 0.07f), cornerRadius = CornerRadius(s * 0.03f))
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
    val pink = Color(0xFFF48FB1)
    val pinkDark = Color(0xFFE91E63)
    // Bottom shell
    drawRoundRect(
        brush = Brush.verticalGradient(colors = listOf(pink, pinkDark), startY = s * 0.50f, endY = s * 0.68f),
        topLeft = Offset(s * 0.22f, s * 0.50f), size = Size(s * 0.56f, s * 0.20f),
        cornerRadius = CornerRadius(s * 0.08f)
    )
    // Filling
    drawRoundRect(Color(0xFFFFF9C4), topLeft = Offset(s * 0.22f, s * 0.46f), size = Size(s * 0.56f, s * 0.07f), cornerRadius = CornerRadius(s * 0.02f))
    // Top shell
    drawRoundRect(
        brush = Brush.verticalGradient(colors = listOf(Color(0xFFFCE4EC), pink), startY = s * 0.28f, endY = s * 0.48f),
        topLeft = Offset(s * 0.22f, s * 0.28f), size = Size(s * 0.56f, s * 0.20f),
        cornerRadius = CornerRadius(s * 0.08f)
    )
    // Top shell highlight
    drawOval(color = Color.White.copy(alpha = 0.30f), topLeft = Offset(s * 0.30f, s * 0.29f), size = Size(s * 0.22f, s * 0.07f))
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
    // Bowl body
    drawArc(
        brush = Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFB0BEC5))),
        startAngle = 0f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(s * 0.12f, s * 0.40f), size = Size(s * 0.76f, s * 0.52f)
    )
    // Broth
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFCCBC), Color(0xFFE64A19)),
            center = Offset(s * 0.5f, s * 0.5f), radius = s * 0.38f
        ),
        topLeft = Offset(s * 0.14f, s * 0.30f), size = Size(s * 0.72f, s * 0.42f)
    )
    // Noodles
    repeat(3) { i ->
        drawLine(Color(0xFFF9E79F),
            start = Offset(s * 0.20f, s * (0.40f + i * 0.07f)),
            end = Offset(s * 0.80f, s * (0.38f + i * 0.07f)),
            strokeWidth = s * 0.026f)
    }
    // Egg halve
    drawCircle(Color(0xFFFFF176), radius = s * 0.09f, center = Offset(s * 0.66f, s * 0.42f))
    drawCircle(Color(0xFFF57F17), radius = s * 0.055f, center = Offset(s * 0.66f, s * 0.42f))
    // Nori slice
    drawRect(Color(0xFF1B3A2D), topLeft = Offset(s * 0.22f, s * 0.34f), size = Size(s * 0.10f, s * 0.22f))
}

private fun DrawScope.drawPremiumTempura(s: Float) {
    drawRoundRect(Color(0xFFF39C12), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.3f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumOnigiri(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.50f, s * 0.16f)
        lineTo(s * 0.82f, s * 0.72f)
        quadraticTo(s * 0.50f, s * 0.80f, s * 0.18f, s * 0.72f)
        close()
    }
    drawPath(
        brush = Brush.verticalGradient(listOf(Color(0xFFF8F8F2), Color(0xFFE0E0D8)),
            startY = s * 0.16f, endY = s * 0.80f),
        path = path
    )
    // Nori band
    drawRoundRect(Color(0xFF1B3A2D), topLeft = Offset(s * 0.34f, s * 0.60f), size = Size(s * 0.32f, s * 0.18f), cornerRadius = CornerRadius(s * 0.03f))
    // Sesame seeds
    drawCircle(Color(0xFFD4AC0D), radius = s * 0.020f, center = Offset(s * 0.38f, s * 0.44f))
    drawCircle(Color(0xFFD4AC0D), radius = s * 0.018f, center = Offset(s * 0.56f, s * 0.38f))
    // Edge highlight
    drawPath(path = path, color = Color.White.copy(alpha = 0.20f), style = Stroke(width = s * 0.022f))
}

private fun DrawScope.drawPremiumMochi(s: Float) {
    val colors = listOf(Color(0xFFF48FB1), Color(0xFFFFFFFF), Color(0xFFC5E1A5))
    val centerXs = listOf(s * 0.30f, s * 0.50f, s * 0.70f)
    colors.forEachIndexed { i, color ->
        // Shadow under each ball
        drawCircle(Color.Black.copy(alpha = 0.14f), radius = s * 0.13f, center = Offset(centerXs[i] + s * 0.02f, s * 0.56f))
        // Ball body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(red = (color.red + 0.2f).coerceAtMost(1f), green = (color.green + 0.2f).coerceAtMost(1f), blue = (color.blue + 0.2f).coerceAtMost(1f), alpha = 0.90f),
                    color
                ),
                center = Offset(centerXs[i] - s * 0.04f, s * 0.40f), radius = s * 0.14f
            ),
            radius = s * 0.13f, center = Offset(centerXs[i], s * 0.50f)
        )
        // Highlight
        drawCircle(Color.White.copy(alpha = 0.28f), radius = s * 0.04f, center = Offset(centerXs[i] - s * 0.05f, s * 0.42f))
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
    // Cup / bowl
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF388E3C), Color(0xFF1B5E20)),
            startY = s * 0.36f, endY = s * 0.74f
        ),
        topLeft = Offset(s * 0.26f, s * 0.36f), size = Size(s * 0.48f, s * 0.38f),
        cornerRadius = CornerRadius(s * 0.08f)
    )
    // Froth swirl
    drawCircle(Color(0xFFA5D6A7).copy(alpha = 0.70f), radius = s * 0.14f, center = Offset(s * 0.50f, s * 0.48f))
    drawCircle(Color(0xFFC8E6C9).copy(alpha = 0.50f), radius = s * 0.07f, center = Offset(s * 0.50f, s * 0.46f))
    // Cup rim highlight
    drawRoundRect(Color.White.copy(alpha = 0.22f), topLeft = Offset(s * 0.26f, s * 0.36f), size = Size(s * 0.48f, s * 0.06f), cornerRadius = CornerRadius(s * 0.04f))
}

private fun DrawScope.drawPremiumDorayaki(s: Float) {
    drawOval(Color(0xFFD35400), topLeft = Offset(s * 0.25f, s * 0.35f), size = Size(s * 0.5f, s * 0.2f))
    drawOval(Color(0xFFD35400), topLeft = Offset(s * 0.25f, s * 0.5f), size = Size(s * 0.5f, s * 0.2f))
}

// --- Group 4: Mexico & Sudan ---

private fun DrawScope.drawPremiumTaco(s: Float) {
    // Shell
    val shellPath = Path().apply {
        moveTo(s * 0.08f, s * 0.72f)
        quadraticTo(s * 0.18f, s * 0.32f, s * 0.50f, s * 0.28f)
        quadraticTo(s * 0.82f, s * 0.32f, s * 0.92f, s * 0.72f)
        close()
    }
    drawPath(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFE082), Color(0xFFFFA000), Color(0xFFE65100)),
            startY = s * 0.28f, endY = s * 0.72f
        ),
        path = shellPath
    )
    // Lettuce
    drawOval(Color(0xFF66BB6A), topLeft = Offset(s * 0.22f, s * 0.42f), size = Size(s * 0.56f, s * 0.14f))
    // Meat
    drawOval(Color(0xFF6D4C41), topLeft = Offset(s * 0.26f, s * 0.50f), size = Size(s * 0.48f, s * 0.12f))
    // Tomato
    drawCircle(Color(0xFFE53935), radius = s * 0.055f, center = Offset(s * 0.42f, s * 0.48f))
    drawCircle(Color(0xFFE53935), radius = s * 0.055f, center = Offset(s * 0.60f, s * 0.50f))
    // Shell highlight
    drawPath(path = shellPath, color = Color.White.copy(alpha = 0.15f), style = Stroke(width = s * 0.022f))
}

private fun DrawScope.drawPremiumBurrito(s: Float) {
    drawRoundRect(Color(0xFFF5CBA7), topLeft = Offset(s * 0.2f, s * 0.4f), size = Size(s * 0.6f, s * 0.2f), cornerRadius = CornerRadius(s * 0.1f))
}

private fun DrawScope.drawPremiumGuacamole(s: Float) {
    // Avocado body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF388E3C), Color(0xFF1B5E20)),
            center = Offset(s * 0.44f, s * 0.46f), radius = s * 0.38f
        ),
        radius = s * 0.36f
    )
    // Guac inside (lighter)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFA5D6A7), Color(0xFF66BB6A)),
            center = Offset(s * 0.46f, s * 0.48f), radius = s * 0.22f
        ),
        radius = s * 0.22f, center = Offset(s * 0.50f, s * 0.52f)
    )
    // Pit
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFF795548), Color(0xFF3E2723))),
        radius = s * 0.09f, center = Offset(s * 0.52f, s * 0.54f)
    )
    // Rim highlight
    drawCircle(color = Color.White.copy(alpha = 0.18f), radius = s * 0.35f, style = Stroke(width = s * 0.025f))
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

private fun DrawScope.drawPremiumPaella(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFF7DC6F), Color(0xFFE67E22))), radius = s * 0.38f)
    drawCircle(Color(0xFF7D6608), radius = s * 0.3f, style = Stroke(width = s * 0.06f))
    drawCircle(Color(0xFF1E8449), radius = s * 0.05f, center = Offset(s * 0.38f, s * 0.42f))
    drawCircle(Color(0xFFC0392B), radius = s * 0.05f, center = Offset(s * 0.62f, s * 0.52f))
}

private fun DrawScope.drawPremiumTortillaEspanola(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFF8C471), Color(0xFFD68910))), radius = s * 0.34f)
    drawCircle(Color(0xFF7B4A12), radius = s * 0.08f, center = Offset(s * 0.35f, s * 0.46f))
    drawCircle(Color(0xFF7B4A12), radius = s * 0.08f, center = Offset(s * 0.6f, s * 0.42f))
}

private fun DrawScope.drawPremiumJamonIberico(s: Float) {
    val path = Path().apply {
        moveTo(s * 0.28f, s * 0.22f)
        lineTo(s * 0.68f, s * 0.32f)
        lineTo(s * 0.62f, s * 0.72f)
        lineTo(s * 0.34f, s * 0.82f)
        close()
    }
    drawPath(path, brush = Brush.linearGradient(listOf(Color(0xFFA93226), Color(0xFF641E16))))
    drawCircle(Color(0xFFE59866), radius = s * 0.06f, center = Offset(s * 0.55f, s * 0.42f))
}

private fun DrawScope.drawPremiumGazpacho(s: Float) {
    drawCircle(Color(0xFFEC7063), radius = s * 0.35f)
    drawCircle(Color(0xFFFDEBD0), radius = s * 0.22f)
    drawCircle(Color(0xFF27AE60), radius = s * 0.04f, center = Offset(s * 0.42f, s * 0.46f))
}

private fun DrawScope.drawPremiumCroquetas(s: Float) {
    drawRoundRect(brush = Brush.linearGradient(listOf(Color(0xFFF9E79F), Color(0xFFD4AC0D))), topLeft = Offset(s * 0.22f, s * 0.35f), size = Size(s * 0.56f, s * 0.25f), cornerRadius = CornerRadius(s * 0.12f))
    drawCircle(Color(0xFF7B4A12), radius = s * 0.03f, center = Offset(s * 0.42f, s * 0.47f))
}

private fun DrawScope.drawPremiumPatatasBravas(s: Float) {
    drawRect(Color(0xFFE1B12C), topLeft = Offset(s * 0.24f, s * 0.28f), size = Size(s * 0.18f, s * 0.22f))
    drawRect(Color(0xFFE1B12C), topLeft = Offset(s * 0.44f, s * 0.38f), size = Size(s * 0.18f, s * 0.24f))
    drawRect(Color(0xFFE1B12C), topLeft = Offset(s * 0.62f, s * 0.28f), size = Size(s * 0.14f, s * 0.22f))
    drawCircle(Color(0xFFC0392B), radius = s * 0.1f, center = Offset(s * 0.5f, s * 0.6f))
}

private fun DrawScope.drawPremiumPulpo(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFF9B59B6), Color(0xFF6C3483))), radius = s * 0.28f, center = Offset(s * 0.5f, s * 0.45f))
    repeat(4) { i ->
        drawRoundRect(
            color = Color(0xFF7D3C98),
            topLeft = Offset(s * (0.32f + i * 0.09f), s * 0.55f),
            size = Size(s * 0.05f, s * 0.28f),
            cornerRadius = CornerRadius(s * 0.03f)
        )
    }
}

private fun DrawScope.drawPremiumSangria(s: Float) {
    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFB03A2E), Color(0xFF641E16))), radius = s * 0.34f)
    drawCircle(Color(0xFFF7DC6F), radius = s * 0.05f, center = Offset(s * 0.38f, s * 0.35f))
    drawCircle(Color(0xFF7DCEA0), radius = s * 0.04f, center = Offset(s * 0.6f, s * 0.56f))
}

private fun DrawScope.drawPremiumCremaCatalana(s: Float) {
    drawRoundRect(brush = Brush.verticalGradient(listOf(Color(0xFFF9E79F), Color(0xFFF5CBA7))), topLeft = Offset(s * 0.22f, s * 0.34f), size = Size(s * 0.56f, s * 0.28f), cornerRadius = CornerRadius(s * 0.08f))
    drawRect(Color(0xFFB03A2E), topLeft = Offset(s * 0.22f, s * 0.28f), size = Size(s * 0.56f, s * 0.06f))
}

// --- Shared Helpers ---

private fun DrawScope.drawPremiumGloss(s: Float) {
    // Primary specular arc — mimics plastic/candy-coat sheen.
    drawArc(
        color = Color.White.copy(alpha = 0.45f),
        startAngle = -130f,
        sweepAngle = 65f,
        useCenter = false,
        topLeft = Offset(s * 0.15f, s * 0.10f),
        size = Size(s * 0.70f, s * 0.70f),
        style = Stroke(width = s * 0.055f)
    )
    // Soft oval highlight dot at top-left (candy-gloss diffuse).
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.42f), Color.Transparent),
            center = Offset(s * 0.30f, s * 0.22f),
            radius = s * 0.15f
        ),
        topLeft = Offset(s * 0.16f, s * 0.12f),
        size = Size(s * 0.26f, s * 0.18f)
    )
}
