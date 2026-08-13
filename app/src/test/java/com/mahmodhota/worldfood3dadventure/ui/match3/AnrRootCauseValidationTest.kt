package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.ui.match3.components.verifyGermanyForestTermination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnrRootCauseValidationTest {

    @Test
    fun germanyForestLoopTerminatesWithZeroDimensions() {
        // Root cause reproduction: width = 0 leads to step = 0, which previously caused an infinite loop
        val iterations = verifyGermanyForestTermination(width = 0f, step = 0f)
        assertEquals("Should terminate immediately with 0 iterations for zero dimensions", 0, iterations)
    }

    @Test
    fun germanyForestLoopTerminatesWithNegativeDimensions() {
        val iterations = verifyGermanyForestTermination(width = -100f, step = -1f)
        assertEquals("Should terminate immediately for negative dimensions", 0, iterations)
    }

    @Test
    fun germanyForestLoopTerminatesWithNonFiniteDimensions() {
        val iterationsNan = verifyGermanyForestTermination(width = Float.NaN, step = Float.NaN)
        val iterationsInf = verifyGermanyForestTermination(width = Float.POSITIVE_INFINITY, step = Float.POSITIVE_INFINITY)
        
        assertEquals(0, iterationsNan)
        assertEquals(0, iterationsInf)
    }

    @Test
    fun germanyForestLoopTerminatesWithTinyStep() {
        // Even with a very small step, the iteration count should be bounded
        val iterations = verifyGermanyForestTermination(width = 1000f, step = 0.00001f)
        assertTrue("Should terminate due to the hard iteration limit (500)", iterations <= 500)
    }

    @Test
    fun germanyForestLoopProducesCorrectCountForValidDimensions() {
        // For w=1000, step = w * 0.075 = 75
        // x starts at -37.5
        // x <= 1075
        // x sequence: -37.5, 37.5, 112.5, ..., 1012.5, 1087.5 (terminates)
        val iterations = verifyGermanyForestTermination(width = 1000f, step = 75f)
        assertTrue("Should produce a reasonable number of trees for valid dimensions", iterations in 10..20)
    }
}
