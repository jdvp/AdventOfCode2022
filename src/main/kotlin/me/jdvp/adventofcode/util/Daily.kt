package me.jdvp.adventofcode.util

import java.io.File

abstract class Daily(
    private val year: Int, private val day: Int
) {
    fun getInputText(): String {
        return File("src/main/resources/inputs/$year/Day$day.txt").readText()
    }
}