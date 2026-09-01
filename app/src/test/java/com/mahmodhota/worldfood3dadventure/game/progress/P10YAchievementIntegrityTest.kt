package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10YAchievementIntegrityTest {

    @Test
    fun `Every achievement must have a unique ID`() {
        val ids = AchievementRegistry.achievements.map { it.id }
        assertEquals("Duplicate achievement IDs detected", ids.size, ids.distinct().size)
    }

    @Test
    fun `Achievement requirements must be reachable`() {
        AchievementRegistry.achievements.forEach { achievement ->
            when (val req = achievement.requirement) {
                is AchievementRequirement.TotalStars -> assertTrue(req.count <= 3195 * 3)
                is AchievementRequirement.LevelsCompleted -> assertTrue(req.count <= 3195)
                is AchievementRequirement.CountriesMastered -> assertTrue(req.count <= 213)
                is AchievementRequirement.FoodsDiscovered -> assertTrue(req.count <= 2000)
                else -> {}
            }
        }
    }
}
