package com.mahmodhota.worldfood3dadventure.ui.match3.components

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.ui.match3.Match3AnimationPhase

enum class TileAnimationRole {
    Idle,
    Selected,
    Swapping,
    InvalidReturning,
    Removing,
    Falling,
    Refilling,
    Landing
}

data class VisualTileState(
    val tile: FoodTile,
    val currentLogicalPosition: BoardPosition,
    val previousLogicalPosition: BoardPosition,
    val targetLogicalPosition: BoardPosition,
    val role: TileAnimationRole,
    val fallDistance: Int = 0,
    val spawnOriginRow: Int? = null
)

data class BoardAnimationSnapshot(
    val tiles: List<VisualTileState>,
    val removedTileIds: Set<Long>
)

fun buildBoardAnimationSnapshot(
    previousBoard: Match3Board?,
    currentBoard: Match3Board,
    selectedPosition: BoardPosition?,
    matchedPositions: Set<BoardPosition>,
    phase: Match3AnimationPhase,
    activeTileAnimationIds: Set<Long>,
    fallDistanceByTileId: Map<Long, Int>,
    refillTileIds: Set<Long>,
    landingTileIds: Set<Long>
): BoardAnimationSnapshot {
    val previousPositions = previousBoard?.allPositions()
        ?.mapNotNull { pos -> previousBoard.tileAt(pos)?.let { it.id to pos } }
        ?.toMap()
        ?: emptyMap()

    val currentPositions = currentBoard.allPositions()
        .mapNotNull { pos -> currentBoard.tileAt(pos)?.let { it.id to pos } }
        .toMap()

    val removedTileIds = if (phase == Match3AnimationPhase.RemovingMatches) {
        matchedPositions.mapNotNullTo(mutableSetOf()) { currentBoard.tileAt(it)?.id }
    } else {
        emptySet()
    }

    val tiles = currentBoard.allPositions().mapNotNull { pos ->
        val tile = currentBoard.tileAt(pos) ?: return@mapNotNull null
        val fallDistance = fallDistanceByTileId[tile.id] ?: 0
        val previousPosition = previousPositions[tile.id]
            ?: if (refillTileIds.contains(tile.id)) {
                BoardPosition(-fallDistance.coerceAtLeast(1), pos.column)
            } else {
                pos
            }

        val role = when {
            removedTileIds.contains(tile.id) -> TileAnimationRole.Removing
            landingTileIds.contains(tile.id) && phase == Match3AnimationPhase.CheckingCascade -> TileAnimationRole.Landing
            activeTileAnimationIds.contains(tile.id) && phase == Match3AnimationPhase.Swapping -> TileAnimationRole.Swapping
            activeTileAnimationIds.contains(tile.id) && phase == Match3AnimationPhase.InvalidReturning -> TileAnimationRole.InvalidReturning
            refillTileIds.contains(tile.id) -> TileAnimationRole.Refilling
            fallDistance > 0 -> TileAnimationRole.Falling
            selectedPosition == pos -> TileAnimationRole.Selected
            else -> TileAnimationRole.Idle
        }

        VisualTileState(
            tile = tile,
            currentLogicalPosition = pos,
            previousLogicalPosition = previousPosition,
            targetLogicalPosition = pos,
            role = role,
            fallDistance = fallDistance,
            spawnOriginRow = if (refillTileIds.contains(tile.id)) previousPosition.row else null
        )
    }

    return BoardAnimationSnapshot(tiles = tiles, removedTileIds = removedTileIds)
}
