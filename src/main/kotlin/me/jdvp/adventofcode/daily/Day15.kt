package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day15 {
    private const val LOWER_LIMIT = 0
    private const val UPPER_LIMIT = 4_000_000

    private data class Sensor(
        val x: Int,
        val y: Int,
        val closestBeaconX: Int,
        val closestBeaconY: Int
    ) {
        val manhattanDistance = abs(x - closestBeaconX) + abs(y - closestBeaconY)
    }

    private fun readInput(): List<Sensor> {
        return getResourceAsText("Day15Input")
            .lines()
            .map { line ->
                val (a, b, c, d) = line.split(" ", ",", ":").filter { part ->
                    part.contains("x=") || part.contains("y=")
                }.map {
                    it.removePrefix("x=").removePrefix("y=").toInt()
                }
                Sensor(a, b, c, d)
            }
    }

    private infix fun IntRange.overlaps(other: IntRange): Boolean {
        return contains(other.first) || contains(other.last)
    }

    private infix fun IntRange.combineWith(other: IntRange): IntRange {
        return (minOf(this.first, other.first)..maxOf(this.last, other.last))
    }

    private fun List<IntRange>.combineHelper(): List<IntRange> {
        return filterNot { it == IntRange.EMPTY }.fold(mutableListOf()) { acc, intRange ->
            val newRange = acc.filter {
                it overlaps intRange
            }.fold(intRange) { updatedRange, otherRange ->
                acc.remove(otherRange)
                updatedRange combineWith otherRange
            }
            acc.add(newRange)
            return@fold acc
        }
    }

    private fun List<IntRange>.combineWherePossible(): List<IntRange> {
        return combineHelper().reversed().combineHelper()
    }

    private fun Sensor.getCoverageForY(
        y: Int,
        lowerLimit: Int = -1,
        upperLimit: Int = -1
    ): IntRange {
        val distFromLine = abs(this.y - y)
        if (distFromLine > manhattanDistance) {
            return IntRange.EMPTY
        }
        val offset = manhattanDistance - distFromLine

        val lower = if (lowerLimit == -1) {
            this.x - offset
        } else {
            max(lowerLimit, this.x - offset)
        }

        val upper = if (upperLimit == -1) {
            this.x + offset
        } else {
            min(upperLimit, this.x + offset)
        }

        return lower..upper
    }

    private fun List<Sensor>.determineTuningFrequency(
        lowerLimit: Int,
        upperLimit: Int
    ): Long? {
        (lowerLimit..upperLimit).forEach { y ->
            val coverage = map {
                it.getCoverageForY(
                    y = y,
                    lowerLimit = lowerLimit,
                    upperLimit = upperLimit
                )
            }
            val coverageSize = coverage.combineWherePossible().sumOf { it.last - it.first }
            if (coverageSize != UPPER_LIMIT) {
                val hardware = getBeaconsAndSensorsForY(y)
                (lowerLimit..upperLimit).firstOrNull { x ->
                    x !in hardware && !coverage.any { it.contains(x) }
                }?.apply {
                    return (this * UPPER_LIMIT.toLong() + y)
                }
            }
        }

        return null
    }

    private fun List<Sensor>.getBeaconsAndSensorsForY(y: Int): Set<Int> {
        return (filter {
            it.y == y
        }.map { it.x } + filter {
            it.closestBeaconY == y
        }.map { it.closestBeaconX }).toSet()
    }

    fun part1(): Int {
        val sensors = readInput()
        val y = 2_000_000
        return sensors.map {
            it.getCoverageForY(y)
        }.combineWherePossible()
            .flatten()
            .distinct()
            .subtract(sensors.getBeaconsAndSensorsForY(y))
            .size.printResults()
    }

    fun part2(): Long? {
        return readInput().determineTuningFrequency(
            lowerLimit = LOWER_LIMIT,
            upperLimit = UPPER_LIMIT
        ).printResults()
    }
}