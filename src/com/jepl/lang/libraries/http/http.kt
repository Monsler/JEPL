package com.jepl.lang.libraries.http

import com.jepl.lang.JEPLException
import com.jepl.lang.Values
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class http : Library {
    override fun invoke(): HashMap<String, Function> {
        val out = HashMap<String, Function>()
        out["http_request"] = object : Function {
            override fun invoke(args: List<String>) {
                val client = HttpClient.newHttpClient()
                val address = args[0]
                val varName = args[1]
                val request = HttpRequest.newBuilder().uri(URI.create(address)).GET().build()
                try {
                    val response =
                        client.send(request, HttpResponse.BodyHandlers.ofString())
                    Values.addVar(varName, response.body())
                } catch (e: IOException) {
                    throw JEPLException(RuntimeException(e))
                } catch (e: InterruptedException) {
                    throw JEPLException(RuntimeException(e))
                }
            }
        }
        return out
    }

}