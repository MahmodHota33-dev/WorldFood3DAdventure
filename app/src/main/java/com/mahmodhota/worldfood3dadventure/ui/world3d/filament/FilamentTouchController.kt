package com.mahmodhota.worldfood3dadventure.ui.world3d.filament

import kotlin.math.abs

/**
 * Isolated prototype version of drag and inertia for the Filament globe.
 */
class FilamentTouchController {

    var rotationY = -15f
    var rotationX = 25f

    private var velY = 0f
    private var velX = 0f
    private var isDragging = false

    fun onDragStart() {
        isDragging = true
        velY = 0f
        velX = 0f
    }

    fun onDrag(panX: Float, panY: Float) {
        val impulseY = panX * 0.24f
        val impulseX = -panY * 0.24f
        
        // Immediate response
        rotationY = (rotationY + impulseY) % 360f
        rotationX = (rotationX + impulseX).coerceIn(-80f, 80f)
        
        // Track velocity for inertia
        velY = (velY * 0.7f + impulseY * 0.3f).coerceIn(-10f, 10f)
        velX = (velX * 0.7f + impulseX * 0.3f).coerceIn(-10f, 10f)
    }

    fun onDragEnd() {
        isDragging = false
    }

    fun tick(deltaSeconds: Float, autoRotationSpeed: Float) {
        if (isDragging) return

        if (abs(velY) > 0.01f || abs(velX) > 0.01f) {
            // Apply inertia
            rotationY = (rotationY + velY) % 360f
            rotationX = (rotationX + velX).coerceIn(-80f, 80f)
            
            val damping = 0.94f
            velY *= damping
            velX *= damping
        } else {
            // Automatic rotation when idle
            rotationY = (rotationY + autoRotationSpeed * deltaSeconds * 60f) % 360f
        }
    }
}
