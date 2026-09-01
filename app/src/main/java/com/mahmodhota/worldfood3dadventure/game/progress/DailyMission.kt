package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType

enum class MissionType {
    TRAVEL,
    FOOD,
    LEVELS,
    STARS,
    CONTINENTS,
    BOOSTERS
}

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val type: MissionType,
    val target: Int,
    val currentProgress: Int = 0,
    val rewardXp: Int = 0,
    val rewardCoins: Int = 0,
    val rewardBooster: BoosterType? = null,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
) {
    fun serialize(): String {
        return "$id|$title|$description|${type.name}|$target|$currentProgress|$rewardXp|$rewardCoins|${rewardBooster?.name ?: "NONE"}|$isCompleted|$isClaimed"
    }

    companion object {
        fun deserialize(s: String): DailyMission? {
            val parts = s.split("|")
            if (parts.size < 11) return null
            return try {
                DailyMission(
                    id = parts[0],
                    title = parts[1],
                    description = parts[2],
                    type = MissionType.valueOf(parts[3]),
                    target = parts[4].toInt(),
                    currentProgress = parts[5].toInt(),
                    rewardXp = parts[6].toInt(),
                    rewardCoins = parts[7].toInt(),
                    rewardBooster = if (parts[8] == "NONE") null else BoosterType.valueOf(parts[8]),
                    isCompleted = parts[9].toBoolean(),
                    isClaimed = parts[10].toBoolean()
                )
            } catch (e: Exception) { null }
        }
    }
}

data class DailyJourney(
    val dateKey: String, // YYYY-MM-DD
    val missions: List<DailyMission> = emptyList(),
    val streak: Int = 0,
    val lastActiveDate: String = ""
)
