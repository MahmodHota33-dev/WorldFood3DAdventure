package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
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
    private val extraMetadata = mutableMapOf<String, CountryMetadata>()

    init {
        registerBuiltInCountries()
    }

    private fun registerBuiltInCountries() {
        // Germany
        register(CountryDefinition(
            id = "germany",
            metadata = CountryMetadata("germany", "Germany", "DE", "🇩🇪", "Explore rustic villages and hearty cuisine.", Continent.EUROPE, unlockedInitially = true),
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
                "From Rome to the Amalfi Coast, savor timeless flavors and café culture.",
                Continent.EUROPE,
                unlockedInitially = false
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
                "Explore the elegance of France, famous for its cuisine, culture, cafés and world-famous landmarks.",
                Continent.EUROPE,
                unlockedInitially = false
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
                "Explore sunlit plazas, coastlines, and a premium tapas journey across Spain.",
                Continent.EUROPE,
                unlockedInitially = false
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
            metadata = CountryMetadata("japan", "Japan", "JP", "🇯🇵", "Experience the zen of Japanese cuisine.", Continent.ASIA, unlockedInitially = false),
            levels = JapanMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Mexico
        register(CountryDefinition(
            id = "mexico",
            metadata = CountryMetadata("mexico", "Mexico", "MX", "🇲🇽", "A vibrant fiesta of flavors.", Continent.NORTH_AMERICA, unlockedInitially = false),
            levels = MexicoMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Sudan
        register(CountryDefinition(
            id = "sudan",
            metadata = CountryMetadata("sudan", "Sudan", "SD", "🇸🇩", "Discover the ancient flavors of the Nile.", Continent.AFRICA, unlockedInitially = false),
            levels = SudanMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // Expanded Global Content - DEFERRED REGISTRATION
        GlobalContentRegistry.EXTRA_COUNTRIES.forEach { metadata ->
            extraMetadata[metadata.levelId] = metadata
        }
    }

    fun register(definition: CountryDefinition) {
        registry[definition.id] = definition
    }

    fun getCountry(id: String): CountryDefinition? {
        val core = registry[id]
        if (core != null) return core
        
        val meta = extraMetadata[id] ?: return null
        
        // On-demand generation to save startup time
        val foods = GlobalContentRegistry.getExtraFoods(id)
        val definition = CountryDefinition(
            id = id,
            metadata = meta,
            levels = GlobalContentRegistry.generateLevels(id, foods).map { it.withBalancedThresholds() }
        )
        // Cache it
        registry[id] = definition
        return definition
    }

    /** Presentation-layer helper: land-specific food identity per country. */
    fun getRepresentativeFoods(countryId: String): List<FoodTileType> = when(countryId) {
        "germany" -> listOf(FoodTileType.PRETZEL, FoodTileType.BRATWURST, FoodTileType.BLACK_FOREST_CAKE, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.APPLE)
        "italy"   -> listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.GELATO, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.SPAGHETTI, FoodTileType.TIRAMISU)
        "france"  -> listOf(FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.CREPE, FoodTileType.FRENCH_CHEESE, FoodTileType.BAGUETTE, FoodTileType.RATATOUILLE, FoodTileType.SOUFFLE, FoodTileType.ECLAIR)
        "spain"   -> listOf(FoodTileType.PAELLA, FoodTileType.TORTILLA_ESPANOLA, FoodTileType.JAMON_IBERICO, FoodTileType.GAZPACHO, FoodTileType.PATATAS_BRAVAS, FoodTileType.PULPO_A_LA_GALLEGA, FoodTileType.CHURROS, FoodTileType.CREMA_CATALANA)
        "japan"   -> listOf(FoodTileType.SUSHI, FoodTileType.RAMEN, FoodTileType.MOCHI, FoodTileType.TEMPURA, FoodTileType.ONIGIRI, FoodTileType.TAKOYAKI, FoodTileType.UDON, FoodTileType.MATCHA)
        "mexico"  -> listOf(FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.NACHOS, FoodTileType.BURRITO, FoodTileType.CHILI, FoodTileType.TAMALE, FoodTileType.QUESADILLA, FoodTileType.POZOLE)
        "sudan"   -> listOf(FoodTileType.KISRA, FoodTileType.FUL_MEDAMES, FoodTileType.SAMBUSA, FoodTileType.MULAH, FoodTileType.TAGALIA, FoodTileType.AGASHE, FoodTileType.SHAWAYA, FoodTileType.ASIDA)
        else -> GlobalContentRegistry.getExtraFoods(countryId)
    }

    private val cachedAllCountryIds: List<String> by lazy {
        (COUNTRY_PROGRESSION_ORDER + GlobalContentRegistry.EXTRA_COUNTRIES.map { it.levelId }).distinct()
    }

    val allCountryIds: List<String> get() = cachedAllCountryIds

    private val cachedAllCountries: List<CountryMetadata> by lazy {
        allCountryIds.mapNotNull { getCountryMetadata(it) }
    }

    val allCountries: List<CountryMetadata> get() = cachedAllCountries

    private fun getCountryMetadata(id: String): CountryMetadata? {
        return registry[id]?.metadata ?: extraMetadata[id]
    }
}

/** Canonical progression order — must match CountryProgressionChain.UNLOCK_ORDER. */
val COUNTRY_PROGRESSION_ORDER = listOf("germany", "italy", "france", "spain", "japan", "mexico", "sudan")
