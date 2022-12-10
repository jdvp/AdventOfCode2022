package me.jdvp.adventofcode.daily

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10Test {
    @Test
    fun verifyPart1Answer() {
        assertEquals(12540, Day10.part1())
    }

    @Test
    fun verifyPart2Answer() {
        val expected = """
            ####.####..##..####.####.#....#..#.####.
            #....#....#..#....#.#....#....#..#.#....
            ###..###..#......#..###..#....####.###..
            #....#....#.....#...#....#....#..#.#....
            #....#....#..#.#....#....#....#..#.#....
            #....####..##..####.####.####.#..#.####.
        """.trimIndent()
        assertEquals(expected, Day10.part2())
    }
}