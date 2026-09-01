package com.mahmodhota.worldfood3dadventure.data.progress.model

/**
 * Persisted progress for an entire country.
 */
data class CountryGameProgress(
    val countryId: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val levels: Map<Int, LevelProgress> = emptyMap(),
    val discoveredFoods: Set<String> = emptySet()
)
