package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.*

/**
 * Scans the board for horizontal and vertical matches.
 */
object MatchDetector {

    /**
     * Finds all matches on the current board.
     */
    fun findMatches(
        board: Match3Board,
        preferredPositions: List<BoardPosition> = emptyList()
    ): MatchResult {
        val horizontalGroups = findHorizontalGroups(board)
        val verticalGroups = findVerticalGroups(board)

        val uniquePositions = mutableSetOf<BoardPosition>()
        uniquePositions.addAll(horizontalGroups.flatMap { it.positions })
        uniquePositions.addAll(verticalGroups.flatMap { it.positions })

        val finalGroups = mutableListOf<MatchGroup>()
        val specialSpawns = mutableMapOf<BoardPosition, SpecialTileType>()

        // 1. Process T/L Shapes (Intersections)
        // A T/L shape occurs when a position belongs to both a horizontal and vertical group of same type
        val processedHorizontal = mutableSetOf<MatchGroup>()
        val processedVertical = mutableSetOf<MatchGroup>()

        horizontalGroups.forEach { hGroup ->
            verticalGroups.forEach { vGroup ->
                if (hGroup.type == vGroup.type) {
                    val intersection = hGroup.positions.intersect(vGroup.positions)
                    if (intersection.isNotEmpty()) {
                        val combinedPositions = hGroup.positions + vGroup.positions
                        val spawnPoint = chooseSpawnPoint(
                            candidates = intersection,
                            preferredPositions = preferredPositions
                        )
                        finalGroups.add(MatchGroup(combinedPositions, MatchDirection.HORIZONTAL, hGroup.type, SpecialTileType.BOMB, spawnPoint))
                        specialSpawns[spawnPoint] = SpecialTileType.BOMB
                        processedHorizontal.add(hGroup)
                        processedVertical.add(vGroup)
                    }
                }
            }
        }

        // 2. Process Line Matches (5 and 4)
        horizontalGroups.filter { it !in processedHorizontal }.forEach { group ->
            val spawnType = when (group.length) {
                in 5..Int.MAX_VALUE -> SpecialTileType.COLOR_BOMB
                4 -> SpecialTileType.ROW_CLEAR
                else -> SpecialTileType.NONE
            }
            val spawnPoint = if (spawnType == SpecialTileType.NONE) {
                null
            } else {
                chooseSpawnPoint(group.positions, preferredPositions)
            }
            finalGroups.add(group.copy(creationType = spawnType, creationPoint = if (spawnType != SpecialTileType.NONE) spawnPoint else null))
            if (spawnType != SpecialTileType.NONE && spawnPoint != null) specialSpawns[spawnPoint] = spawnType
        }

        verticalGroups.filter { it !in processedVertical }.forEach { group ->
            val spawnType = when (group.length) {
                in 5..Int.MAX_VALUE -> SpecialTileType.COLOR_BOMB
                4 -> SpecialTileType.COLUMN_CLEAR
                else -> SpecialTileType.NONE
            }
            val spawnPoint = if (spawnType == SpecialTileType.NONE) {
                null
            } else {
                chooseSpawnPoint(group.positions, preferredPositions)
            }
            finalGroups.add(group.copy(creationType = spawnType, creationPoint = if (spawnType != SpecialTileType.NONE) spawnPoint else null))
            if (spawnType != SpecialTileType.NONE && spawnPoint != null) specialSpawns[spawnPoint] = spawnType
        }

        return MatchResult(finalGroups, uniquePositions, specialSpawns)
    }

