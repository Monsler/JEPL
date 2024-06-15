package com.jepl.lang

import com.google.gson.JsonParser
import com.jepl.lang.Expressions.Companion.checkArguments
import com.jepl.lang.Expressions.Companion.getArgs
import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.net.URLClassLoader
import kotlin.system.exitProcess
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library

class Interpreter {
    companion object {
        var block: Int = 0

        fun interpret(input: String) {
            val code = JsonParser.parseString(input).asJsonArray
            for (i in 0..<code.size()) {
                block = i

                val o = code.get(i).asJsonObject
                val name = o.get("name").asString
                val _args = o.get("args").asJsonArray
                when (name) {
                    "print" -> {
                        val args = getArgs(_args)
                        for (arg in args) {
                            print(arg)
                        }
                    }

                    "println" -> {
                        val args = getArgs(_args)
                        for (arg in args) {
                            println(arg)
                        }
                    }

                    "exit" -> {
                        checkArguments(1, getArgs(_args), o)
                        exitProcess(_args.get(0).asInt)
                    }

                    "function" -> {
                        val body = o.get("body").asJsonArray.toString()
                        checkArguments(1, getArgs(_args), o)
                        val funName = getArgs(_args)[0]
                        Values.addFun(funName, body)
                    }

                    "jump" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val funName = args[0]
                        var argc = 0
                        if (args.size > 1) {
                            for (a in 1..<args.size) {
                                argc += 1
                                Values.addVar("arg$a", args[a])
                            }
                        }
                        Values.vars["argc"] = argc
                        try {
                            interpret(Values.getFun(funName).toString())
                        } catch (e: IllegalStateException) {
                            throw JEPLException(NoSuchFieldException(e.message), o)
                        }
                        Values.vars.remove("argc")
                        if (args.size > 1) {
                            for (a in 1..<args.size) {
                                Values.vars.remove("arg$a")
                            }
                        }
                    }

                    "for" -> {
                        val body = o.get("body").asString
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val from: Int;
                        val to: Int
                        try {
                            from = Integer.parseInt(args[0]); to = Integer.parseInt(args[1])
                        } catch (e: NumberFormatException) {
                            throw JEPLException(e, o)
                        }
                        for (iter in from..to) {
                            Values.addVar("index", iter)
                            interpret(body)
                        }
                        Values.vars.remove("index")
                    }

                    "variable" -> {
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val varName = args[0];
                        val varContent = args[1]
                        Values.addVar(varName, varContent)
                    }

                    "input" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val text = args[0];
                        val outTo = args[1]
                        println(text)
                        val inputs: String? = readlnOrNull()
                        if (inputs != null) {
                            Values.addVar(outTo, inputs)
                        }
                    }

                    "rmvar" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val varName = args[0]
                        Values.vars.remove(varName)
                    }

                    "include" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val filename = args[0]
                        try {
                            interpret(File(filename).readText())
                        } catch (e: FileNotFoundException) {
                            throw JEPLException(
                                FileNotFoundException("Error [block${block + 1}] - file $filename not found either not exists!"),
                                o
                            )
                        }
                    }

                    "while" -> {
                        var args = getArgs(_args)
                        checkArguments(1, args, o)
                        val body = o["body"].asJsonArray.toString()
                        var condition = args[0].toBoolean()
                        while (condition) {
                            interpret(body)
                            args = getArgs(_args)
                            condition = args[0].toBoolean()
                            if (!condition) {
                                break
                            }
                        }
                    }

                    "if" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val condition = args[0].toBoolean()
                        val body = o["body"].asJsonArray.toString()
                        if (condition) {
                            interpret(body)
                        }
                    }

                    "import" -> {
                        val args = getArgs(_args)
                        for (arg in args) {
                            when (Values.libs.containsKey(arg)) {
                                true -> {
                                    throw JEPLException(RuntimeException("Library <$arg> already defined!"), o)
                                }

                                false -> {
                                    Values.addLib(arg)
                                }
                            }
                        }
                    }

                    "eval" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val codeIn = args[0]
                        interpret(codeIn)
                    }

                    "runtime_execute" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val cmd = args[0]
                        try {
                            Runtime.getRuntime().exec(cmd)
                        } catch (e: RuntimeException) {
                            throw JEPLException(e, o)
                        }
                    }

                    "unload" -> {
                        val args = getArgs(_args)
                        checkArguments(1, args, o)
                        val lib = args[0]
                        when (Values.libs.containsKey(lib)) {
                            true -> Values.libs.remove(lib)
                            false -> throw JEPLException(RuntimeException("Library $lib wasn't imported!"), o)
                        }
                    }

                    "get_property" -> {
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val property = args[0]
                        val variable = args[1]
                        Values.addVar(variable, System.getProperty(property))
                    }

                    "set_property" -> {
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val property = args[0];
                        val value = args[1]
                        System.setProperty(property, value)
                    }

                    "import_jar" -> {
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val path = "${System.getProperty("user.dir")}\\${args[0].replace("/", "\\")}"
                        val addr = args[1]
                        try {
                            val urls = arrayOf(URI("file:$path").toURL())
                            val loader = URLClassLoader(urls, this::class.java.classLoader)
                            val classOf = Class.forName("$addr.Lib", true, loader)
                            val method = classOf.getDeclaredMethod("invoke")
                            val instance = classOf.getDeclaredConstructor().newInstance()

                            @Suppress("UNCHECKED_CAST")
                            val lib: HashMap<String, Function> = method.invoke(instance) as HashMap<String, Function>
                            Values.libs[addr] = lib
                        } catch (e: Exception) {
                            throw JEPLException(e, o)
                        }
                    }

                    "neg" -> {
                        val args = getArgs(_args)
                        val v = args[0]
                        val value = args[1]
                        if (Values.vars.containsKey(v))
                            Values.vars[v] = Integer.parseInt(Values.vars[v].toString()) + Integer.parseInt(value)
                    }

                    "assert" -> {
                        checkArguments(1, getArgs(_args), o)
                        val condition = getArgs(_args)[0]
                        if (!condition.toBoolean())
                            throw JEPLException(AssertionError("Condition is false"), o)
                    }

                    "preprocessor" -> {
                        val args = getArgs(_args)
                        checkArguments(2, args, o)
                        val `in` = args[0]
                        val of = args[1]
                        val generatedLib = object : Library {
                            override fun invoke(): HashMap<String, Function> {
                                val out = HashMap<String, Function>()
                                out[of] = object : Function {
                                    override fun invoke(args: List<String>) {
                                        val sb = StringBuilder()
                                        sb.append('[')
                                        for (arg in args) {
                                            if (arg != args.last())
                                                sb.append("\"\\\"").append(arg).append("\\\"\"").append(", ")
                                            else
                                                sb.append("\"\\\"").append(arg).append("\\\"\"")
                                        }
                                        sb.append("]")
                                        interpret("[{\"name\": \"${`in`}\", args: ${sb}}]")
                                    }
                                }
                                return out
                            }
                        }
                        Values.libs["$!generated$?lib!${`in`}"] = generatedLib.invoke()
                    }

                    else -> {
                        val args = getArgs(_args)
                        var index = 0

                        for (lib in Values.libs.iterator()) {
                            if (lib.value.containsKey(name)) {
                                lib.value[name]?.invoke(args)
                                break
                            }
                            ++index
                        }

                        when (index == Values.libs.size) {
                            true -> throw JEPLException(NoSuchFieldException("field $name wasn't found"), o)
                            false -> continue
                        }
                    }
                }
            }
        }

    }
}