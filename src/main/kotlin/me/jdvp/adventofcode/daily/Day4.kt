package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day4 {
    private fun readInput(): List<String> {
        return getResourceAsText("Day4Input").lines()
    }

    private fun List<String>.toTimeSlots(): List<List<Int>> = map {
        return@map it.split(",", "-").map(String::toInt)
    }

    private fun List<Int>.timeSlotIntersects(fullIntersection: Boolean): Boolean {
        //not super efficient but didn't wanna math
        val worker1 = (get(0) .. get(1)).toSet()
        val worker2 = (get(2) .. get(3)).toSet()
        return if (fullIntersection) {
            worker1.intersect(worker2).size in arrayOf(worker1.size, worker2.size)
        } else {
            worker1.intersect(worker2).isNotEmpty()
        }
    }

    fun part1(): Int {
        return readInput()
            .toTimeSlots()
            .count {
                it.timeSlotIntersects(fullIntersection = true)
            }.printResults()
    }

    fun part2(): Int {
        return readInput()
            .toTimeSlots()
            .count {
                it.timeSlotIntersects(fullIntersection = false)
            }.printResults()
    }
}