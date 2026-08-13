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
        if (id == 0) {
            android.util.Log.w("SoundRepository", "Audio resource missing: res/raw/$rawName")
            return null
        }
        return id
    }

    fun getSfxResId(context: Context, type: SfxType): Int? {
        val rid = when(type) {
            SfxType.BUTTON_CLICK -> com.mahmodhota.worldfood3dadventure.R.raw.button_click
            SfxType.TILE_SELECT -> com.mahmodhota.worldfood3dadventure.R.raw.tile_select
            SfxType.SWAP_VALID -> com.mahmodhota.worldfood3dadventure.R.raw.swap_valid
            SfxType.SWAP_INVALID -> com.mahmodhota.worldfood3dadventure.R.raw.swap_invalid
            SfxType.MATCH_SMALL -> com.mahmodhota.worldfood3dadventure.R.raw.match_small
            SfxType.MATCH_LARGE -> com.mahmodhota.worldfood3dadventure.R.raw.match_large
            SfxType.COMBO_1 -> com.mahmodhota.worldfood3dadventure.R.raw.combo_1
            SfxType.COMBO_2 -> com.mahmodhota.worldfood3dadventure.R.raw.combo_2
            SfxType.COMBO_3 -> com.mahmodhota.worldfood3dadventure.R.raw.combo_3
            SfxType.CASCADE -> com.mahmodhota.worldfood3dadventure.R.raw.cascade
            SfxType.COIN_COLLECT -> com.mahmodhota.worldfood3dadventure.R.raw.coin_collect
            SfxType.STAR_EARNED -> com.mahmodhota.worldfood3dadventure.R.raw.star_earned
            SfxType.XP_GAINED -> com.mahmodhota.worldfood3dadventure.R.raw.xp_gained
            SfxType.LEVEL_UNLOCK -> com.mahmodhota.worldfood3dadventure.R.raw.level_unlock
            SfxType.COUNTRY_UNLOCK -> com.mahmodhota.worldfood3dadventure.R.raw.country_unlock
            SfxType.VICTORY -> com.mahmodhota.worldfood3dadventure.R.raw.victory
            SfxType.DEFEAT -> com.mahmodhota.worldfood3dadventure.R.raw.defeat
            SfxType.ITALY_VICTORY -> com.mahmodhota.worldfood3dadventure.R.raw.italy_victory
            SfxType.JAPAN_VICTORY -> com.mahmodhota.worldfood3dadventure.R.raw.japan_victory
            SfxType.MEXICO_VICTORY -> com.mahmodhota.worldfood3dadventure.R.raw.mexico_victory
        }
        return if (rid == 0) resolveRawResourceId(context, type.name.lowercase()) else rid
    }

    fun getMusicResId(context: Context, type: MusicType): Int? {
        if (type == MusicType.NONE) return null
        val rid = when(type) {
            MusicType.WORLD_MAP -> com.mahmodhota.worldfood3dadventure.R.raw.world_map
            MusicType.GERMANY_THEME -> com.mahmodhota.worldfood3dadventure.R.raw.germany_theme
            MusicType.ITALY_THEME -> com.mahmodhota.worldfood3dadventure.R.raw.italy_theme
            MusicType.FRANCE_THEME -> com.mahmodhota.worldfood3dadventure.R.raw.france_theme
            MusicType.JAPAN_THEME -> com.mahmodhota.worldfood3dadventure.R.raw.japan_theme
            MusicType.MEXICO_THEME -> com.mahmodhota.worldfood3dadventure.R.raw.mexico_theme
            MusicType.NONE -> 0
        }
        return if (rid == 0) resolveRawResourceId(context, type.name.lowercase()) else rid
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
