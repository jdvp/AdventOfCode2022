package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

/**
 * This code would be awful to have IRL but is for fun so tried to code golf it a bit which ended up with
 * code that is pretty hard for a human to read straight away
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

    private fun Pair<Int, Int>.scoreMatch(): Int {
        //stumbled into this one, not totally sure why it works mathematically, sorry :(
        //the +4 is actually + 1 for 0-based indexing of the score and then + 3 for the second term with the floorMods
        return second + 4 + ((Math.floorMod(first - second, -3) + Math.floorMod(first - second, 3)) * 3)
    }

    //0 -> lose, 1 -> draw, 2 -> win
    private fun Pair<Int, Int>.convertWinConditionToHand(): Pair<Int, Int> {
        //lol can you imagine getting here and wondering what 'first + second -1' means in production code
        //BUT this works bc left shift in the array is a losing hand shape and right shift is a winning hand shape
        return first to Math.floorMod(first + second - 1, 3)
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