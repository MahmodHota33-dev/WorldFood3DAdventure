package com.mahmodhota.worldfood3dadventure.data.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.GameSettings
import org.junit.Assert.*
import org.junit.Test

class SettingsPersistenceTest {

    @Test
    fun testDefaultSettings() {
        val settings = GameSettings()
        assertEquals(1.0f, settings.musicVolume, 0.01f)
        assertEquals(1.0f, settings.sfxVolume, 0.01f)
        assertTrue(settings.vibrationEnabled)
    }

    @Test
    fun testSettingsClamping() {
        val volume = 1.5f
        val clamped = volume.coerceIn(0f, 1f)
        assertEquals(1.0f, clamped, 0.01f)
    }
}
