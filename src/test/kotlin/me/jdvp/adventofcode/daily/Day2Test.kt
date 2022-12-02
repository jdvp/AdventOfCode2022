package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day2Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(11603, Day2.part1())
    }

    @Test
    fun verifyPart2Answer() {
        assertEquals(12725, Day2.part2())
    }
}