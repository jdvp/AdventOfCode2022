package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day8 {
    private fun readInput(): List<List<Int>> {
        return getResourceAsText("Day8Input")
            .lines()
            .map {
                it.map(Char::digitToInt)
            }
    }

    enum class Neighbors(
        val find: (List<List<Int>>, Int, Int) -> List<Int>
    ) {
        TOP({ grid, x, y ->
            (x - 1 downTo 0).map { grid[it][y] }
        }),
        BOTTOM({ grid, x, y ->
            (x + 1..grid.lastIndex).map { grid[it][y] }
        }),
        LEFT({ grid, x, y ->
            (y - 1 downTo 0).map { grid[x][it] }
        }),
        RIGHT({ grid, x, y ->
            (y + 1..grid[x].lastIndex).map { grid[x][it] }
        });
    }

    private fun List<List<Int>>.toCoordinates(): List<Pair<Int, Int>> {
        return (0..lastIndex).flatMap { y ->
            (0..this[y].lastIndex).map { x ->
                x to y
            }
        }
    }

    private fun List<List<Int>>.isEdgePosition(x: Int, y: Int): Boolean {
        return x == 0 || x == this.lastIndex || y == 0 || y == this[x].lastIndex
    }

    private fun List<List<Int>>.isVisibleFromEdge(x: Int, y: Int): Boolean {
        if (isEdgePosition(x, y)) {
            return true
        }
        val height = this[x][y]
        if (height == 0) {
            return false
        }

        return Neighbors.values().any { cardinalNeighborDirection ->
            cardinalNeighborDirection.find(this, x, y).all { otherHeight ->
                otherHeight < height
            }
        }
    }

    private fun List<List<Int>>.calculateViewingScore(x: Int, y: Int): Int {
        if (isEdgePosition(x, y)) {
            return 0
        }
        val height = this[x][y]
        return Neighbors.values().map { cardinalNeighborDirection ->
            return@map cardinalNeighborDirection.find(this, x, y)
                .takeWhile { it <= height }
                .fold(0) { acc, i ->
                    if (i == height) {
                        return@map acc + 1
                    }
                    return@fold acc + 1
                }
        }.reduce(Int::times)
    }

    fun part1(): Int {
        val grid = readInput()
        return grid.toCoordinates().map { coordinate ->
            grid.isVisibleFromEdge(coordinate.first, coordinate.second)
        }.count { it }.printResults()
    }

    fun part2(): Int {
        val grid = readInput()
        return grid.toCoordinates().maxOf { coordinate ->
            grid.calculateViewingScore(coordinate.first, coordinate.second)
        }.printResults()
    }
}