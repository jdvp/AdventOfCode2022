@file:Suppress("UNCHECKED_CAST")

package me.jdvp.adventofcode.util

import java.util.*

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
        index: List<Int>
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
            getNeighborsOf(visiting)
                .subtract(explored)
                .filter { isEdge(get(visiting)!!, get(it)!!) }
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