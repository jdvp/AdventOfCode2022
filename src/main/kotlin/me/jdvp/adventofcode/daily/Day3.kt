package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day3 {
    private fun readInput(): List<String> {
        return getResourceAsText("Day3Input").lines()
    }

    private fun Char.toItemScore(): Int {
        return if (isLowerCase()) {
            1 + code - 'a'.code
        } else {
            27 + code - 'A'.code
        }
    }

    private fun Collection<String>.intersectAll(): Char {
        return map { it.toSet() }.reduce { acc, s ->
            acc.intersect(s)
        }.first()
    }

    private fun List<String>.toMatchingCompartmentItems(): List<Char> {
        return map { rucksack ->
            rucksack.chunked(rucksack.length / 2).intersectAll()
        }
    }

    private fun List<String>.toElfBadges(): List<Char> {
        return chunked(3).map { items ->
            return@map items.intersectAll()
        }
    }

    fun part1(): Int {
        return readInput()
            .toMatchingCompartmentItems()
            .sumOf { item ->
                item.toItemScore()
            }.printResults()
    }

    fun part2(): Int {
        return readInput()
            .toElfBadges()
            .sumOf { item ->
                item.toItemScore()
            }.printResults()
    }
}