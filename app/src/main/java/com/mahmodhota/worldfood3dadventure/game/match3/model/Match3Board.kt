package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Immutable representation of the match-3 game board.
 */
class Match3Board(
    val rows: Int,
    val columns: Int,
    private val tiles: List<FoodTile>
) {
    init {
        require(tiles.size == rows * columns) { "Tile list size must match rows * columns" }
    }

    /**
     * Checks if a position is within the board bounds.
     */
    fun contains(position: BoardPosition): Boolean {
        return position.row in 0 until rows && position.column in 0 until columns
    }

    /**
     * Safely retrieves a tile at a specific position.
     */
    fun tileAt(position: BoardPosition): FoodTile? {
        if (!contains(position)) return null
        return tiles[position.row * columns + position.column]
    }

    /**
     * Returns a list of all valid positions on the board.
     */
    fun allPositions(): List<BoardPosition> {
        return (0 until rows).flatMap { r ->
            (0 until columns).map { c -> BoardPosition(r, c) }
        }
    }

    /**
     * Swaps tiles at two positions and returns a new board instance.
     */
    fun swap(pos1: BoardPosition, pos2: BoardPosition): Match3Board {
        if (!contains(pos1) || !contains(pos2)) return this
        
        val newTiles = tiles.toMutableList()
        val index1 = pos1.row * columns + pos1.column
        val index2 = pos2.row * columns + pos2.column
        
        val temp = newTiles[index1]
        newTiles[index1] = newTiles[index2]
        newTiles[index2] = temp
        
        return Match3Board(rows, columns, newTiles.toList())
    }

    /**
     * Replaces a tile at a position and returns a new board instance.
     */
    fun replace(position: BoardPosition, tile: FoodTile): Match3Board {
        if (!contains(position)) return this
        
        val newTiles = tiles.toMutableList()
        newTiles[position.row * columns + position.column] = tile
        
        return Match3Board(rows, columns, newTiles.toList())
    }

    /**
     * Replaces multiple tiles and returns a new board instance.
     */
    fun replaceAll(replacements: Map<BoardPosition, FoodTile>): Match3Board {
        val newTiles = tiles.toMutableList()
        replacements.forEach { (pos, tile) ->
            if (contains(pos)) {
                newTiles[pos.row * columns + pos.column] = tile
            }
        }
        return Match3Board(rows, columns, newTiles.toList())
    }

    /**
     * Damages a blocker at the specified position if it exists.
     * Returns a new board if changed, or the same board if no blocker was present.
     */
    fun damageBlocker(position: BoardPosition): Match3Board {
        val tile = tileAt(position) ?: return this
        if (tile.blocker == BlockerState.NONE) return this
        
        return replace(position, tile.copy(blocker = tile.blocker.damage()))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Match3Board) return false
        if (rows != other.rows) return false
        if (columns != other.columns) return false
        return tiles == other.tiles
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + tiles.hashCode()
        return result
    }
}
