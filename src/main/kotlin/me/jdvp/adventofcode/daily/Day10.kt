package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.printResults
import kotlin.math.abs

object Day10: Daily(year = 2022, day = 10) {
    private fun readInput(): List<Int> {
        return getInputText()
            .lines()
            .flatMap { command ->
                return@flatMap listOf(0) + command.split(" ").run {
                    if (first() == ADD_COMMAND) listOf(last().toInt()) else listOf()
                }
            }.fold(listOf(1)) { previous, amount ->
                previous + (previous.last() + amount)
            }
    }

    fun part1(): Int {
        val registerValues = readInput()
        return (19 until registerValues.size step SCREEN_WIDTH_PIXELS).sumOf { cycle ->
            registerValues[cycle] * (cycle + 1)
        }.printResults()
    }

    fun part2(): String {
        return readInput()
            .mapIndexed { index, x ->
                return@mapIndexed if (abs((index % SCREEN_WIDTH_PIXELS) - x) <= 1) {
                    "#"
                } else {
                    "."
                }
            }.chunked(SCREEN_WIDTH_PIXELS).filter {
                it.size == SCREEN_WIDTH_PIXELS
            }.joinToString("\n") {
                it.joinToString("")
            }.trim().printResults(startOnNewLine = true)
    }

    private const val ADD_COMMAND = "addx"
    private const val SCREEN_WIDTH_PIXELS = 40
}