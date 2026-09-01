package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Defines the state of a blocker on a tile.
 */
enum class BlockerState {
    NONE,
    ICE_1,
    ICE_2;

    val isIce: Boolean get() = this == ICE_1 || this == ICE_2
    
    fun damage(): BlockerState = when (this) {
        ICE_2 -> ICE_1
        ICE_1 -> NONE
        NONE -> NONE
    }
}

/**
 * Immutable data model for a single tile on the match-3 board.
 */
data class FoodTile(
    val id: Long,
    val type: FoodTileType,
    val specialType: SpecialTileType = SpecialTileType.NONE,
    val blocker: BlockerState = BlockerState.NONE
)
