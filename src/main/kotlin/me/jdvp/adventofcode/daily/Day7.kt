package me.jdvp.adventofcode.daily

import me.jdvp.adventofcode.util.getResourceAsText
import me.jdvp.adventofcode.util.printResults

object Day7 {
    private const val COMMAND_CHARACTER = "$"
    private const val COMMAND_CD = "cd"
    private const val OUTPUT_DIRECTORY = "dir"
    private const val PARENT_DIRECTORY = ".."

    private sealed interface FileSystemItem {
        val name: String

        data class Directory(
            override val name: String,
            val files: MutableList<FileSystemItem> = mutableListOf(),
            val parent: Directory? = null
        ): FileSystemItem {
            fun add(fileSystemItem: FileSystemItem) = files.add(fileSystemItem)
            fun getSubDirectory(name: String): Directory? {
                return if (name == PARENT_DIRECTORY) {
                    parent
                } else {
                    files.firstOrNull {
                        it is Directory && it.name == name
                    } as? Directory
                }
            }

            fun getSize(): Int {
                return files.sumOf {
                    when (it) {
                        is File -> it.size
                        is Directory -> it.getSize()
                    }
                }
            }

            override fun toString(): String {
                return "Directory(name=$name, files=$files)"
            }
        }

        data class File(
            override val name: String,
            val size: Int
        ): FileSystemItem
    }

    private fun buildFileTree(): FileSystemItem.Directory {
        var root: FileSystemItem.Directory? = null
        var currentDirectory = root

        getResourceAsText("Day7Input").lines().forEach {
            if (it.startsWith(COMMAND_CHARACTER)) {
                val command = it.removePrefix(COMMAND_CHARACTER).trim()
                if (command.startsWith(COMMAND_CD)) {
                    val dirName = command.removePrefix(COMMAND_CD).trim()
                    if (root == null) {
                        root = FileSystemItem.Directory(dirName)
                        currentDirectory = root
                    } else {
                        currentDirectory = currentDirectory?.getSubDirectory(dirName)
                    }
                }
            } else if (it.startsWith(OUTPUT_DIRECTORY)) {
                val dirName = it.removePrefix(OUTPUT_DIRECTORY).trim()
                currentDirectory?.add(FileSystemItem.Directory(
                    name = dirName,
                    parent = currentDirectory
                ))
            } else {
                val (fileSize, fileName) = it.split(" ")
                currentDirectory?.add(FileSystemItem.File(
                    name = fileName,
                    size = fileSize.toInt()
                ))
            }
        }
        return root!!
    }

    private fun FileSystemItem.Directory.getDirectorySizes(): List<Int> {
        val directories = files.filterIsInstance<FileSystemItem.Directory>()
        return listOf(getSize()) + directories.flatMap {
            it.getDirectorySizes()
        }
    }

    fun part1(): Int {
        return buildFileTree().getDirectorySizes().filterNot {
            it > 100_000
        }.sortedDescending().sum().printResults()
    }

    fun part2(): Int {
        val fileSystem = buildFileTree()
        val freeSpace = 70_000_000 - fileSystem.getSize()
        val spaceNeeded = 30_000_000 - freeSpace

        return fileSystem.getDirectorySizes().filter {
            it > spaceNeeded
        }.min().printResults()
    }
}