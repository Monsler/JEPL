import com.google.gson.JsonParser
import com.jepl.lang.Runner
import java.io.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path

object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        print("JEPL ${Runner.langVersion} \n")
        print("-".repeat(("JEPL " + Runner.langVersion + "\n").length - 1) + "\n")
        if (args[0] == "-r") {
            if (args.size > 1 && args[1].contains(".json")) {
                Runner.runFile(args[1])
            } else if (!args[1].contains(".json")) {
                val wd = System.getProperty("user.dir") + "\\" + args[1]
                val main = JsonParser.parseString(Files.readString(Path.of("$wd/project.json"))).asJsonObject["main"].asString
                System.setProperty("user.dir", "$wd\\src")
                Runner.runString(Files.readString(Path.of("$wd\\$main")))
            }
        } else if (args[0] == "install") {
            val lib = args[1]
            val to = args[2]
            if (File("$to/project.json").exists()) {
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pkg.mountaintech.ru/jepl/logic.php?uname=$lib&action=install")).GET()
                    .build()
                try {
                    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                    if (response.body() != "<h1>Library not found on server!</h1>") {
                        saveUrl("$to/src/lib/$lib.jar", response.body())
                        println("Library $lib successfully installed to $to/src/lib/$lib.jar")
                    } else {
                        println("Library $lib isn't found on repository!")
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
        } else if (args[0] == "-i") {
            val console = System.console()
            print("Project name: ")
            val name = console.readLine()
            val file = File(System.getProperty("user.dir") + "/" + name)
            file.mkdir()
            val writer = PrintWriter(System.getProperty("user.dir") + "/" + name + "/project.json")
            writer.println("{\"main\": \"src/main.json\"}")
            writer.close()
            val src = File(System.getProperty("user.dir") + "/" + name + "/src")
            src.mkdir()
            val writer1 = PrintWriter(System.getProperty("user.dir") + "/" + name + "/src/main.json")
            writer1.println(
                """
                    [
                      {
                        "name": "function",
                        "args": ["\"main\""],
                        "body": [
                          {
                            "name": "println",
                            "args": ["\"Hello, World!\""]
                          }
                        ]
                      }
                    ]
                    """.trimIndent()
            )
            writer1.close()
            val lib = File(System.getProperty("user.dir") + "/" + name + "/src/lib")
            lib.mkdir()
            print("Project $name created.")
        } else if (args[0] == "search") {
            val lib = args[1]
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://pkg.mountaintech.ru/jepl/logic.php?uname=$lib&action=search")).GET().build()
            try {
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                println("Results for $lib:")
                println(response.body().replace("<br>", "\n"))
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(IOException::class)
    fun saveUrl(filename: String?, urlString: String?) {
        var `in`: BufferedInputStream? = null
        var fout: FileOutputStream? = null
        try {
            `in` = BufferedInputStream(URI(urlString!!).toURL().openStream())
            fout = FileOutputStream(filename!!)

            val data = ByteArray(1024)
            var count: Int
            while ((`in`.read(data, 0, 1024).also { count = it }) != -1) {
                fout.write(data, 0, count)
            }
        } finally {
            `in`?.close()
            fout?.close()
        }
    }
}