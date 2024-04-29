import com.google.gson.JsonParser;
import com.jepl.lang.Runner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.print("JEPL "+Runner.langVersion+"\n");
        System.out.print("-".repeat(("JEPL "+Runner.langVersion+"\n").length()-1)+"\n");
        if(args[0].equals("-r")) {
            if (args.length > 1 && args[1].contains(".json")) {
                System.out.print("\nExecuting " + args[1] + "\n");
                Runner.runFile(args[1]);
            } else if (!args[1].contains(".json")) {
                String wd = System.getProperty("user.dir") + "/" + args[1];
                String main = JsonParser.parseString(Files.readString(Path.of(wd + "/project.json"))).getAsJsonObject().get("main").getAsString();
                System.setProperty("user.dir", wd+"/src");
                Runner.runString(Files.readString(Path.of(wd + "/" + main)));
            }
        }else if(args[0].equals("install")){
            final String lib = args[1];
            final String to = args[2];
            if(new File(to+"/project.json").exists()) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://pkg.mountaintech.ru/jepl/logic.php?uname=" + lib + "&action=install")).GET().build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (!response.body().equals("<h1>Library not found on server!</h1>")) {
                        saveUrl(to + "/src/lib/" + lib + ".jar", response.body());
                        System.out.println("Library " + lib + " successfully installed to " + to + "/src/lib/coloride.jar");
                    } else {
                        System.out.println("Library " + lib + " isn't found on repository!");
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }else if(args[0].equals("-i")){
            Console console = System.console();
            System.out.print("Project name: ");
            final String name = console.readLine();
            File file = new File(System.getProperty("user.dir")+"/"+name);
            file.mkdir();
            PrintWriter writer = new PrintWriter(System.getProperty("user.dir")+"/"+name+"/project.json");
            writer.println("{\"main\": \"src/main.json\"}");
            writer.close();
            File src = new File(System.getProperty("user.dir")+"/"+name+"/src");
            src.mkdir();
            PrintWriter writer1 = new PrintWriter(System.getProperty("user.dir")+"/"+name+"/src/main.json");
            writer1.println("""
                    [
                      {
                        "name": "function",
                        "args": ["\\"main\\""],
                        "body": [
                          {
                            "name": "println",
                            "args": ["\\"Hello, World!\\""]
                          }
                        ]
                      }
                    ]""");
            writer1.close();
            File lib = new File(System.getProperty("user.dir")+"/"+name+"/src/lib");
            lib.mkdir();
            System.out.print("Project "+name+" created.");
        }else if(args[0].equals("search")){
            final String lib = args[1];
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://pkg.mountaintech.ru/jepl/logic.php?uname=" + lib+"&action=search")).GET().build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Results for "+lib+":");
                System.out.println(response.body().replace("<br>", "\n"));
            }catch (InterruptedException | IOException e){
                throw new RuntimeException(e);
            }
        }
    }
    public static void saveUrl(final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

}