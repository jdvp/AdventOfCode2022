@file:Suppress("UNCHECKED_CAST")

package me.jdvp.adventofcode.util

import java.util.*
import kotlin.math.abs

open class NDimensionalArray<T: Any>(
    protected val dimensions: Int,
    protected val defaultValueCalculation: () -> T? = { null }
) {
    private val topDimension = mutableMapOf<Int, Any>()

    private fun getMapForDimension(vararg index: Int): MutableMap<Int, Any> {
        var dimension: MutableMap<Int, Any> = topDimension
        for (i in index.dropLast(1)) {
            dimension = dimension.getOrPut(i) {
                mutableMapOf<Int, Any>()
            } as MutableMap<Int, Any>
        }
        return dimension
    }

    open operator fun set(vararg index: Int, value: T) {
        verifyIndexSize(*index)
        getMapForDimension(*index)[index.last()] = value
    }

    open operator fun set(index: List<Int>, value: T) {
        set(*index.toIntArray(), value = value)
    }

    open operator fun get(vararg index: Int): T? {
        verifyIndexSize(*index)
        return getMapForDimension(*index)[index.last()] as? T? ?: defaultValueCalculation()
    }

    open operator fun get(index: List<Int>): T? {
        return get(*index.toIntArray())
    }

    open fun clearItem(vararg index: Int) {
        verifyIndexSize(*index)
        getMapForDimension(*index).remove(index.last())
    }

    open fun clearItem(index: List<Int>) {
        clearItem(*index.toIntArray())
    }

    private fun Map<Int, Any>.getSatisfyingIndices(
        predicate: (T) -> Boolean,
        priorIndices: List<Int> = listOf()
    ): Sequence<List<Int>> {
        return entries.asSequence().flatMap { entry ->
            val value = entry.value
            if (value is Map<*, *>) {
                return@flatMap (value as Map<Int, Any>).getSatisfyingIndices(
                    predicate = predicate,
                    priorIndices = priorIndices + entry.key
                )
            } else if (predicate(value as T)) {
                return@flatMap sequenceOf(priorIndices + entry.key)
            }

            return@flatMap sequenceOf()
        }.filterNot { it.isEmpty() }
    }

    fun getSatisfyingIndices(
        predicate: (T?) -> Boolean
    ): Sequence<List<Int>> {
        return topDimension.getSatisfyingIndices(predicate)
    }

    private fun Map<Int, *>?.get1dArrayString(
        maxLength: Int
    ): String {
        return (0..maxLength).map {
            this?.get(it) ?: defaultValueCalculation()
        }.joinToString(
            separator = "",
            prefix = "[",
            postfix = "]"
        )
    }

    fun getNeighborsOf(
        index: List<Int>,
        filterUnfilledItems: Boolean = true
    ): List<List<Int>> {
        verifyIndexSize(*index.toIntArray())

        return (0 until dimensions).reversed().flatMap { dimensionIndex ->
            listOf(
                index.toIntArray().copyOf().apply {
                    set(index = dimensionIndex, value = get(dimensionIndex) - 1)
                },
                index.toIntArray().copyOf().apply {
                    set(index = dimensionIndex, value = get(dimensionIndex) + 1)
                },
            )
        }.filterNot {
            filterUnfilledItems && get(*it) == defaultValueCalculation()
        }.map { it.toList() }
    }

    private fun verifyIndexSize(vararg index: Int) {
        if (index.size != dimensions) {
            throw IllegalArgumentException(DIMENSION_SIZE_ERROR)
        }
    }

    /**
     * Finds the shortest path leveraging the A* algorithm (unless I coded it wrong LOL).
     * The heuristic used is just a simple Manhattan distance.
     */
    fun shortestPath(
        start: List<Int>,
        end: List<Int>,
        isEdge: (T?, T?) -> Boolean = { _, _ ->
            true
        },
        filterUnfilledItems: Boolean = true
    ): List<List<Int>>? {
        fun List<Int>.h(): Int {
            //just using manhattan distance for heuristic
            return end.mapIndexed { index, i ->
                abs(this[index] - i)
            }.sum()
        }

        fun Map<List<Int>, List<Int>>.reconstructPath(): List<List<Int>>? {
            var current = end
            val totalPath = mutableListOf(end)
            while(current in keys) {
                current = this[current] ?: return null
                totalPath.add(current)
            }
            return totalPath.reversed()
        }

        val cameFrom = mutableMapOf<List<Int>, List<Int>>()
        val gScore = mutableMapOf(
            start to 0
        )
        val fScore = mutableMapOf(
            start to start.h()
        )
        val openSet = PriorityQueue<List<Int>> { a, b ->
            return@PriorityQueue (fScore[a] ?: Int.MAX_VALUE) - (fScore[b] ?: Int.MAX_VALUE)
        }
        openSet.add(start)

        while(openSet.isNotEmpty()) {
            val current = openSet.remove()
            if (current == end) {
                return cameFrom.reconstructPath()
            }

            getNeighborsOf(current, filterUnfilledItems)
                .filter { isEdge(get(current), get(it)) }
                .forEach { neighbor ->
                val tentativeGScore = gScore[current]!! + 1
                if (tentativeGScore < (gScore[neighbor] ?: Int.MAX_VALUE)) {
                    cameFrom[neighbor] = current
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + neighbor.h()
                    if (neighbor !in openSet) {
                        openSet.add(neighbor)
                    }
                }
            }
        }

        return null
    }

    fun flipAxes(
        shiftAxes: List<Int>? = null
    ): NDimensionalArray<T>{
        val newArray = NDimensionalArray(
            dimensions = dimensions,
            defaultValueCalculation = defaultValueCalculation
        )
        getSatisfyingIndices {
            it != defaultValueCalculation()
        }.forEach { oldIndex ->
            val newIndex = oldIndex.mapIndexed { index, i ->
                i + (shiftAxes?.getOrNull(index) ?: 0)
            }.reversed()
            newArray[newIndex] = this[oldIndex]!!
        }
        return newArray
    }

    fun print() {
        when (dimensions) {
            1 -> {
                println(topDimension.get1dArrayString(
                    maxLength = topDimension.keys.sorted().max()
                ))
            }
            2 -> {
                val xMax = topDimension.keys.sorted().max()
                val yMax = (0..xMax).maxOf {
                    (topDimension[it] as? Map<Int, *>)?.keys?.sorted()?.maxOrNull() ?: 0
                }

                (0..xMax).joinToString(
                    separator = ",\n\t",
                    prefix = "[\n\t",
                    postfix = "\n]"
                ) {
                    (topDimension[it] as? Map<Int, *>?).get1dArrayString(
                        maxLength = yMax
                    )
                }.apply {
                    println(this)
                }
            }
            else -> {
                println("Can only print 1D & 2D arrays.")
            }
        }
    }

    private companion object {
       const val DIMENSION_SIZE_ERROR = "Number of index arguments must match number of dimensions of the array!"
    }
}