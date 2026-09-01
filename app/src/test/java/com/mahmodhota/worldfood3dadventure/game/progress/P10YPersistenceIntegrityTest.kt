package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.GameKeys
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10YPersistenceIntegrityTest {

    @Test
    fun `Every critical game state key must be defined in GameKeys`() {
        assertNotNull(GameKeys.XP)
        assertNotNull(GameKeys.COINS)
        assertNotNull(GameKeys.TOTAL_STARS)
        assertNotNull(GameKeys.UNLOCKED_ACHIEVEMENTS)
        assertNotNull(GameKeys.ONBOARDING_STATE)
        assertNotNull(GameKeys.DAILY_DATE_KEY)
    }
}
