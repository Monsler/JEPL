package com.jepl.lang.libraries.math

import com.jepl.lang.Values
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library

class math : Library {
    override fun invoke(): HashMap<String, Function> {
        val out = java.util.HashMap<String, Function>()
        out["math_random"] = object : Function {
            override fun invoke(args: List<String>) {
                val range = args[1].toInt()
                val min = args[0].toInt()
                val `var` = args[2]
                val rand = (Math.random() * range).toInt() + min
                Values.addVar(`var`, rand.toString())
            }
        }
        out["math_round"] = object : Function {
            override fun invoke(args: List<String>) {
                val num = args[0].toFloat()
                val `var` = args[1]
                Values.addVar(`var`, num.toString())
            }
        }
        out["math_sqr"] = object : Function {
            override fun invoke(args: List<String>) {
                val a = args[0].toInt()
                val `var` = args[1]
                Values.addVar(`var`, (a * a).toString())
            }
        }
        return out
    }
}