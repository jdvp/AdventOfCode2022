package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day7Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(1642503, Day7.part1())
    }

    @Test
    fun verifyPart2Answer() {
        assertEquals(6999588, Day7.part2())
    }
}