package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.printResults

object Day1: Daily(year = 2022, day = 1) {
    private fun getElfCalories(): Sequence<Long> {
        return getInputText().splitToSequence(
            "\r\n\r\n", "\n\n", "\r\r"
        ).map {
            it.lineSequence().sumOf(String::toLong)
        }
    }

    fun part1(): Long {
        return getElfCalories().max().printResults()
    }

    fun part2(): Long {
        return getElfCalories().sortedDescending().take(3).sum().printResults()
    }
}