package com.mahmodhota.worldfood3dadventure.data.audio

import android.content.Context

/**
 * Singleton holder for Audio and Haptic managers.
 */
object GlobalSystemManager {
    private var _audioManager: AudioManager? = null
    private var _hapticManager: HapticManager? = null

    val audio: AudioManager
        get() = _audioManager ?: throw IllegalStateException("GlobalSystemManager not initialized")
    
    val haptics: HapticManager
        get() = _hapticManager ?: throw IllegalStateException("GlobalSystemManager not initialized")

    fun initialize(context: Context) {
        if (_audioManager == null) {
            _audioManager = AudioManager(context.applicationContext)
            _hapticManager = HapticManager(context.applicationContext)
        }
    }

    fun applySettings(musicVolume: Float, sfxVolume: Float, vibrationEnabled: Boolean) {
        _audioManager?.setMusicVolume(musicVolume)
        _audioManager?.setSfxVolume(sfxVolume)
        _hapticManager?.setEnabled(vibrationEnabled)
    }

    fun onAppBackground() {
        _audioManager?.pauseMusicForBackground()
    }

    fun onAppForeground() {
        _audioManager?.resumeMusicIfNeeded()
    }

    fun release() {
        _audioManager?.release()
        _audioManager = null
        _hapticManager = null
    }
}
