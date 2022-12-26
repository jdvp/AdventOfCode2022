package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(4793062, Day15.part1())
    }

    @Test
    fun verifyPart2Answer() {
        assertEquals(10826395253551L, Day15.part2())
    }
}