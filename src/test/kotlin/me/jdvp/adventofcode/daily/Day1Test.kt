package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day1Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(69206, Day1.part1())
    }

    @Test
    fun verifyPart2Answer() {
        assertEquals(197400, Day1.part2())
    }
}