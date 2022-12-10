package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults
import kotlin.math.abs
import kotlin.math.sign

object Day9 {
    private fun readInput(): List<Pair<Direction, Int>> {
        return getResourceAsText("Day9Input")
            .lines()
            .map {
                val (a, b) = it.split(" ")
                return@map when (a) {
                    "R" -> Direction.RIGHT
                    "L" -> Direction.LEFT
                    "U" -> Direction.UP
                    else -> Direction.DOWN
                } to b.toInt()
            }.flatMap { pair ->
                MutableList(pair.second) { pair.first to 1 }
            }
    }

    private fun Pair<Int, Int>.isTouching(other: Pair<Int, Int>): Boolean {
        return abs(this.first - other.first) <= 1 && abs(this.second - other.second) <= 1
    }

    private fun Int.moveTowards(other: Int) = this + (other - this).sign

    private fun Pair<Int, Int>.moveTowards(other: Pair<Int, Int>): Pair<Int, Int> {
        return first.moveTowards(other.first) to second.moveTowards(other.second)
    }

    enum class Direction(val applyMovement: (Pair<Int, Int>) -> Pair<Int, Int>) {
        RIGHT({
            it.first + 1 to it.second
        }),
        LEFT({
            it.first - 1 to it.second
        }),
        UP({
            it.first to it.second + 1
        }),
        DOWN({
            it.first to it.second - 1
        })
    }

    data class StepState(
        val tailVisitedPos: List<Pair<Int, Int>>,
        val knots: List<Pair<Int, Int>>
    )

    private fun List<Pair<Direction, Int>>.determineFinalState(
        numberOfKnots: Int
    ): StepState {
        return fold(StepState(
            tailVisitedPos = mutableListOf(0 to 0),
            knots = MutableList(numberOfKnots) { 0 to 0 }
        )) computeState@ { oldState, nextMove ->
            val newKnotPositions = oldState.knots.drop(1)
                .fold(listOf(nextMove.first.applyMovement(oldState.knots.first()))) computeKnotPos@ { previousKnots, knot ->
                    val previousKnot = previousKnots.last()
                    return@computeKnotPos previousKnots + if (knot.isTouching(previousKnot)) {
                        knot
                    } else {
                        knot.moveTowards(previousKnot)
                    }
                }

            return@computeState StepState(
                tailVisitedPos = oldState.tailVisitedPos + listOf(newKnotPositions.last()),
                knots = newKnotPositions
            )
        }
    }

    private fun StepState.getLastKnotVisitedPositionCount(): Int {
        return tailVisitedPos.toSet().size
    }

    fun part1(): Int {
        return readInput()
            .determineFinalState(
                numberOfKnots = 2
            ).getLastKnotVisitedPositionCount()
            .printResults()
    }

    fun part2(): Int {
        return readInput()
            .determineFinalState(
                numberOfKnots = 10
            ).getLastKnotVisitedPositionCount()
            .printResults()
    }
}