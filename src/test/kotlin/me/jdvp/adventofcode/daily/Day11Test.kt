package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(56350L, Day11.part1())
    }

    @Test
    fun verifyPart2Answer() {
        assertEquals(13954061248L, Day11.part2())
    }
}