package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day11 {
    private const val MONKEY_TEST_TEXT = "Test: divisible by "
    private const val MONKEY_OPERATION_TEXT = "Operation: new = old "
    private const val MONKEY_STARTING_ITEMS_TEXT = "Starting items: "
    private const val MONKEY_TEST_TRUE_TEXT = "If true: throw to monkey "
    private const val MONKEY_TEST_FALSE_TEXT = "If false: throw to monkey "

    data class Monkey(
        val items: MutableList<Long>,
        val operation: (Long) -> Long,
        val test: (Long) -> Boolean,
        val trueMonkey: Int,
        val falseMonkey: Int
    )

    private fun String.getLinesAfter(prefix: String): List<String> {
        return lines().filter {
            it.contains(prefix)
        }.map {
            it.substringAfter(prefix)
        }
    }

    private fun String.getLineAfter(prefix: String): String {
        return getLinesAfter(prefix).first()
    }

    private fun getMonkeys(): List<Monkey> {
        val text = getResourceAsText("Day11Input")

        val mod = text.getLinesAfter(MONKEY_TEST_TEXT).map(String::toInt).reduce(Int::times)

        return text
            .split("\n\n")
            .map { monkeyText ->
                val (operator, amount) = monkeyText.getLineAfter(MONKEY_OPERATION_TEXT)
                    .split(" ")

                return@map Monkey(
                    items = monkeyText.getLineAfter(MONKEY_STARTING_ITEMS_TEXT)
                        .split(", ").map(String::toLong).toMutableList(),
                    operation = { old ->
                        val actionAmount = when(amount) {
                            "old" -> old
                            else -> amount.toLong()
                        }

                        when(operator) {
                            "+" -> old + actionAmount
                            "-" -> old - actionAmount
                            "/" -> old / actionAmount
                            "*" -> old * actionAmount
                            else -> old
                        } % mod
                    }, test = { old ->
                        old % (monkeyText.getLineAfter(MONKEY_TEST_TEXT).toLong()) == 0L
                    },
                    trueMonkey = monkeyText.getLineAfter(MONKEY_TEST_TRUE_TEXT).toInt(),
                    falseMonkey = monkeyText.getLineAfter(MONKEY_TEST_FALSE_TEXT).toInt(),
                )
            }
    }

    private fun List<Monkey>.performRounds(
        numberOfRounds: Int,
        worryDivisor: Int = 1
    ): List<Long> {
        val inspections = map { 0L }.toMutableList()

        repeat(numberOfRounds) {
            forEachIndexed { index, monkey ->
                monkey.items.toMutableList().forEach { oldWorryLevel ->
                    val newWorryLevel = monkey.operation(oldWorryLevel) / worryDivisor
                    inspections[index] = inspections[index] + 1
                    val newMonkey = if (monkey.test(newWorryLevel)) {
                        monkey.trueMonkey
                    } else {
                        monkey.falseMonkey
                    }
                    get(newMonkey).items.add(newWorryLevel)
                }

                monkey.items.clear()
            }
        }
        return inspections
    }

    private fun List<Long>.calculateMonkeyBusiness(): Long {
        return sortedDescending().take(2).reduce(Long::times)
    }

    fun part1(): Long {
        return getMonkeys().performRounds(
            numberOfRounds = 20,
            worryDivisor = 3
        ).calculateMonkeyBusiness().printResults()
    }

    fun part2(): Long {
        return getMonkeys().performRounds(
            numberOfRounds = 10_000
        ).calculateMonkeyBusiness().printResults()
    }
}