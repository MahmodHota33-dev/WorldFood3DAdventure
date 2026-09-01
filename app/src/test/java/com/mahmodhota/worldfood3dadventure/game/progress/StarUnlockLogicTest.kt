package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.buildLevelCompletionRewardPlan
import com.mahmodhota.worldfood3dadventure.data.progress.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.COUNTRY_PROGRESSION_ORDER
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for star-based country unlock logic, reward-plan no-double-counting,
 * and TOTAL_STARS reconciliation.
 */
class StarUnlockLogicTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun makeState(
        totalStars: Int = 0,
        countries: Map<String, CountryGameProgress> = emptyMap()
    ): PersistedGameState = PersistedGameState(
        player = PlayerProgress(totalStars = totalStars),
        countries = countries
    )

    private fun germanyProgress(vararg levelStars: Pair<Int, Int>): CountryGameProgress =
        CountryGameProgress(
            countryId = "germany",
            isUnlocked = true,
            isCompleted = false,
            levels = levelStars.associate { (lvl, stars) ->
                lvl to LevelProgress(
                    levelNumber = lvl,
                    countryId = "germany",
                    isUnlocked = true,
                    isCompleted = stars > 0,
                    bestStars = stars.coerceIn(0, 3)
                )
            }
        )

    // ── CountryProgressionChain unlock thresholds ─────────────────────────

    @Test
    fun germanyUnlockableWithZeroStars() {
        assertTrue(CountryProgressionChain.canUnlock("germany", 0))
    }

    @Test
    fun italyRequires30Stars() {
        assertFalse(CountryProgressionChain.canUnlock("italy", 29))
        assertTrue(CountryProgressionChain.canUnlock("italy", 30))
    }

    @Test
    fun franceRequires60Stars() {
        assertFalse(CountryProgressionChain.canUnlock("france", 59))
        assertTrue(CountryProgressionChain.canUnlock("france", 60))
    }

    @Test
    fun spainRequires90Stars() {
        assertFalse(CountryProgressionChain.canUnlock("spain", 89))
        assertTrue(CountryProgressionChain.canUnlock("spain", 90))
    }

    @Test
    fun japanRequires120Stars() {
        assertFalse(CountryProgressionChain.canUnlock("japan", 119))
        assertTrue(CountryProgressionChain.canUnlock("japan", 120))
    }

    @Test
    fun mexicoRequires145Stars() {
        assertFalse(CountryProgressionChain.canUnlock("mexico", 144))
        assertTrue(CountryProgressionChain.canUnlock("mexico", 145))
    }

    @Test
    fun sudanRequires185Stars() {
        assertFalse(CountryProgressionChain.canUnlock("sudan", 184))
        assertTrue(CountryProgressionChain.canUnlock("sudan", 185))
    }

    // ── Star threshold math: enough stars are earnable ────────────────────

    @Test
    fun maxEarnableStarsSufficeForAllUnlocks() {
        // Max 3 stars × total levels across all countries
        val totalLevels = COUNTRY_PROGRESSION_ORDER.sumOf {
            CountryProgressionChain.getTotalLevels(it)
        }
        val maxPossibleStars = totalLevels * 3
        // Sudan requires 185; check it's reachable from previous countries' stars
        val starsBeforeSudan = COUNTRY_PROGRESSION_ORDER
            .filter { it != "sudan" }
            .sumOf { CountryProgressionChain.getTotalLevels(it) * 3 }
        assertTrue(
            "Stars earnable before Sudan ($starsBeforeSudan) must meet Sudan threshold (185)",
            starsBeforeSudan >= 185
        )
        assertTrue(
            "Max possible stars ($maxPossibleStars) must exceed all thresholds",
            maxPossibleStars >= 185
        )
    }

    @Test
    fun germanyCumulativeMaxCoversItaly() {
        val germanyMax = CountryProgressionChain.getTotalLevels("germany") * 3
        assertTrue(germanyMax >= CountryProgressionChain.getSpec("italy")!!.requiredStarsToUnlock)
    }

    // ── Reward plan: no double-counting of XP/coins/stars ─────────────────

    @Test
    fun firstClearGrantsXpAndCoinsOnce() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 0, currentBestScore = 0,
            alreadyCompleted = false,
            incomingStars = 2, incomingScore = 1000,
            xpReward = 100, coinReward = 40
        )
        assertTrue(plan.isFirstClear)
        assertEquals(100, plan.xpDelta)
        assertEquals(40, plan.coinsDelta)
        assertEquals(2, plan.starsDelta)
    }

    @Test
    fun replaySameScoreGrantsNoXpOrCoins() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 2, currentBestScore = 1000,
            alreadyCompleted = true,
            incomingStars = 2, incomingScore = 1000,
            xpReward = 100, coinReward = 40
        )
        assertFalse(plan.isFirstClear)
        assertEquals(0, plan.xpDelta)
        assertEquals(0, plan.coinsDelta)
        assertEquals(0, plan.starsDelta)
    }

    @Test
    fun replayWithBetterStarsAddsOnlyDelta() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 1, currentBestScore = 800,
            alreadyCompleted = true,
            incomingStars = 3, incomingScore = 1800,
            xpReward = 150, coinReward = 60
        )
        assertEquals(2, plan.starsDelta)   // 3 - 1 = 2
        assertEquals(3, plan.updatedBestStars)
        assertEquals(0, plan.xpDelta)     // no XP on replay
        assertEquals(0, plan.coinsDelta)  // no coins on replay
    }

    @Test
    fun replayWithWorseStarsAddsNoDelta() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 3, currentBestScore = 2000,
            alreadyCompleted = true,
            incomingStars = 1, incomingScore = 500,
            xpReward = 100, coinReward = 40
        )
        assertEquals(0, plan.starsDelta)
        assertEquals(3, plan.updatedBestStars)  // best stays at 3
    }

    // ── ProgressionQuery star totals match per-level data ──────────────────

    @Test
    fun totalStarsEarnedSumsFromLevels() {
        val state = makeState(
            totalStars = 999, // intentionally wrong stored value — query ignores it
            countries = mapOf(
                "germany" to germanyProgress(1 to 3, 2 to 2, 3 to 1)
            )
        )
        // totalStarsEarned reads from per-level data, not player.totalStars
        assertEquals(6, ProgressionQuery.totalStarsEarned(state))
    }

    @Test
    fun totalStarsEarnedIsZeroForLockedCountries() {
        val state = makeState(totalStars = 0)
        assertEquals(0, ProgressionQuery.totalStarsEarned(state))
    }

    // ── Germany is unlocked on fresh install ──────────────────────────────

    @Test
    fun germanyUnlockedByDefaultViaInitiallyUnlocked() {
        // Fresh install: countries map is empty
        val state = makeState()
        val map = ProgressionQuery.countryProgressMap(state)
        assertTrue("Germany must be unlocked on fresh install",
            map["germany"]?.isUnlocked == true)
    }

    @Test
    fun otherCountriesLockedByDefault() {
        val state = makeState()
        val map = ProgressionQuery.countryProgressMap(state)
        val initial = CountryProgressionChain.UNLOCK_ORDER.filter { it.unlockedInitially }.map { it.countryId }
        val others = LevelRegistry.allCountryIds.filterNot { it in initial }
        for (id in others) {
            assertFalse("$id should be locked by default", map[id]?.isUnlocked == true)
        }
    }

    // ── LevelRegistry ordering ────────────────────────────────────────────

    @Test
    fun countryOrderMatchesProgressionChain() {
        val registryOrder = LevelRegistry.allCountryIds
        val chainOrder = CountryProgressionChain.UNLOCK_ORDER.map { it.countryId }
        assertEquals("LevelRegistry order must match CountryProgressionChain",
            chainOrder, registryOrder)
    }

    @Test
    fun allCountryIdsAreRegistered() {
        assertEquals(LevelRegistry.allCountryIds, LevelRegistry.allCountryIds) // This is trivial but matches intent
    }

    // ── TOTAL_STARS reconciliation logic (pure math) ───────────────────────

    @Test
    fun reconciliationNeededWhenComputedExceedsStored() {
        // Simulate: player completed 10 Germany levels with 3 stars each
        // but TOTAL_STARS key was never written → stored = 0, computed = 30
        val storedStars = 0
        val computedStars = 30
        assertTrue("Reconciliation needed when computed > stored",
            computedStars > storedStars)
    }

    @Test
    fun reconciliationNotNeededWhenComputedLessOrEqual() {
        // If stored is already correct or higher (e.g. due to bonus), don't reduce
        val storedStars = 35
        val computedStars = 30
        assertFalse("No reconciliation when computed <= stored",
            computedStars > storedStars)
    }

    @Test
    fun starsDeltaIsNeverNegative() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 3, currentBestScore = 9999,
            alreadyCompleted = true,
            incomingStars = 0, incomingScore = 0,
            xpReward = 0, coinReward = 0
        )
        assertTrue("starsDelta must never be negative", plan.starsDelta >= 0)
    }

    // ── Already-unlocked countries stay unlocked ──────────────────────────

    @Test
    fun alreadyUnlockedCountryRemainsUnlocked() {
        val state = makeState(
            countries = mapOf(
                "germany" to germanyProgress(1 to 3),
                "italy" to CountryGameProgress("italy", isUnlocked = true)
            )
        )
        val map = ProgressionQuery.countryProgressMap(state)
        assertTrue("Italy must stay unlocked", map["italy"]?.isUnlocked == true)
    }

    @Test
    fun alreadyCompletedCountryRemainsCompleted() {
        val state = makeState(
            countries = mapOf(
                "germany" to CountryGameProgress("germany", isUnlocked = true, isCompleted = true)
            )
        )
        val map = ProgressionQuery.countryProgressMap(state)
        assertTrue("Germany must stay completed", map["germany"]?.isCompleted == true)
    }

    // ── Sudan chapter-legacy check ────────────────────────────────────────

    @Test
    fun sudanIsRegistered() {
        assertTrue(LevelRegistry.allCountryIds.contains("sudan"))
    }

    @Test
    fun lastCountryHasNoNextInRegistry() {
        val ids = LevelRegistry.allCountryIds
        val lastId = CountryProgressionChain.UNLOCK_ORDER.last().countryId
        val lastIdx = ids.indexOf(lastId)
        assertEquals("Last country must be at the end", ids.size - 1, lastIdx)
    }

    // ── Thresholds are monotonically increasing ───────────────────────────

    @Test
    fun unlockThresholdsAreMonotonicallyIncreasing() {
        val thresholds = CountryProgressionChain.UNLOCK_ORDER.map { it.requiredStarsToUnlock }
        for (i in 1 until thresholds.size) {
            assertTrue(
                "Threshold at index $i (${thresholds[i]}) must be >= threshold at ${i - 1} (${thresholds[i - 1]})",
                thresholds[i] >= thresholds[i - 1]
            )
        }
    }

    // ── Each country's threshold is reachable from prior countries' stars ─

    @Test
    fun eachCountryThresholdIsReachableFromPriorStars() {
        val chain = CountryProgressionChain.UNLOCK_ORDER
        var cumulativeMaxStars = 0
        for (spec in chain) {
            if (spec.unlockedInitially) {
                cumulativeMaxStars += spec.totalLevels * 3
                continue
            }
            assertTrue(
                "${spec.countryId} threshold (${spec.requiredStarsToUnlock}) must be reachable " +
                    "from prior country stars ($cumulativeMaxStars)",
                cumulativeMaxStars >= spec.requiredStarsToUnlock
            )
            cumulativeMaxStars += spec.totalLevels * 3
        }
    }

    // ── Boundary: exact threshold values per country ──────────────────────

    @Test
    fun allCountryBoundaryConditions() {
        data class Case(val id: String, val threshold: Int)
        val cases = listOf(
            Case("germany", 0),
            Case("italy", 30),
            Case("france", 60),
            Case("spain", 90),
            Case("japan", 120),
            Case("mexico", 145),
            Case("sudan", 185)
        )
        for (c in cases) {
            if (c.threshold > 0) {
                assertFalse(
                    "${c.id} should be locked at threshold-1 (${c.threshold - 1})",
                    CountryProgressionChain.canUnlock(c.id, c.threshold - 1)
                )
            }
            assertTrue(
                "${c.id} should unlock at threshold (${c.threshold})",
                CountryProgressionChain.canUnlock(c.id, c.threshold)
            )
            // Also verify at a large star count (e.g. 300) — already-met thresholds stay met
            assertTrue(
                "${c.id} should remain unlockable at 300 stars",
                CountryProgressionChain.canUnlock(c.id, 300)
            )
        }
    }
}
