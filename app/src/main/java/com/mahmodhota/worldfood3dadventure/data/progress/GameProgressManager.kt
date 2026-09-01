package com.mahmodhota.worldfood3dadventure.data.progress

import android.content.Context

/**
 * Access point for the GameProgressRepository.
 */
object GameProgressManager {
    private var _repository: GameProgressRepository? = null
    
    val repository: GameProgressRepository
        get() = _repository ?: throw IllegalStateException("GameProgressManager not initialized")

    fun initialize(context: Context) {
        if (_repository == null) {
            _repository = GameProgressRepository(context.applicationContext)
        }
    }

    /** For unit testing only. */
    fun setRepositoryForTesting(repository: GameProgressRepository) {
        _repository = repository
    }
}
