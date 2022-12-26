@file:Suppress("UNCHECKED_CAST")

package me.jdvp.adventofcode.util

import java.util.*

class NDimensionalArray<T: Any>(
    private val dimensions: Int = 2,
    private val defaultValueCalculation: () -> T?
) {
    private val topDimension = mutableMapOf<Int, Any>()

    private fun getMapForDimension(vararg index: Int): MutableMap<Int, Any> {
        var dimension: MutableMap<Int, Any> = topDimension
        for (i in index.dropLast(1)) {
            dimension = topDimension.getOrPut(i) {
                mutableMapOf<Int, Any>()
            } as MutableMap<Int, Any>
        }
        return dimension
    }

    operator fun set(vararg index: Int, value: T) {
        verifyIndexSize(*index)
        getMapForDimension(*index)[index.last()] = value
    }

    operator fun get(vararg index: Int): T? {
        verifyIndexSize(*index)
        return getMapForDimension(*index)[index.last()] as? T? ?: defaultValueCalculation()
    }

    private fun Map<Int, Any>.getSatisfyingIndices(
        predicate: (T) -> Boolean,
        priorIndices: List<Int> = listOf()
    ): List<List<Int>> {
        return entries.flatMap { entry ->
            val value = entry.value
            if (value is Map<*, *>) {
                return@flatMap (value as Map<Int, Any>).getSatisfyingIndices(
                    predicate = predicate,
                    priorIndices = priorIndices + entry.key
                )
            } else if (predicate(value as T)) {
                return@flatMap listOf(priorIndices + entry.key)
            }

            return@flatMap listOf()
        }.filterNot { it.isEmpty() }
    }

    fun getSatisfyingIndices(
        predicate: (T) -> Boolean
    ): List<List<Int>> {
        return topDimension.getSatisfyingIndices(predicate)
    }

    private fun Map<Int, *>?.get1dArrayString(
        maxLength: Int
    ): String {
        return (0..maxLength).map {
            this?.get(it) ?: defaultValueCalculation()
        }.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]"
        )
    }

    private fun getNeighborsOf(index: IntArray): List<List<Int>> {
        verifyIndexSize(*index)

        return (0 until dimensions).reversed().flatMap { dimensionIndex ->
            listOf(
                index.copyOf().apply {
                    set(index = dimensionIndex, value = get(dimensionIndex) - 1)
                },
                index.copyOf().apply {
                    set(index = dimensionIndex, value = get(dimensionIndex) + 1)
                },
            )
        }.filterNot {
            get(*it) == defaultValueCalculation()
        }.map { it.toList() }
    }

    private fun verifyIndexSize(vararg index: Int) {
        if (index.size != dimensions) {
            throw IllegalArgumentException(DIMENSION_SIZE_ERROR)
        }
    }

    fun shortestPath(
        start: List<Int>,
        end: List<Int>,
        isEdge: (T, T) -> Boolean = { _, _ ->
            true
        }
    ): List<List<Int>>? {
        val unvisited = LinkedList<List<Int>>()
        val explored = mutableSetOf(start)
        val parents = mutableMapOf<List<Int>, List<Int>>()
        unvisited.add(start)

        var foundPath = false

        while (unvisited.isNotEmpty()) {
            val visiting = unvisited.remove()
            if (visiting == end.toList()) {
                foundPath = true
                break
            }
            getNeighborsOf(visiting.toIntArray())
                .subtract(explored)
                .filter { isEdge(get(*visiting.toIntArray())!!, get(*it.toIntArray())!!) }
                .forEach {
                    explored.add(it)
                    parents[it] = visiting
                    unvisited.add(it)
                }
        }

        if (!foundPath) {
            return null
        }

        val path = mutableListOf<List<Int>>()
        var node: List<Int> = end
        while(node != start) {
            path.add(0, node)
            node = parents[node] ?: return null
        }
        path.add(0, start)
        return path
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
                    (topDimension[it] as? Map<Int, *>)?.keys?.sorted()?.max() ?: 0
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