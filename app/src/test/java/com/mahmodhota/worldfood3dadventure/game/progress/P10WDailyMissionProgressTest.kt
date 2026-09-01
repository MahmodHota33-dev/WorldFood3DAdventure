package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10WDailyMissionProgressTest {

    @Test
    fun `Updating progress should correctly update mission state`() {
        val missions = listOf(
            DailyMission("m1", "Title", "Desc", MissionType.STARS, target = 10)
        )
        val journey = DailyJourney("2026-08-23", missions = missions)
        
        val updated = DailyJourneyManager.updateProgress(journey, MissionType.STARS, 5)
        assertEquals(5, updated.missions.first().currentProgress)
        assertEquals(false, updated.missions.first().isCompleted)
        
        val finished = DailyJourneyManager.updateProgress(updated, MissionType.STARS, 10)
        assertEquals(10, finished.missions.first().currentProgress)
        assertEquals(true, finished.missions.first().isCompleted)
    }
}
