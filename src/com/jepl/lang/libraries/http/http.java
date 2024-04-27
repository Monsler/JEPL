package com.jepl.lang.libraries.http;

import com.jepl.lang.JEPLException;
import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

public class http implements Library {
    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("http_request", args -> {
            HttpClient client = HttpClient.newHttpClient();
            final String addr = args.get(0);
            final String vname = args.get(1);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(addr)).GET().build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Values.addVar(vname, response.body());
            } catch (IOException | InterruptedException e) {
                throw new JEPLException(new RuntimeException(e));
            }
        });
        return out;
    }
}
