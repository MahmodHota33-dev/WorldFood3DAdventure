package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10VAchievementDefinitionTest {

    @Test
    fun `Achievement IDs should be unique`() {
        val ids = AchievementRegistry.achievements.map { it.id }
        assertEquals("Duplicate achievement IDs found", ids.size, ids.distinct().size)
    }

    @Test
    fun `Achievements should have valid data`() {
        AchievementRegistry.achievements.forEach { achievement ->
            assertTrue("Achievement ${achievement.id} has empty title", achievement.title.isNotBlank())
            assertTrue("Achievement ${achievement.id} has empty description", achievement.description.isNotBlank())
            assertTrue("Achievement ${achievement.id} has empty icon", achievement.icon.isNotBlank())
        }
    }

    @Test
    fun `Registry should contain core categories`() {
        val categories = AchievementRegistry.achievements.map { it.category }.distinct()
        assertTrue("Should contain TRAVEL category", categories.contains(AchievementCategory.TRAVEL))
        assertTrue("Should contain FOOD category", categories.contains(AchievementCategory.FOOD))
        assertTrue("Should contain LEVELS category", categories.contains(AchievementCategory.LEVELS))
        assertTrue("Should contain STARS category", categories.contains(AchievementCategory.STARS))
    }
}
