package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.NDimensionalArray
import me.jdvp.adventofcode.util.printResults

object Day12: Daily(year = 2022, day = 12) {
    private fun readInput(): NDimensionalArray<Char> {
        val grid = NDimensionalArray<Char>(
            dimensions = 2,
            defaultValueCalculation = { null }
        )

        getInputText()
            .lines()
            .mapIndexed { x, i ->
                i.toCharArray().mapIndexed { y, c ->
                    grid[x, y] = c
                }
            }
        return grid
    }

    private fun Char.mountainValue(): Int {
        return when(this) {
            CHARACTER_START -> CHARACTER_ELEVATION_LOWEST.code
            CHARACTER_END -> CHARACTER_ELEVATION_HIGHEST.code
            else -> this.code
        }
    }

    private fun isEdge(from: Char?, to: Char?): Boolean {
        return to!!.mountainValue() - 1 <= from!!.mountainValue()
    }

    fun part1(): Int {
        val grid = readInput()
        grid.getSatisfyingIndices {
            it == CHARACTER_START
        }

        return (grid.shortestPath(
            start = grid.getSatisfyingIndices {
                it == CHARACTER_START
            }.first(),
            end = grid.getSatisfyingIndices {
                it == CHARACTER_END
            }.first(),
            isEdge = ::isEdge
        )!!.size - 1).printResults()
    }

    fun part2(): Int {
        val grid = readInput()
        val possibleStartPosition = grid.getSatisfyingIndices {
            it == CHARACTER_START || it == CHARACTER_ELEVATION_LOWEST
        }.toList()
        val endingPosition = grid.getSatisfyingIndices {
            it == CHARACTER_END
        }.first()

        return (possibleStartPosition.mapNotNull { startPosition ->
            grid.shortestPath(
                start = startPosition,
                end = endingPosition,
                isEdge = ::isEdge
            )?.size
        }.min() - 1).printResults()
    }

    private const val CHARACTER_START = 'S'
    private const val CHARACTER_END = 'E'
    private const val CHARACTER_ELEVATION_LOWEST = 'a'
    private const val CHARACTER_ELEVATION_HIGHEST = 'z'
}