package com.example.regexchallenge.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import sun.java2d.pipe.SpanShapeRenderer
import tornadofx.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class MyApp : App(Workspace::class) {

    override fun onBeforeShow(view: UIComponent) {
        workspace.dock<MainView>()
    }
}

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MainView : View() {
    var directories = FXCollections.observableArrayList<Directory>()

    init {
        var fileReader: BufferedReader? = null
        try {
            var line: String?

            fileReader = BufferedReader(FileReader(File(ClassLoader.getSystemResource("dir_names.csv").toURI())))

            line = fileReader.readLine()
            while (line != null) {
                val nextDirectory = parseDirName(line)
                if(nextDirectory!= null) directories.add(nextDirectory)
                line = fileReader.readLine()
            }
        } catch (e: Exception) {
            println("Error reading the csv file")
            e.printStackTrace()
        } finally {
            try {
                fileReader!!.close()
            } catch (e: IOException) {
                println("closing file reader error")
                e.printStackTrace()
            }
        }
    }

    override val root = borderpane {
        center {
            tableview(directories) {
                readonlyColumn("dir_name", Directory::name).contentWidth()
                readonlyColumn("lang_code", Directory::langCode).contentWidth()
                readonlyColumn("book_id", Directory::book_id).contentWidth()
                readonlyColumn("resource_type", Directory::resourceType).contentWidth()
                readonlyColumn("level", Directory::level).contentWidth()
                columnResizePolicy = SmartResize.POLICY
                prefWidth = 550.0
            }
        }
    }

    fun parseDirName(dirName: String): Directory? {
        var seperators = 0
        for (char in dirName) {
            if (char.equals('_')) seperators++
        }
        when (seperators) {
            0 -> {
                val regex = "(([a-z]+)-([a-z]+)-([a-z]+)-([a-z]+))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, match1, resource, langCode1, langCode2) ->
                    return Directory(name = enitreMatch,
                            resourceType = resource, langCode = langCode1 + "-" + langCode2)
                }
            }

            1 -> {
                val regex = "(([a-z]+)-([a-z]+)_([a-z]+)-([a-z]+))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, match1, bookId, resourceType, langCode) ->
                    return Directory(name = enitreMatch,
                            resourceType = resourceType,
                            langCode = langCode, book_id = bookId)
                }
            }

            2 -> {
                val regex = "(([a-z]+)_([a-z0-9]+)_([a-z]+))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, langCode, match3, resourceType) ->
                    return Directory(name = enitreMatch,
                            resourceType = resourceType,
                            langCode = langCode)
                }
            }

            3 -> {
                val regex = "(([a-z-0-9]+)_([a-z0-9]+)_([a-z]+)_([a-z]+))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, langCode, bookId, match3, match4) ->
                    if (match4.length > 2) {
                        return Directory(name = enitreMatch, resourceType = match4,
                                langCode = langCode, book_id = bookId)
                    } else {
                        return Directory(name = enitreMatch, resourceType = match3, level = match4.last().toString(),
                                langCode = langCode, book_id = bookId)
                    }
                }
            }
            4 -> {
                val regex = "(([a-z-0-9]+)?_([a-z0-9]+)_([a-z]+)?_([a-z]{3})?_([a-z0-9]{2}))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, langCode, bookId, x, resourceType, level) ->
                    return Directory(name = enitreMatch,
                            level = level.last().toString(),
                            book_id = bookId,
                            resourceType = resourceType,
                            langCode = langCode)
                }
            }

            5 -> {
                val regex = "(([a-z-0-9]+)_([a-z0-9]+)_([a-z]+)_([a-z]+)_([a-z]+)_([0-9]))".toRegex()
                regex.matchEntire(dirName)?.destructured?.let { (enitreMatch, langCode, bookId, x, resourceType, y, level) ->
                    return Directory(name = enitreMatch,
                                    level = level,
                                    book_id = bookId,
                                    resourceType = resourceType,
                                    langCode = langCode)
                }
            }
        }
        return null
    }
}

data class Directory(
        val name: String? = null,
        val langCode: String? = null,
        val book_id: String? = null,
        val resourceType: String? = null,
        val level: String? = null
)
