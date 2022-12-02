package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

/**
 * This code would be awful to have IRL, but I felt having enums etc. was too much for this.
 * Even considering that, there is probably a better 'mathy' way to do it.
 */
object Day2 {
    private fun readInput(): List<Pair<Int, Int>> {
        return getResourceAsText("Day2Input")
            .lines().map {
                val pair = it.split(" ")
                return@map pair.first().toHandScore() to pair.last().toHandScore()
            }
    }

    private fun String.toHandScore(): Int {
        return when(this) {
            "A", "X" -> 0
            "B", "Y" -> 1
            else -> 2
        }
    }

    //losing
    //r - p = -1
    //p - s = -1
    //s - r = 2 - 0 = 2

    //winning
    //r - s = 0 - 2 = -2
    //p - r = 1
    //s - p = 1

    //r, p, s, 0, 1, 2
    private fun Pair<Int, Int>.scoreMatch(): Int {
        return (second + 1) + when(second - first) {
            -1, 2 -> 0
            1, -2 -> 6
            else -> 3
        }
    }

    //0 -> lose, 1 -> draw, 2 -> win
    private fun Pair<Int, Int>.convertWinConditionToHand(): Pair<Int, Int> {
        return first to when(second) {
            1 -> first
            2 -> Math.floorMod(first + 1, 3)
            else -> Math.floorMod(first - 1, 3)
        }.apply {
            println("first: $first, second: $second, result: $this")
        }
    }

    fun part1(): Int {
        return readInput().sumOf { it.scoreMatch() }.printResults()
    }

    fun part2(): Int {
        return readInput().sumOf {
            it.convertWinConditionToHand().scoreMatch()
        }.printResults()
    }
}