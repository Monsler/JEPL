package com.jepl.lang.libraries

interface Library {
    fun invoke(): HashMap<String, Function>
}