package com.mahmodhota.worldfood3dadventure.data.progress

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType

/**
 * Low-level DataStore configuration.
 */
val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "worldfood_match_save")

object GameKeys {
    val SCHEMA_VERSION = intPreferencesKey("schema_version")
    
    // Player
    val LIVES = intPreferencesKey("player_lives")
    val COINS = intPreferencesKey("player_coins")
    val XP = intPreferencesKey("player_xp")
    val LEVEL = intPreferencesKey("player_level")
    val TOTAL_STARS = intPreferencesKey("total_stars")
    val USERNAME = stringPreferencesKey("username")

    // Selection
    val LAST_COUNTRY = stringPreferencesKey("last_country")
    val LAST_LEVEL = intPreferencesKey("last_level")

    val CHAPTER_1_COMPLETED = booleanPreferencesKey("chapter_1_completed")
    val WORLD_EXPLORER_BADGE = booleanPreferencesKey("world_explorer_badge")
    val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    val ONBOARDING_STATE = stringPreferencesKey("onboarding_state")
    val UNLOCKED_ACHIEVEMENTS = stringSetPreferencesKey("unlocked_achievements")

    // Daily Journey
    val DAILY_DATE_KEY = stringPreferencesKey("daily_date_key")
    val DAILY_STREAK = intPreferencesKey("daily_streak")
    val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    val DAILY_MISSIONS_JSON = stringPreferencesKey("daily_missions_json")

    // Stats
    val FIRST_INSTALL = longPreferencesKey("stat_first_install")
    val LAST_PLAYED = longPreferencesKey("stat_last_played")
    val TOTAL_PLAY_TIME = longPreferencesKey("stat_total_play_time")
    val TOTAL_MATCHES = intPreferencesKey("stat_total_matches")
    val LONGEST_SESSION = longPreferencesKey("stat_longest_session")

    // Settings
    val VOL_MUSIC = floatPreferencesKey("set_vol_music")
    val VOL_SFX = floatPreferencesKey("set_vol_sfx")
    val VIBRATION = booleanPreferencesKey("set_vibration")
    val DARK_MODE = booleanPreferencesKey("set_dark_mode")

    // Dynamic Helpers
    fun countryUnlocked(id: String) = booleanPreferencesKey("c_${id}_unlocked")
    fun countryCompleted(id: String) = booleanPreferencesKey("c_${id}_completed")
    fun levelUnlocked(cId: String, lvl: Int) = booleanPreferencesKey("l_${cId}_${lvl}_unlocked")
    fun levelCompleted(cId: String, lvl: Int) = booleanPreferencesKey("l_${cId}_${lvl}_completed")
    fun levelStars(cId: String, lvl: Int) = intPreferencesKey("l_${cId}_${lvl}_stars")
    fun levelScore(cId: String, lvl: Int) = intPreferencesKey("l_${cId}_${lvl}_score")
    fun boosterCount(type: BoosterType) = intPreferencesKey("booster_${type.name.lowercase()}")
    fun foodDiscovered(cId: String, foodId: String) = booleanPreferencesKey("fd_${cId}_${foodId}")
}
