package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day3 {
    private fun readInput(): List<String> {
        return getResourceAsText("Day3Input").lines()
    }

    private fun intersectAll(vararg charSequence: CharSequence): Char {
        return charSequence.map { it.toSet() }.reduce { acc, cs ->
            acc.intersect(cs)
        }.first()
    }

    private fun String.getCompartmentMatchingItem(): Char {
        val compartment1 = subSequence(0, length / 2)
        val compartment2 = subSequence(length / 2, length)
        return intersectAll(compartment1, compartment2)
    }

    private fun List<String>.toMatchingCompartmentItems(): List<Char> {
        return map { rucksack ->
            rucksack.getCompartmentMatchingItem()
        }
    }

    private fun Char.toItemScore(): Int {
        return if (isLowerCase()) {
            1 + code - 'a'.code
        } else {
            27 + code - 'A'.code
        }
    }

    private fun List<String>.toElfBadges(): List<Char> {
        return (indices step 3).map { groupIndex ->
            val elf1 = get(groupIndex)
            val elf2 = get(groupIndex + 1)
            val elf3 = get(groupIndex + 2)
            return@map intersectAll(elf1, elf2, elf3)
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