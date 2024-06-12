package com.jepl.lang

import com.ezylang.evalex.EvaluationException
import com.ezylang.evalex.Expression
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.net.URLClassLoader
import kotlin.system.exitProcess
import com.jepl.lang.libraries.Function

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
                        exitProcess(_args.get(0).asInt)
                    }

                    "function" -> {
                        val body = o.get("body").asJsonArray.toString()
                        val funName = getArgs(_args)[0]
                        Values.funs[funName] = body
                    }

                    "jump" -> {
                        val args = getArgs(_args)
                        val funName = args[0]
                        var argc = 0
                        if (args.size > 1) {
                            for(a in 1..<args.size) {
                                argc += 1
                                Values.addVar("arg$a", args[a])
                            }
                        }
                        Values.vars.put("argc", argc)
                        try {
                            interpret(Values.getFun(funName).toString())
                        }catch (e: IllegalStateException){
                            throw JEPLException(NoSuchFieldException(e.message))
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
                        val from: Int; val to: Int
                        try {
                            from = Integer.parseInt(args[0]); to = Integer.parseInt(args[1])
                        }catch (e: NumberFormatException) {
                            throw JEPLException(e)
                        }
                        for (i in from..to){
                            Values.addVar("index", i)
                            interpret(body)
                        }
                        Values.vars.remove("index")
                    }

                    "variable" -> {
                        val args = getArgs(_args)
                        val varName = args[0]; val varContent = args[1]
                        Values.addVar(varName, varContent)
                    }

                    "input" -> {
                        val args = getArgs(_args)
                        val text = args[0]; val outTo = args[1]
                        println(text)
                        val input: String? = readlnOrNull()
                        if (input != null) {
                            Values.addVar(outTo, input)
                        }
                    }

                    "rmvar" -> {
                        val args = getArgs(_args)
                        val varName = args[0]
                        Values.vars.remove(varName)
                    }

                    "include" -> {
                        val args = getArgs(_args)
                        val filename = args[0]
                        try {
                            interpret(File(filename).readText())
                        }catch (e: FileNotFoundException){
                            throw JEPLException(FileNotFoundException("Error [block${block + 1}] - file $filename not found either not exists!"))
                        }
                    }

                    "while" -> {
                        var args = getArgs(_args)
                        val body = o["body"].asJsonArray.toString()
                        var condition = args[0].toBoolean()
                        while (condition) {
                            interpret(body)
                            args = getArgs(_args)
                            condition = args[0].toBoolean()
                            if(!condition){
                                break
                            }
                        }
                    }

                    "if" -> {
                        val args = getArgs(_args)
                        val condition = args[0].toBoolean()
                        val body = o["body"].asJsonArray.toString()
                        if (condition) {
                            interpret(body)
                        }
                    }

                    "import" -> {
                        val args = getArgs(_args)
                        for (i in args) {
                            when (Values.libs.containsKey(i)) {
                                true -> {
                                    throw JEPLException(RuntimeException("Library <$i> already defined!"))
                                }

                                false -> {
                                    Values.addLib(i)
                                }
                            }
                        }
                    }

                    "eval" -> {
                        val args = getArgs(_args)
                        val codeIn = args[0]
                        interpret(codeIn)
                    }

                    "runtime_execute" -> {
                        val args = getArgs(_args)
                        val cmd = args[0]
                        try {
                            Runtime.getRuntime().exec(cmd)
                        }catch (e: RuntimeException) {
                            throw JEPLException(e)
                        }
                    }

                    "unload" -> {
                        val args = getArgs(_args)
                        val lib = args[0]
                        when (Values.libs.containsKey(lib)) {
                            true -> Values.libs.remove(lib)
                            false -> throw JEPLException(RuntimeException("Library $lib wasn't imported!"))
                        }
                    }

                    "getProperty" -> {
                        val args = getArgs(_args)
                        val property = args[0]
                        val variable = args[1]
                        Values.addVar(variable, System.getProperty(property))
                    }

                    "importJar" -> {
                        val args = getArgs(_args)
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
                        }catch (e: Exception) {
                            throw JEPLException(e)
                        }
                    }

                    "neg" -> {
                        val args = getArgs(_args)
                        val v = args[0]
                        val value = args[1].toInt()
                        if (Values.vars.containsKey(v)) {
                            Values.vars[v] = Values.vars[v] as Int + value
                        }
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
                    }
                }
            }
        }

        private fun getArgs(arr: JsonArray) : List<String> {
            val out: MutableList<String> = mutableListOf()
            for (elem in arr){
                val e = getExpression(elem)
                try {
                    out += e.evaluate().stringValue
                }catch (e: EvaluationException){
                    throw JEPLException(RuntimeException(e.message))
                }
            }

            return out
        }

        private fun getExpression(n: JsonElement) : Expression {
            val expr = Expression(n.asString)

            expr.with("JEPL_VERSION", Runner.langVersion)
            for (i in 0..Values.vars.size){
                for (map in Values.vars.iterator()){
                    var obj: Any
                    try {
                        obj = Integer.parseInt(map.value.toString())
                    }catch (e: NumberFormatException){
                        obj = map.value
                    }
                    expr.with(map.key, obj)
                }
            }
            return expr
        }
    }
}