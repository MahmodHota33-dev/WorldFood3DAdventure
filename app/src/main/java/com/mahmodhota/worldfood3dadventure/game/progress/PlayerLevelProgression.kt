package com.mahmodhota.worldfood3dadventure.game.progress

/**
 * Shared XP/level math used by repository + UI to keep displays and persistence consistent.
 */
object PlayerLevelProgression {
    const val XP_PER_LEVEL: Int = 500

    data class Snapshot(
        val totalXp: Int,
        val level: Int,
        val xpIntoCurrentLevel: Int,
        val xpForNextLevel: Int
    )

    fun levelFromTotalXp(totalXp: Int): Int =
        (totalXp.coerceAtLeast(0) / XP_PER_LEVEL) + 1

    fun xpIntoCurrentLevel(totalXp: Int): Int =
        totalXp.coerceAtLeast(0) % XP_PER_LEVEL

    fun normalizedLevel(totalXp: Int, storedLevel: Int): Int =
        maxOf(storedLevel.coerceAtLeast(1), levelFromTotalXp(totalXp))

    fun needsLegacyLevelMigration(totalXp: Int, storedLevel: Int): Boolean =
        storedLevel.coerceAtLeast(1) < levelFromTotalXp(totalXp)

    fun snapshot(totalXp: Int, storedLevel: Int? = null): Snapshot {
        val normalizedXp = totalXp.coerceAtLeast(0)
        val resolvedLevel = storedLevel?.let { normalizedLevel(normalizedXp, it) }
            ?: levelFromTotalXp(normalizedXp)
        return Snapshot(
            totalXp = normalizedXp,
            level = resolvedLevel,
            xpIntoCurrentLevel = xpIntoCurrentLevel(normalizedXp),
            xpForNextLevel = XP_PER_LEVEL
        )
    }
}
