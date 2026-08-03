package com.mahmodhota.worldfood3dadventure.data.audio

import android.content.Context

/**
 * Categorizes all game sound effects.
 */
enum class SfxType {
    BUTTON_CLICK,
    TILE_SELECT,
    SWAP_VALID,
    SWAP_INVALID,
    MATCH_SMALL,
    MATCH_LARGE,
    COMBO_1,
    COMBO_2,
    COMBO_3,
    CASCADE,
    COIN_COLLECT,
    STAR_EARNED,
    XP_GAINED,
    LEVEL_UNLOCK,
    COUNTRY_UNLOCK,
    VICTORY,
    DEFEAT,
    ITALY_VICTORY,
    JAPAN_VICTORY,
    MEXICO_VICTORY
}

/**
 * Categorizes background music tracks.
 */
enum class MusicType {
    WORLD_MAP,
    GERMANY_THEME,
    ITALY_THEME,
    FRANCE_THEME,
    JAPAN_THEME,
    MEXICO_THEME,
    NONE
}

/**
 * Resolves sound resources by enum name from res/raw at runtime.
 * Example: SfxType.BUTTON_CLICK -> res/raw/button_click.*
 */
object SoundRepository {

    private fun resolveRawResourceId(context: Context, rawName: String): Int? {
        val id = context.resources.getIdentifier(rawName, "raw", context.packageName)
        return if (id == 0) null else id
    }

    fun getSfxResId(context: Context, type: SfxType): Int? {
        return resolveRawResourceId(context, type.name.lowercase())
    }

    fun getMusicResId(context: Context, type: MusicType): Int? {
        if (type == MusicType.NONE) return null
        return resolveRawResourceId(context, type.name.lowercase())
    }

    fun getMusicForCountry(countryId: String): MusicType {
        return when (countryId.lowercase()) {
            "germany" -> MusicType.GERMANY_THEME
            "italy" -> MusicType.ITALY_THEME
            "france" -> MusicType.FRANCE_THEME
            "japan" -> MusicType.JAPAN_THEME
            "mexico" -> MusicType.MEXICO_THEME
            else -> MusicType.WORLD_MAP
        }
    }
}
