package com.jepl.lang.libraries.io;

import com.jepl.lang.JEPLException;
import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class io implements Library {
    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("mkdir", new Function() {
            @Override
            public void invoke(List<String> args) {
                String dirname = args.getFirst();
                File file = new File(dirname);
                if(!file.exists()){
                    file.mkdir();
                }
            }
        });
        out.put("rmdir", new Function() {
            @Override
            public void invoke(List<String> args) {
                String dirname = args.getFirst();
                File file = new File(dirname);
                file.delete();
            }
        });
        out.put("io_put_contents", new Function() {
            @Override
            public void invoke(List<String> args) {
                String filename = args.getFirst();
                String content = args.get(1);
                PrintWriter writer;
                try {
                    writer = new PrintWriter(new File(filename));
                } catch (FileNotFoundException e) {
                    throw new JEPLException(new RuntimeException(e));
                }
                writer.println(content);
                writer.close();
            }
        });
        out.put("io_get_contents", new Function() {
            @Override
            public void invoke(List<String> args) {
                String filename = args.getFirst();
                String var = args.get(1);
                try {
                    Values.addVar(var, Files.readString(Path.of(filename)));
                } catch (IOException e) {
                    throw new JEPLException(e);
                }
            }
        });
        return out;
    }
}
