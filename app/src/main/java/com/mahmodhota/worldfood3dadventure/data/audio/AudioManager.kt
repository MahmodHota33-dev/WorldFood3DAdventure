package com.mahmodhota.worldfood3dadventure.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Orchestrates SFX and Music.
 */
class AudioManager(private val context: Context) {
    
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    private var mediaPlayer: MediaPlayer? = null
    private var currentMusicType: MusicType = MusicType.NONE
    
    private var musicVolume: Float = 1.0f
    private var sfxVolume: Float = 1.0f
    private val scope = CoroutineScope(Dispatchers.Main)
    private var fadeJob: kotlinx.coroutines.Job? = null
    private var released = false
    private var shouldResumeMusic = false

    private val loadedSfx = mutableMapOf<SfxType, Int>()
    private val loadingSfx = mutableSetOf<SfxType>()
    private val sampleIdToSfx = mutableMapOf<Int, SfxType>()
    private val pendingSfxPlay = mutableSetOf<SfxType>()
    private val lastSfxPlayAtMs = mutableMapOf<SfxType, Long>()

    init {
        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            val type = sampleIdToSfx.remove(sampleId) ?: return@setOnLoadCompleteListener
            loadingSfx.remove(type)
            if (status == 0) {
                loadedSfx[type] = sampleId
                if (pendingSfxPlay.remove(type) && sfxVolume > 0f && !released) {
                    pool.play(sampleId, sfxVolume, sfxVolume, 1, 0, 1f)
                }
            } else {
                pendingSfxPlay.remove(type)
            }
        }
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(musicVolume, musicVolume)
        } catch (e: Exception) { /* Released */ }
    }

    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
    }

    /**
     * Plays a sound effect.
     */
    fun playSfx(type: SfxType) {
        if (released || sfxVolume <= 0f) return
        val now = SystemClock.elapsedRealtime()
        val lastPlay = lastSfxPlayAtMs[type] ?: 0L
        if (now - lastPlay < 40L) return
        lastSfxPlayAtMs[type] = now
        val resId = SoundRepository.getSfxResId(context, type) ?: return

        try {
            val loadedSampleId = loadedSfx[type]
            if (loadedSampleId != null) {
                soundPool.play(loadedSampleId, sfxVolume, sfxVolume, 1, 0, 1f)
                return
            }

            if (type in loadingSfx) {
                pendingSfxPlay.add(type)
                return
            }

            val sampleId = soundPool.load(context, resId, 1)
            if (sampleId > 0) {
                loadingSfx.add(type)
                sampleIdToSfx[sampleId] = type
                pendingSfxPlay.add(type)
            }
        } catch (e: Exception) {
            // Missing/invalid resource should fail silently.
        }
    }

    /**
     * Fades into a new music track.
     */
    fun playMusic(type: MusicType) {
        if (released) return
        if (currentMusicType == type) return
        
        fadeJob?.cancel()
        fadeJob = scope.launch {
            // Fade out current
            fadeOut()
            
            mediaPlayer?.release()
            mediaPlayer = null
            currentMusicType = type
            
            if (type == MusicType.NONE) return@launch

            val resId = SoundRepository.getMusicResId(context, type) ?: return@launch

            try {
                mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                    isLooping = true
                    setVolume(0f, 0f)
                    start()
                    fadeIn()
                }
            } catch (e: Exception) {
                // Ignore missing placeholder resource
            }
        }
    }

    fun pauseMusicForBackground() {
        val player = mediaPlayer ?: return
        try {
            shouldResumeMusic = player.isPlaying
            if (player.isPlaying) {
                player.pause()
            }
        } catch (e: Exception) {
            shouldResumeMusic = false
        }
    }

    fun resumeMusicIfNeeded() {
        if (!shouldResumeMusic) return
        try {
            mediaPlayer?.start()
            shouldResumeMusic = false
        } catch (e: Exception) {
            shouldResumeMusic = false
        }
    }

    private suspend fun fadeOut() {
        val player = mediaPlayer ?: return
        var vol = musicVolume
        try {
            while (vol > 0f) {
                vol -= 0.1f
                player.setVolume(vol.coerceAtLeast(0f), vol.coerceAtLeast(0f))
                delay(50L)
            }
            player.pause()
        } catch (e: Exception) {
            // Player might have been released
        }
    }

    private suspend fun fadeIn() {
        val player = mediaPlayer ?: return
        var vol = 0f
        try {
            while (vol < musicVolume) {
                vol += 0.1f
                player.setVolume(vol.coerceAtMost(musicVolume), vol.coerceAtMost(musicVolume))
                delay(50L)
            }
        } catch (e: Exception) {
            // Player might have been released
        }
    }

    fun release() {
        released = true
        fadeJob?.cancel()
        pendingSfxPlay.clear()
        sampleIdToSfx.clear()
        loadingSfx.clear()
        loadedSfx.clear()
        lastSfxPlayAtMs.clear()
        soundPool.release()
        mediaPlayer?.release()
        mediaPlayer = null
        shouldResumeMusic = false
        currentMusicType = MusicType.NONE
    }
}
