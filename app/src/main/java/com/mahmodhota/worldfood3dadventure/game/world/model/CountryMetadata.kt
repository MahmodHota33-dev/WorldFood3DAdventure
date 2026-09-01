package com.mahmodhota.worldfood3dadventure.game.world.model

import com.mahmodhota.worldfood3dadventure.game.world.model.Continent

/**
 * Pure data model for country metadata.
 */
data class CountryMetadata(
    val levelId: String,
    val displayName: String,
    val countryCode: String,
    val flagEmoji: String,
    val travelDescription: String = "",
    val continent: Continent = Continent.EUROPE,
    val unlockedInitially: Boolean = false,
    val comingSoonText: String? = null
)
