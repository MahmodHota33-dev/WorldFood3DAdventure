package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object DailyJourneyManager {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getTodayKey(): String = LocalDate.now().format(formatter)

    /**
     * Generates 3 deterministic daily missions for the given date key.
     */
    fun generateMissions(dateKey: String): List<DailyMission> {
        val seed = dateKey.hashCode().toLong()
        val random = Random(seed)
        
        val missions = mutableListOf<DailyMission>()
        
        // Mission 1: Levels or Travel
        if (random.nextBoolean()) {
            missions.add(DailyMission(
                id = "travel_daily",
                title = "Daily Explorer",
                description = "Complete 3 adventures today.",
                type = MissionType.TRAVEL,
                target = 3,
                rewardXp = EconomyConfig.DAILY_MISSION_XP_DEFAULT,
                rewardCoins = EconomyConfig.DAILY_MISSION_COINS_DEFAULT
            ))
        } else {
            missions.add(DailyMission(
                id = "levels_daily",
                title = "Puzzle Master",
                description = "Win 5 levels today.",
                type = MissionType.LEVELS,
                target = 5,
                rewardXp = EconomyConfig.DAILY_MISSION_XP_DEFAULT + 10,
                rewardCoins = EconomyConfig.DAILY_MISSION_COINS_DEFAULT + 5
            ))
        }
        
        // Mission 2: Food Discovery
        missions.add(DailyMission(
            id = "food_daily",
            title = "Taste Finder",
            description = "Discover 2 new foods.",
            type = MissionType.FOOD,
            target = 2,
            rewardXp = EconomyConfig.DAILY_MISSION_XP_DEFAULT - 10,
            rewardCoins = EconomyConfig.DAILY_MISSION_COINS_DEFAULT - 5
        ))
        
        // Mission 3: Stars
        missions.add(DailyMission(
            id = "stars_daily",
            title = "Star Collector",
            description = "Earn 10 stars in total.",
            type = MissionType.STARS,
            target = 10,
            rewardXp = EconomyConfig.DAILY_MISSION_XP_DEFAULT + 10,
            rewardCoins = EconomyConfig.DAILY_MISSION_COINS_DEFAULT + 5,
            rewardBooster = BoosterType.HAMMER
        ))
        
        return missions
    }

    /**
     * Updates mission progress based on an event.
     */
    fun updateProgress(currentJourney: DailyJourney, type: MissionType, amount: Int): DailyJourney {
        val updatedMissions = currentJourney.missions.map { mission ->
            if (mission.type == type && !mission.isCompleted) {
                val newProgress = (mission.currentProgress + amount).coerceAtMost(mission.target)
                mission.copy(
                    currentProgress = newProgress,
                    isCompleted = newProgress >= mission.target
                )
            } else {
                mission
            }
        }
        return currentJourney.copy(missions = updatedMissions)
    }

    /**
     * Checks for day change and updates streak.
     */
    fun checkDayChange(currentJourney: DailyJourney): DailyJourney {
        val today = getTodayKey()
        if (currentJourney.dateKey == today) return currentJourney

        // Day changed
        val lastActive = currentJourney.lastActiveDate
        if (lastActive == "") {
             return DailyJourney(
                dateKey = today,
                missions = generateMissions(today),
                streak = 1,
                lastActiveDate = today
            )
        }

        val lastDate = LocalDate.parse(lastActive, formatter)
        val todayDate = LocalDate.parse(today, formatter)
        val daysDiff = todayDate.toEpochDay() - lastDate.toEpochDay()
        
        val newStreak = when (daysDiff) {
            1L -> currentJourney.streak + 1
            0L -> currentJourney.streak
            else -> 1 // Reset if missed a day
        }

        return DailyJourney(
            dateKey = today,
            missions = generateMissions(today),
            streak = newStreak,
            lastActiveDate = today
        )
    }
}
