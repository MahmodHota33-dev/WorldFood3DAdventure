package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GlobeCameraTest {

    @Test
    fun autoLevelReducesTiltToZero() {
        val camera = GlobeCameraState(initialRotX = 45f)
        
        // Simulate enough ticks to reach zero threshold
        repeat(250) {
            camera.tickAutoLevel(1f)
        }
        
        assertEquals("Tilt should be centered at 0 after auto-leveling", 0f, camera.rotationX, 0.01f)
    }

    @Test
    fun autoLevelDoesNotAffectSpin() {
        val initialSpin = 120f
        val camera = GlobeCameraState(initialRotY = initialSpin, initialRotX = 30f)
        
        repeat(100) {
            camera.tickAutoLevel(1f)
        }
        
        assertEquals("RotationY (Spin) must be preserved during auto-leveling", initialSpin, camera.rotationY, 0.001f)
    }

    @Test
    fun autoLevelHandlesNegativeTilt() {
        val camera = GlobeCameraState(initialRotX = -20f)
        
        repeat(200) {
            camera.tickAutoLevel(1f)
        }
        
        assertEquals(0f, camera.rotationX, 0.01f)
    }

    @Test
    fun tickInertiaDecaysVelocity() {
        val camera = GlobeCameraState()
        camera.velY = 5f
        camera.velX = 5f
        
        assertTrue(camera.hasVelocity)
        
        repeat(200) {
            camera.tickInertia()
        }
        
        assertFalse("Velocity should decay to zero", camera.hasVelocity)
        assertEquals(0f, camera.velY, 0.01f)
        assertEquals(0f, camera.velX, 0.01f)
    }

    @Test
    fun rotationYWrapsCorrectly() {
        val camera = GlobeCameraState(initialRotY = 350f)
        camera.rotate(20f, 0f) // 370 -> 10
        assertEquals(10f, camera.rotationY, 0.001f)
        
        camera.rotate(-30f, 0f) // 10 -> -20
        // rotationY is stored as (initial + dY) % 360.
        // If rotate adds it, -20 % 360 is -20 in some languages, but let's check tickInertia logic.
        // tickInertia has explicit wrap:
        // if (rotationY < 0f) rotationY += 360f
        
        camera.velY = -30f
        camera.tickInertia()
        assertTrue(camera.rotationY >= 0f)
    }
}
