package com.jepl.lang.libraries.arrays

import com.jepl.lang.Values
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library

class arrays: Library {
    override fun invoke(): HashMap<String, Function> {
        val out: HashMap<String, Function> = HashMap()

        out["array_new"] = object : Function {
            override fun invoke(args: List<String>) {
                val varName = args[0]
                Values.addVar(varName, arrayListOf<Any>())
            }
        }

        out["array_put"] = object : Function {
            override fun invoke(args: List<String>) {
                val value = args[1]
                (Values.vars[args[0]] as ArrayList<Any>?)?.add(value)
            }
        }

        out["array_get"] = object : Function {
            override fun invoke(args: List<String>) {
                val list = Values.vars[args[0]] as java.util.ArrayList<Any>?
                val `var` = args[2]
                val index = args[1].toInt()
                Values.addVar(`var`, list!![index - 1])
            }
        }

        out["array_unset"] = object : Function {
            override fun invoke(args: List<String>) {
                val list =
                    Values.vars[args[0]] as java.util.ArrayList<Any>?
                val index = args[1].toInt()
                list!!.removeAt(index - 1)
                Values.addVar(args[0], list)
            }
        }

        out["array_putIndex"] = object : Function {
            override fun invoke(args: List<String>) {
                val list =
                    Values.vars[args[0]] as java.util.ArrayList<Any>?
                val index = args[1].toInt()
                val of = args[2]
                list!!.add(index - 1, of)
                Values.addVar(args[0], list)
            }
        }

        return out
    }
}