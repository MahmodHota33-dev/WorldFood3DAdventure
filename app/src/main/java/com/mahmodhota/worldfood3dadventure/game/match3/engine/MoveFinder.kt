package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board

/**
 * Scans a stable board for possible valid swaps.
 */
object MoveFinder {

    /**
     * Returns true if at least one valid swap exists.
     */
    fun hasValidMove(board: Match3Board): Boolean {
        // Iterate through all possible horizontal swaps
        for (r in 0 until board.rows) {
            for (c in 0 until board.columns - 1) {
                val p1 = BoardPosition(r, c)
                val p2 = BoardPosition(r, c + 1)
                if (wouldCreateMatch(board, p1, p2)) return true
            }
        }

        // Iterate through all possible vertical swaps
        for (c in 0 until board.columns) {
            for (r in 0 until board.rows - 1) {
                val p1 = BoardPosition(r, c)
                val p2 = BoardPosition(r + 1, c)
                if (wouldCreateMatch(board, p1, p2)) return true
            }
        }

        return false
    }

    fun wouldCreateMatch(board: Match3Board, pos1: BoardPosition, pos2: BoardPosition): Boolean {
        val t1 = board.tileAt(pos1) ?: return false
        val t2 = board.tileAt(pos2) ?: return false

        // t1 is moving to pos2, t2 is moving to pos1
        if (checkVirtualMatch(board, pos1.row, pos1.column, t2.type, pos1, pos2, t1.type, t2.type)) return true
        if (checkVirtualMatch(board, pos2.row, pos2.column, t1.type, pos1, pos2, t1.type, t2.type)) return true
        
        return false
    }

    private fun checkVirtualMatch(
        board: Match3Board,
        row: Int,
        col: Int,
        matchType: FoodTileType,
        p1: BoardPosition,
        p2: BoardPosition,
        t1Type: FoodTileType,
        t2Type: FoodTileType
    ): Boolean {
        // Horizontal
        var hCount = 1
        // Left
        for (c in col - 1 downTo 0) {
            val cur = BoardPosition(row, c)
            val type = if (cur == p1) t2Type else if (cur == p2) t1Type else board.tileAt(cur)?.type
            if (type == matchType) hCount++ else break
        }
        // Right
        for (c in col + 1 until board.columns) {
            val cur = BoardPosition(row, c)
            val type = if (cur == p1) t2Type else if (cur == p2) t1Type else board.tileAt(cur)?.type
            if (type == matchType) hCount++ else break
        }
        if (hCount >= 3) return true

        // Vertical
        var vCount = 1
        // Up
        for (r in row - 1 downTo 0) {
            val cur = BoardPosition(r, col)
            val type = if (cur == p1) t2Type else if (cur == p2) t1Type else board.tileAt(cur)?.type
            if (type == matchType) vCount++ else break
        }
        // Down
        for (r in row + 1 until board.rows) {
            val cur = BoardPosition(r, col)
            val type = if (cur == p1) t2Type else if (cur == p2) t1Type else board.tileAt(cur)?.type
            if (type == matchType) vCount++ else break
        }
        if (vCount >= 3) return true

        return false
    }

    /**
     * Finds all valid moves on the board.
     */
    fun findValidMoves(board: Match3Board): List<Pair<BoardPosition, BoardPosition>> {
        val moves = mutableListOf<Pair<BoardPosition, BoardPosition>>()
        
        // Horizontal
        for (r in 0 until board.rows) {
            for (c in 0 until board.columns - 1) {
                val p1 = BoardPosition(r, c)
                val p2 = BoardPosition(r, c + 1)
                if (wouldCreateMatch(board, p1, p2)) moves.add(p1 to p2)
            }
        }

        // Vertical
        for (c in 0 until board.columns) {
            for (r in 0 until board.rows - 1) {
                val p1 = BoardPosition(r, c)
                val p2 = BoardPosition(r + 1, c)
                if (wouldCreateMatch(board, p1, p2)) moves.add(p1 to p2)
            }
        }

        return moves
    }
}
