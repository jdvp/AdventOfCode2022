package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults
import java.util.*

object Day5 {
    private fun readInput(): List<String> {
        return getResourceAsText("Day5Input").lines()
    }

    private data class CraneMovement(
        val amount: Int,
        val from: Int,
        val to: Int
    )

    private data class LoadingDock(
        val crateStacks: List<Stack<String>>,
        val movements: List<CraneMovement>
    )

    private fun List<String>.getCrateStacks(): List<Stack<String>> {
        val allLines = takeWhile { it.isNotEmpty() }
        val stacks = allLines.last().chunked(4).map {
            Stack<String>()
        }
        allLines.dropLast(1).forEach { line ->
            line.chunked(4).forEachIndexed { stackNumber, potentialCrate ->
                val crate = potentialCrate.filter(Char::isLetter)
                if (crate.isNotEmpty()) {
                    stacks.getOrNull(stackNumber)?.add(0, crate)
                }
            }
        }
        return stacks
    }

    private fun List<String>.getCraneMovements(): List<CraneMovement> {
        return filter {
            it.contains("move")
        }.map {
            it.replace("move ", "")
                .replace("from ", "")
                .replace("to ", "")
                .split(" ")
                .map(String::toInt)
        }.map { specification ->
            CraneMovement(
                amount = specification[0],
                from = specification[1],
                to = specification[2]
            )
        }
    }

    private fun List<String>.toLoadingDock(): LoadingDock {
        return LoadingDock(
            crateStacks = getCrateStacks(),
            movements = getCraneMovements()
        )
    }

    private fun LoadingDock.executeMovements(moveEntireGroup: Boolean): LoadingDock  = apply {
        movements.forEach { movement ->
            for (i in 0 until movement.amount) {
                val movedCrate = crateStacks.getOrNull(movement.from - 1)?.pop()
                if (moveEntireGroup) {
                    crateStacks.getOrNull(movement.to - 1)?.apply {
                        add(size - i, movedCrate)
                    }
                } else {
                    crateStacks.getOrNull(movement.to - 1)?.push(movedCrate)
                }
            }
        }
    }

    private fun LoadingDock.toStackTopString(): String {
        return crateStacks.joinToString(separator = "") {
            it.peek()
        }
    }

    fun part1(): String {
        return readInput()
            .toLoadingDock()
            .executeMovements(moveEntireGroup = false)
            .toStackTopString()
            .printResults()
    }

    fun part2(): String {
        return readInput()
            .toLoadingDock()
            .executeMovements(moveEntireGroup = true)
            .toStackTopString()
            .printResults()
    }
}