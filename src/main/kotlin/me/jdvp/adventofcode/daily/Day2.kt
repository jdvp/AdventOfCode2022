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
            "A", "X" -> 1
            "B", "Y" -> 2
            else -> 3
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

    //r, p, s 1, 2, 3
    private fun Pair<Int, Int>.scoreMatch(): Int {
        return second + when(second - first) {
            -1, 2 -> 0
            1, -2 -> 6
            else -> 3
        }
    }

    //1 -> lose, 2 -> draw, 3 -> win
    private fun Pair<Int, Int>.convertWinConditionToHand(): Pair<Int, Int> {
        return first to when(second) {
            2 -> first
            1 -> ((first + 1) % 3) + 1
            else -> (first % 3) + 1
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