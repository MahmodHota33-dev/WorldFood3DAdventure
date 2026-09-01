package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import com.mahmodhota.worldfood3dadventure.ui.match3.components.*

@Composable
fun FoodCollectionScreen(
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    var selectedContinent by remember { mutableStateOf<Continent?>(null) }
    var selectedFoodDetail by remember { mutableStateOf<FoodDetailData?>(null) }
    
    val allCountries = remember(gameState) {
        LevelRegistry.allCountryIds.mapNotNull { id ->
            val def = LevelRegistry.getCountry(id) ?: return@mapNotNull null
            val progress = gameState.countries[id]
            val foods = LevelRegistry.getRepresentativeFoods(id)
            val discovered = progress?.discoveredFoods ?: emptySet()
            
            CountryFoodData(
                id = id,
                name = def.metadata.displayName,
                flag = def.metadata.flagEmoji,
                continent = def.metadata.continent,
                foods = foods,
                discoveredCount = discovered.size,
                discoveredNames = discovered,
                isUnlocked = progress?.isUnlocked ?: false
            )
        }
    }

    val filteredCountries = remember(allCountries, selectedContinent) {
        if (selectedContinent == null) allCountries
        else allCountries.filter { it.continent == selectedContinent }
    }

    val totalDiscovered = allCountries.sumOf { it.discoveredCount }
    val totalFoods = allCountries.sumOf { it.foods.size }.coerceAtLeast(1)
    val globalProgress = totalDiscovered.toFloat() / totalFoods.toFloat()

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        bottomBar = { BottomNavigationBar(currentTab = "collection", onTabSelected = onTabSelected) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenWidth = maxWidth
                val columns = when {
                    screenWidth < 360.dp -> 2
                    screenWidth < 430.dp -> 3
                    else -> 4
                }

                PremiumGameBackdrop()
                
                Column(modifier = Modifier.fillMaxSize()) {
                    // P10-S: Refined Premium Header
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .premiumPanel(),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            Brush.radialGradient(listOf(PremiumColors.Gold.copy(alpha = 0.2f), Color.Transparent)),
                                            CircleShape
                                        )
                                        .border(1.5.dp, PremiumColors.Gold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🍲", fontSize = 34.sp)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "CULINARY DISCOVERY", 
                                        color = PremiumColors.Gold, 
                                        fontSize = 20.sp, 
                                        fontWeight = FontWeight.Black, 
                                        letterSpacing = 1.2.sp
                                    )
                                    Text(
                                        "WORLD COLLECTION • BOOK I", 
                                        color = Color.White.copy(alpha = 0.5f), 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(20.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "GLOBAL PROGRESS",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "$totalDiscovered / $totalFoods ITEMS",
                                    color = PremiumColors.Gold, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(globalProgress.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .background(Brush.horizontalGradient(listOf(PremiumColors.Gold, Color(0xFFFFA000))))
                                )
                            }
                        }
                    }

                    // Continent Filter (Unified styling)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            label = "ALL",
                            isSelected = selectedContinent == null,
                            onClick = { selectedContinent = null }
                        )
                        Continent.entries.forEach { continent ->
                            FilterChip(
                                label = continent.displayName.uppercase(),
                                isSelected = selectedContinent == continent,
                                onClick = { selectedContinent = continent }
                            )
                        }
                    }

                    // Grid of Countries
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredCountries, key = { it.id }) { data ->
                            CountryFoodSection(data, columns) { food ->
                                selectedFoodDetail = FoodDetailData(food, data.name, data.flag, data.discoveredNames.contains(food.name))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedFoodDetail != null) {
        FoodDetailDialog(
            data = selectedFoodDetail!!,
            onDismiss = { selectedFoodDetail = null }
        )
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = {
            GlobalSystemManager.audio.playSfx(SfxType.UI_FILTER_SELECT)
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PremiumColors.Gold else Color.White.copy(alpha = 0.05f),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (isSelected) PremiumColors.DeepNavy else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun CountryFoodSection(data: CountryFoodData, columns: Int, onFoodClick: (FoodTileType) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(data.flag, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    data.name.uppercase(), 
                    color = Color.White, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "${data.continent.displayName.uppercase()} • ${data.discoveredCount}/${data.foods.size} DISCOVERED",
                    color = PremiumColors.Gold.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // P10-AJ: Explicitly use calculated columns
        val rowCount = (data.foods.size + columns - 1) / columns
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(rowCount) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(columns) { colIndex ->
                        val itemIndex = rowIndex * columns + colIndex
                        if (itemIndex < data.foods.size) {
                            val food = data.foods[itemIndex]
                            val isFound = data.discoveredNames.contains(food.name)
                            FoodDiscoveryCard(
                                food = food,
                                isDiscovered = isFound,
                                onClick = { onFoodClick(food) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodDiscoveryCard(
    food: FoodTileType,
    isDiscovered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f), 
        shape = RoundedCornerShape(16.dp),
        color = if (isDiscovered) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isDiscovered) 1.5.dp else 1.dp,
            color = if (isDiscovered) PremiumColors.Gold.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)
        ),
        tonalElevation = if (isDiscovered) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isDiscovered) {
                FoodIcon(type = food, size = 38.dp)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(listOf(PremiumColors.Gold.copy(alpha = 0.05f), Color.Transparent))
                        )
                )
            } else {
                Text(
                    "🔒", 
                    fontSize = 18.sp, 
                    modifier = Modifier.graphicsLayer { alpha = 0.3f }
                )
            }
        }
    }
}

@Composable
private fun FoodDetailDialog(
    data: FoodDetailData,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(32.dp)
                .widthIn(max = 340.dp)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(32.dp),
            color = PremiumColors.DeepNavy,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PremiumColors.Gold.copy(alpha = 0.6f)),
            tonalElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (data.isDiscovered) "DISCOVERED ITEM" else "MYSTERY DISCOVERY",
                    color = if (data.isDiscovered) PremiumColors.Gold else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                
                Spacer(Modifier.height(24.dp))
                
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.size(100.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (data.isDiscovered) {
                            FoodIcon(type = data.food, size = 64.dp)
                        } else {
                            Text("🔒", fontSize = 42.sp, modifier = Modifier.graphicsLayer { alpha = 0.25f })
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = if (data.isDiscovered) data.food.name.replace('_', ' ') else "???",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(data.flag, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = data.countryName.uppercase(),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(Modifier.height(28.dp))
                
                Text(
                    text = if (data.isDiscovered) {
                        "This culinary treasure has been added to your collection. Travel to more countries to discover local flavors!"
                    } else {
                        "Play levels in ${data.countryName} to unlock this item and complete the collection."
                    },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (data.isDiscovered) PremiumColors.Gold else Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "CLOSE", 
                        color = if (data.isDiscovered) PremiumColors.DeepNavy else Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

private data class FoodDetailData(
    val food: FoodTileType,
    val countryName: String,
    val flag: String,
    val isDiscovered: Boolean
)

private data class CountryFoodData(
    val id: String,
    val name: String,
    val flag: String,
    val continent: Continent,
    val foods: List<FoodTileType>,
    val discoveredCount: Int,
    val discoveredNames: Set<String>,
    val isUnlocked: Boolean
)
