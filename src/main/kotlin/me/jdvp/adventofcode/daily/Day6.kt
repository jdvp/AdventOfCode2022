package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.printResults

object Day6: Daily(year = 2022, day = 6) {
    private fun parseInputForMessageMarker(
        requiredDistinctCharactersInMarker: Int
    ): Int {
        return getInputText().lines().first()
            .windowed(requiredDistinctCharactersInMarker)
            .indexOfFirst {
                it.toSet().size == requiredDistinctCharactersInMarker
            } + requiredDistinctCharactersInMarker
    }

    fun part1(): Int {
        return parseInputForMessageMarker(
            requiredDistinctCharactersInMarker = 4
        ).printResults()
    }

    fun part2(): Int {
        return parseInputForMessageMarker(
            requiredDistinctCharactersInMarker = 14
        ).printResults()
    }
}