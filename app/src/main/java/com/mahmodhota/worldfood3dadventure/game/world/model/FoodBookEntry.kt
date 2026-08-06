package com.mahmodhota.worldfood3dadventure.game.world.model

data class FoodBookEntry(
    val id: String,
    val name: String,
    val country: String,
    val description: String,
    val unlocked: Boolean = false
)