    /**
     * Efficiently checks if the board contains at least one match.
     * Stops immediately when a match is found.
     */
    fun hasAnyMatch(board: Match3Board): Boolean {
        // Horizontal scan
        for (r in 0 until board.rows) {
            var count = 1
            for (c in 1 until board.columns) {
                val t1 = board.tileAt(BoardPosition(r, c - 1))
                val t2 = board.tileAt(BoardPosition(r, c))
                if (t1 != null && t2 != null && t1.type == t2.type) {
                    count++
                    if (count >= 3) return true
                } else {
                    count = 1
                }
            }
        }
        // Vertical scan
        for (c in 0 until board.columns) {
            var count = 1
            for (r in 1 until board.rows) {
                val t1 = board.tileAt(BoardPosition(r - 1, c))
                val t2 = board.tileAt(BoardPosition(r, c))
                if (t1 != null && t2 != null && t1.type == t2.type) {
                    count++
                    if (count >= 3) return true
                } else {
                    count = 1
                }
            }
        }
        return false
    }

    /**
     * Checks if there is a match intersecting a specific position.
     */
    fun hasMatchAt(board: Match3Board, pos: BoardPosition): Boolean {
        val tile = board.tileAt(pos) ?: return false
        
        // Horizontal check around pos
        var hCount = 1
        // Look left
        var c = pos.column - 1
        while (c >= 0) {
            if (board.tileAt(BoardPosition(pos.row, c))?.type == tile.type) {
                hCount++
                c--
            } else break
        }
        // Look right
        c = pos.column + 1
        while (c < board.columns) {
            if (board.tileAt(BoardPosition(pos.row, c))?.type == tile.type) {
                hCount++
                c++
            } else break
        }
        if (hCount >= 3) return true

        // Vertical check around pos
        var vCount = 1
        // Look up
        var r = pos.row - 1
        while (r >= 0) {
            if (board.tileAt(BoardPosition(r, pos.column))?.type == tile.type) {
                vCount++
                r--
            } else break
        }
        // Look down
        r = pos.row + 1
        while (r < board.rows) {
            if (board.tileAt(BoardPosition(r, pos.column))?.type == tile.type) {
                vCount++
                r++
            } else break
        }
        return vCount >= 3
    }

    private fun chooseSpawnPoint(
        candidates: Set<BoardPosition>,
        preferredPositions: List<BoardPosition>
    ): BoardPosition {
        preferredPositions.firstOrNull { it in candidates }?.let { return it }
        return candidates
            .sortedWith(compareBy<BoardPosition>({ it.row }, { it.column }))
            .let { sorted -> sorted[sorted.size / 2] }
    }

    private fun findHorizontalGroups(board: Match3Board): List<MatchGroup> {
        val groups = mutableListOf<MatchGroup>()
        for (r in 0 until board.rows) {
            var c = 0
            while (c < board.columns) {
                val startTile = board.tileAt(BoardPosition(r, c))
                if (startTile == null) {
                    c++
                    continue
                }

                var matchLength = 1
                while (c + matchLength < board.columns) {
                    val nextTile = board.tileAt(BoardPosition(r, c + matchLength))
                    if (nextTile != null && nextTile.type == startTile.type) {
                        matchLength++
                    } else {
                        break
                    }
                }

                if (matchLength >= 3) {
                    val positions = (0 until matchLength).map { BoardPosition(r, c + it) }.toSet()
                    groups.add(MatchGroup(positions, MatchDirection.HORIZONTAL, startTile.type))
                    c += matchLength
                } else {
                    c++
                }
            }
        }
        return groups
    }

    private fun findVerticalGroups(board: Match3Board): List<MatchGroup> {
        val groups = mutableListOf<MatchGroup>()
        for (c in 0 until board.columns) {
            var r = 0
            while (r < board.rows) {
                val startTile = board.tileAt(BoardPosition(r, c))
                if (startTile == null) {
                    r++
                    continue
                }

                var matchLength = 1
                while (r + matchLength < board.rows) {
                    val nextTile = board.tileAt(BoardPosition(r + matchLength, c))
                    if (nextTile != null && nextTile.type == startTile.type) {
                        matchLength++
                    } else {
                        break
                    }
                }

                if (matchLength >= 3) {
                    val positions = (0 until matchLength).map { BoardPosition(r + it, c) }.toSet()
                    groups.add(MatchGroup(positions, MatchDirection.VERTICAL, startTile.type))
                    r += matchLength
                } else {
                    r++
                }
            }
        }
        return groups
    }
}
