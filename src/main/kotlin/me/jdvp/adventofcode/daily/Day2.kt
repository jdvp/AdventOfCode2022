package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

/**
 * This code would be awful to have IRL but is for fun so tried to code golf it a bit which ended up with
 * code that is pretty hard for a human to read straight away
 */
object Day2 {
    private fun readInput(): List<Pair<Int, Int>> {
        return getResourceAsText("Day2Input")
            .lines()
            .asSequence()
            .map(String::toCharArray)
            .flatMap(CharArray::toList)
            .filter(Char::isLetter)
            .map { it.toHandScore() }
            .chunked(2)
            .map {
                it.first() to it.last()
            }.toList()
    }

    private fun Char.toHandScore(): Int {
        return min(abs('A'.code - code), abs('X'.code - code))
    }

    private fun Pair<Int, Int>.scoreMatch(): Int {
        //stumbled into this one, not totally sure why it works mathematically, sorry :(
        //the +4 is actually + 1 for 0-based indexing of the score and then + 3 for the second term with the floorMods
        return second + 4 + ((((first - second) % 3) * 2 + (-1 * (first - second).sign * 3)) * 3)
    }

    //0 -> lose, 1 -> draw, 2 -> win
    private fun Pair<Int, Int>.convertWinConditionToHand(): Pair<Int, Int> {
        //lol can you imagine getting here and wondering what 'first + second -1' means in production code
        //BUT this works bc left shift in the array is a losing hand shape and right shift is a winning hand shape
        return first to Math.floorMod(first + second - 1, 3)
    }

    fun part1(): Int {
        return readInput().sumOf {
            it.scoreMatch()
        }.printResults()
    }

    fun part2(): Int {
        return readInput().sumOf {
            it.convertWinConditionToHand().scoreMatch()
        }.printResults()
    }
}