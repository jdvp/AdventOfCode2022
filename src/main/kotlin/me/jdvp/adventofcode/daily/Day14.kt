package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.NDimensionalArray
import me.jdvp.adventofcode.util.printResults
import me.jdvp.adventofcode.util.toward

object Day14: Daily(year = 2022, day = 14) {
    private class RegolithReservoir: NDimensionalArray<Char>(
        dimensions = 2,
        defaultValueCalculation = { '.' }
    ) {
        val abyssLevel: Int by lazy {
            getSatisfyingIndices {
                it == ROCK
            }.maxOf { it[1] } + 2
        }

        private fun List<Int>.getNextFallingPosition(): List<Int>? {
            val x = get(0)
            val y = get(1)
            return sequenceOf(
                listOf(x, y + 1),
                listOf(x - 1, y + 1),
                listOf(x + 1, y + 1),
            ).filter {
                it[1] <= abyssLevel
            }.firstOrNull {
                get(it) == defaultValueCalculation()
            }
        }

        fun addSand(
            infiniteAbyss: Boolean = true
        ): Boolean {
            val fallingSand = listOf(500, 0)
            clearItem(fallingSand)
            var pos: List<Int> = fallingSand
            while(pos.getNextFallingPosition() != null) {
                pos = pos.getNextFallingPosition()!!
            }
            if (pos[1] >= abyssLevel && infiniteAbyss) {
                return false
            } else {
                set(*pos.toIntArray(), value = SAND)
            }
            return true
        }
    }

    private fun readInput(): RegolithReservoir {
        val grid = RegolithReservoir()

        getInputText()
            .lines()
            .forEach { line ->
                line.split(" -> ").map { element ->
                    element.split(",").map(String::toInt)
                }.windowed(size = 2).forEach { window ->
                    val (from, to) = window
                    for (x in from[0] toward to[0]) {
                        for (y in from[1] toward to[1] ) {
                            grid[x, y] = ROCK
                        }
                    }
                }
            }
        return grid
    }

    fun part1(): Int {
        val grid = readInput()
        while(true) {
           if (!grid.addSand(infiniteAbyss = true)) {
               break
           }
        }
        //grid.flipAxes(shiftAxes = listOf(-450, 0)).print()
        return grid.getSatisfyingIndices {
            it == SAND
        }.toList().size.printResults()
    }

    fun part2(): Int {
        val grid = readInput()
        while(true) {
            if (!grid.addSand(infiniteAbyss = false) || grid[500, 0] == SAND) {
                break
            }
        }
        //grid.flipAxes(shiftAxes = listOf(-425, 0)).print()
        return grid.getSatisfyingIndices {
            it == SAND
        }.filter {
            it[1] < grid.abyssLevel
        }.toList().size.printResults()
    }

    private const val ROCK = '#'
    private const val SAND = 'o'
}