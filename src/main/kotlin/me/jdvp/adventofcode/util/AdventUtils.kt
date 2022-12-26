package me.jdvp.adventofcode.util

import java.io.File

fun getResourceAsText(fileName: String): String {
    return File("src/main/resources/$fileName").readText()
}

/**
 * Overly complicated way of printing results as a side effect.
 *
 * If called from "Day1" class "part1" method, will indicate as
 * such by starting with "Day1 part1"
 */
inline fun <reified T> T.printResults(
    startOnNewLine: Boolean = false
): T = ::apply {
    val stackTrace = Thread.currentThread().stackTrace
    //Start with 1 because getStackTrace itself is 0
    var enclosingStackTraceElement: StackTraceElement? = null
    for(index in 1 until stackTrace.size) {
        enclosingStackTraceElement = stackTrace.getOrNull(index)
        if (enclosingStackTraceElement?.methodName != null && enclosingStackTraceElement.methodName != "invoke") {
            break
        }
    }
    val enclosingClass = enclosingStackTraceElement?.className?.split(".")?.lastOrNull()
    val enclosingMethod = enclosingStackTraceElement?.methodName
    val newLineChar = if (startOnNewLine) "\n" else ""
    println("$enclosingClass $enclosingMethod result is $newLineChar$this")
}

infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}