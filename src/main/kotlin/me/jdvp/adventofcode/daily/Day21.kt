package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.Daily
import me.jdvp.adventofcode.util.printResults

object Day21 : Daily(year = 2022, day = 21) {
    private const val ROOT_MONKEY = "root"
    private const val HUMAN = "humn"

    sealed interface Monkey {
        data class Shouter(
            val value: Long
        ) : Monkey

        data class Waiter(
            val monkey1Name: String,
            val operation: String,
            val monkey2Name: String
        ) : Monkey

        data class Root(
            val monkey1Name: String,
            val operation: String,
            val monkey2Name: String
        ) : Monkey

        data class Human(
            val value: Long
        ): Monkey
    }

    private fun getMonkeys(): Map<String, Monkey> {
        return getInputText()
            .lines()
            .associate { line ->
                val (name, action) = line.split(": ")
                return@associate if (name == HUMAN) {
                    name to Monkey.Human(action.toLong())
                } else if (action.all { it.isDigit() }) {
                    name to Monkey.Shouter(action.toLong())
                } else {
                    val (monkey1Name, operation, monkey2Name) = action.split(" ")
                    name to if (name == ROOT_MONKEY) {
                        Monkey.Root(
                            monkey1Name = monkey1Name,
                            operation = operation,
                            monkey2Name = monkey2Name
                        )
                    } else {
                        Monkey.Waiter(
                            monkey1Name = monkey1Name,
                            operation = operation,
                            monkey2Name = monkey2Name
                        )
                    }
                }
            }
    }

    private fun String.performOperation(v1: Long, v2: Long): Long {
        return when (this) {
            "+" -> v1 + v2
            "-" -> v1 - v2
            "/" -> v1 / v2
            else -> v1 * v2
        }
    }

    private fun Map<String, Monkey>.getPart1Value(name: String): Long {
        return when (val monkey = this[name]) {
            null -> 0L
            is Monkey.Shouter -> monkey.value
            is Monkey.Human -> monkey.value
            is Monkey.Waiter -> {
                val v1 = getPart1Value(monkey.monkey1Name)
                val v2 = getPart1Value(monkey.monkey2Name)
                return monkey.operation.performOperation(v1, v2)
            }
            is Monkey.Root -> {
                val v1 = getPart1Value(monkey.monkey1Name)
                val v2 = getPart1Value(monkey.monkey2Name)
                return monkey.operation.performOperation(v1, v2)
            }
        }
    }


    fun part1(): Long {
        getMonkeys().getPart1Value(ROOT_MONKEY).printResults()
        return 0
    }

    fun part2(): Long {
        return 0L
    }
}