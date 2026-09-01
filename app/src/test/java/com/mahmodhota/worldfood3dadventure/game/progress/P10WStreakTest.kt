package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class P10WStreakTest {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @Test
    fun `Streak should increment on consecutive days`() {
        val yesterday = LocalDate.now().minusDays(1).format(formatter)
        val initialJourney = DailyJourney(
            dateKey = yesterday,
            streak = 5,
            lastActiveDate = yesterday
        )
        
        val updated = DailyJourneyManager.checkDayChange(initialJourney)
        assertEquals(6, updated.streak)
    }

    @Test
    fun `Streak should reset on missed days`() {
        val longAgo = LocalDate.now().minusDays(5).format(formatter)
        val initialJourney = DailyJourney(
            dateKey = longAgo,
            streak = 5,
            lastActiveDate = longAgo
        )
        
        val updated = DailyJourneyManager.checkDayChange(initialJourney)
        assertEquals(1, updated.streak)
    }
}
