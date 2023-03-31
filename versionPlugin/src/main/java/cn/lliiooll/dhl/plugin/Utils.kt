package cn.lliiooll.dhl.plugin

import java.io.File
import kotlin.concurrent.thread

class Utils {
}

fun String.exec(): Boolean {
    try {
        println("执行指令: $this")
        val process = Runtime.getRuntime().exec(this)
        var finished = false
        thread {
            val reader = process.errorReader()
            val reader1 = process.inputReader()
            var line: String? = null
            while (!finished) {
                line = reader.readLine()
                if (line != null) {
                    println(line)
                } else {
                    line = reader1.readLine()
                    if (line != null) {
                        println(line)
                    }
                }
            }
        }
        val exitCode = process.waitFor()
        finished = true
        if (exitCode == 2) {
            return true
        } else {
            return false
        }
    } catch (e: Throwable) {
        return false
    }
}

fun String.exec(dir: File): Boolean {
    try {
        println("执行指令: $this")
        val process = Runtime.getRuntime().exec(this, arrayOf(),dir)
        var finished = false
        thread {
            val reader = process.errorReader()
            val reader1 = process.inputReader()
            var line: String? = null
            while (!finished) {
                line = reader.readLine()
                if (line != null) {
                    println(line)
                } else {
                    line = reader1.readLine()
                    if (line != null) {
                        println(line)
                    }
                }
            }
        }
        val exitCode = process.waitFor()
        finished = true
        if (exitCode == 2) {
            return true
        } else {
            return false
        }
    } catch (e: Throwable) {
        return false
    }
}
fun String.exec1(dir: File): Boolean {
    try {
        println("执行指令: $this")
        val process = ProcessBuilder(this).directory(dir).start()
        var finished = false
        thread {
            val reader = process.errorReader()
            val reader1 = process.inputReader()
            var line: String? = null
            while (!finished) {
                line = reader.readLine()
                if (line != null) {
                    println(line)
                } else {
                    line = reader1.readLine()
                    if (line != null) {
                        println(line)
                    }
                }
            }
        }
        val exitCode = process.waitFor()
        finished = true
        if (exitCode == 2) {
            return true
        } else {
            return false
        }
    } catch (e: Throwable) {
        return false
    }
}