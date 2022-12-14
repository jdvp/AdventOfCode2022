package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.NDimensionalArray
import me.jdvp.adventofcode.util.printResults
import kotlin.math.abs

object Day18: Daily(year = 2022, day = 18) {
    private const val LAVA_DROPLET = 18

    private fun readInput(): NDimensionalArray<Int> {
        val grid = NDimensionalArray<Int>(
            dimensions = 3
        )
        getInputText()
            .lines()
            .map { line ->
                val (x, y, z) = line.split(",").map(String::toInt)
                grid[x, y, z] = LAVA_DROPLET
            }
        return grid
    }

    private fun NDimensionalArray<Int>.getLavaDroplets(): List<List<Int>> {
        return getSatisfyingIndices {
            it == LAVA_DROPLET
        }.toList()
    }

    fun part1(): Int {
        val grid = readInput()
        return grid.getLavaDroplets().sumOf {
            6 - grid.getNeighborsOf(it).count()
        }.printResults()
    }

    fun part2(): Int {
        val grid = readInput()
        val lavaDroplets = grid.getLavaDroplets()
        val minX = lavaDroplets.minOf { it[0] }
        val minY = lavaDroplets.minOf { it[1] }
        val minZ = lavaDroplets.minOf { it[2] }
        val maxX = lavaDroplets.maxOf { it[0] }
        val maxY = lavaDroplets.maxOf { it[1] }
        val maxZ = lavaDroplets.maxOf { it[2] }

        for(x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ .. maxZ) {
                    val itemIndex = listOf(x, y, z)
                    val v = grid[itemIndex]
                    val probableClosestVoid = listOf(
                        abs(x - (minX - 1)) to listOf(minX - 1, y, z),
                        abs(x - (maxX + 1)) to listOf(maxX + 1, y, z),
                        abs(y - (minY - 1)) to listOf(x, minY - 1, z),
                        abs(y - (maxY + 1)) to listOf(x, maxY + 1, z),
                        abs(z - (minZ - 1)) to listOf(x, y, minZ - 1),
                        abs(z - (maxZ + 1)) to listOf(x, y, maxZ + 1)
                    ).minBy { it.first }.second

                    if (v != LAVA_DROPLET && grid.shortestPath(
                        start = listOf(x, y, z),
                        end = probableClosestVoid, //just somewhere in the 'void'
                        isEdge = { from, to ->
                            from != LAVA_DROPLET && to != LAVA_DROPLET
                        },
                        filterUnfilledItems = false
                    ) == null) {
                        grid[x, y, z] = LAVA_DROPLET
                    }
                }
            }
        }

        return grid.getLavaDroplets().sumOf {
            6 - grid.getNeighborsOf(it).count()
        }.printResults()
    }
}