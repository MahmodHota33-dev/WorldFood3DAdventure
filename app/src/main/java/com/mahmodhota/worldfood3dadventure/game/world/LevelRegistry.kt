package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.CountryDefinition
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryMetadata
import com.mahmodhota.worldfood3dadventure.game.world.germany.GermanyMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.world.france.FranceFoodBookEntries
import com.mahmodhota.worldfood3dadventure.game.world.france.FranceMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.italy.ItalyMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.japan.JapanMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.mexico.MexicoMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.spain.SpainFoodBookEntries
import com.mahmodhota.worldfood3dadventure.game.world.spain.SpainMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.sudan.SudanMatch3Levels

/**
 * Centralized registry for all countries and their levels.
 */
object LevelRegistry {
    
    private val registry = mutableMapOf<String, CountryDefinition>()

    init {
        registerBuiltInCountries()
    }

    private fun registerBuiltInCountries() {
        // Germany
        register(CountryDefinition(
            id = "germany",
            metadata = CountryMetadata("germany", "Germany", "DE", "🇩🇪", "Explore rustic villages and hearty cuisine."),
            levels = GermanyMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Italy
        register(CountryDefinition(
            id = "italy",
            metadata = CountryMetadata(
                "italy",
                "Italy",
                "IT",
                "🇮🇹",
                "From Rome to the Amalfi Coast, savor timeless flavors and café culture."
            ),
            levels = ItalyMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // France
        register(CountryDefinition(
            id = "france",
            metadata = CountryMetadata(
                "france",
                "France",
                "FR",
                "🇫🇷",
                "Explore the elegance of France, famous for its cuisine, culture, cafés and world-famous landmarks."
            ),
            levels = FranceMatch3Levels.levels.map { it.withBalancedThresholds() },
            foodEntries = FranceFoodBookEntries.entries.map {
                com.mahmodhota.worldfood3dadventure.game.world.model.UnifiedFoodEntry(
                    id = it.id,
                    name = it.name,
                    country = it.country,
                    description = it.description
                )
            }
        ))

        register(CountryDefinition(
            id = "spain",
            metadata = CountryMetadata(
                "spain",
                "Spain",
                "ES",
                "🇪🇸",
                "Explore sunlit plazas, coastlines, and a premium tapas journey across Spain."
            ),
            levels = SpainMatch3Levels.levels.map { it.withBalancedThresholds() },
            foodEntries = SpainFoodBookEntries.entries.map {
                com.mahmodhota.worldfood3dadventure.game.world.model.UnifiedFoodEntry(
                    id = it.id,
                    name = it.name,
                    country = it.country,
                    description = it.description
                )
            }
        ))

        // Japan
        register(CountryDefinition(
            id = "japan",
            metadata = CountryMetadata("japan", "Japan", "JP", "🇯🇵", "Experience the zen of Japanese cuisine."),
            levels = JapanMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Mexico
        register(CountryDefinition(
            id = "mexico",
            metadata = CountryMetadata("mexico", "Mexico", "MX", "🇲🇽", "A vibrant fiesta of flavors."),
            levels = MexicoMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Sudan
        register(CountryDefinition(
            id = "sudan",
            metadata = CountryMetadata("sudan", "Sudan", "SD", "🇸🇩", "Discover the ancient flavors of the Nile."),
            levels = SudanMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))
    }

    fun register(definition: CountryDefinition) {
        registry[definition.id] = definition
    }

    fun getCountry(id: String): CountryDefinition? = registry[id]

    /** Presentation-layer helper: 3 representative food icons per country. */
    fun getRepresentativeFoods(countryId: String): List<FoodTileType> = when(countryId) {
        "germany" -> listOf(FoodTileType.PRETZEL, FoodTileType.BRATWURST, FoodTileType.BLACK_FOREST_CAKE)
        "italy"   -> listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.GELATO)
        "france"  -> listOf(FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.CREPE)
        "spain"   -> listOf(FoodTileType.PAELLA, FoodTileType.CHURROS, FoodTileType.CREMA_CATALANA)
        "japan"   -> listOf(FoodTileType.SUSHI, FoodTileType.RAMEN, FoodTileType.MOCHI)
        "mexico"  -> listOf(FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.NACHOS)
        "sudan"   -> listOf(FoodTileType.KISRA, FoodTileType.FUL_MEDAMES, FoodTileType.SAMBUSA)
        else -> emptyList()
    }

    val allCountries: List<CountryMetadata> get() = COUNTRY_PROGRESSION_ORDER.mapNotNull { registry[it]?.metadata }

    /**
     * Authoritative ordered list of country IDs matching the progression chain.
     * Using an explicit list rather than registry insertion order makes the sequence
     * stable regardless of future registration order changes.
     */
    val allCountryIds: List<String>
        get() = COUNTRY_PROGRESSION_ORDER.filter { it in registry }
}

/** Canonical progression order — must match CountryProgressionChain.UNLOCK_ORDER. */
val COUNTRY_PROGRESSION_ORDER = listOf("germany", "italy", "france", "spain", "japan", "mexico", "sudan")
