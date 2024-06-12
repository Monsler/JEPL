package com.jepl.lang.libraries.io

import com.jepl.lang.JEPLException
import com.jepl.lang.Values
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class io : Library {
    override fun invoke(): HashMap<String, Function> {
        val out = HashMap<String, Function>()
        out["io_mkdir"] = object : Function {
            override fun invoke(args: List<String>) {
                val dirname = args[0]
                val file = File(dirname)
                if (!file.exists()) {
                    file.mkdir()
                }
            }
        }
        out["io_delete"] = object : Function {
            override fun invoke(args: List<String>) {
                val fname = args[0]
                val file = File(fname)
                if (file.exists()) {
                    file.delete()
                } else {
                    throw JEPLException(RuntimeException("File $fname isn't exists!"))
                }
            }
        }
        out["io_put_contents"] = object : Function {
            override fun invoke(args: List<String>) {
                val filename = args[0]
                val content = args[1]
                val writer: File
                try {
                    writer = File(filename)
                } catch (e: FileNotFoundException) {
                    throw JEPLException(e)
                }
                writer.writeText(content)
            }
        }
        out["io_get_contents"] = object : Function {
            override fun invoke(args: List<String>) {
                val filename = args[0]
                val `var` = args[1]
                try {
                    Values.addVar(`var`, File(filename).readText())
                } catch (e: IOException) {
                    throw JEPLException(e)
                }
            }
        }
        out["io_file_rename"] = object : Function {
            override fun invoke(args: List<String>) {
                val src = Path.of(args[0])
                val newName = args[1]
                try {
                    Files.move(src, src.resolveSibling(newName))
                } catch (e: IOException) {
                    throw JEPLException(RuntimeException(e))
                }
            }
        }
        return out
    }
}