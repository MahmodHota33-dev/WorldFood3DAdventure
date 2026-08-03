package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.CountryDefinition
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryMetadata
import com.mahmodhota.worldfood3dadventure.game.world.germany.GermanyMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.italy.ItalyMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.france.FranceMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.japan.JapanMatch3Levels
import com.mahmodhota.worldfood3dadventure.game.world.mexico.MexicoMatch3Levels
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
            metadata = CountryMetadata("italy", "Italy", "IT", "🇮🇹", "Discover the Mediterranean soul."),
            levels = ItalyMatch3Levels.levels.map { it.withBalancedThresholds() }
        ))

        // France
        register(CountryDefinition(
            id = "france",
            metadata = CountryMetadata("france", "France", "FR", "🇫🇷", "Indulge in elegant pastries."),
            levels = FranceMatch3Levels.levels.map { it.withBalancedThresholds() }
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

    val allCountries: List<CountryMetadata> get() = registry.values.map { it.metadata }
    
    val allCountryIds: List<String> get() = registry.keys.toList()
}
