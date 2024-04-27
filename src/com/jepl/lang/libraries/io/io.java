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
        out.put("mkdir", args -> {
            final String dirname = args.get(0);
            File file = new File(dirname);
            if(!file.exists()){
                file.mkdir();
            }
        });
        out.put("delete", args -> {
            final String fname = args.get(0);
            File file = new File(fname);
            if(file.exists()) {
                file.delete();
            }else {
                throw new JEPLException(new RuntimeException("File "+fname+" isn't exists!"));
            }
        });
        out.put("io_put_contents", args -> {
            final String filename = args.get(0);
            final String content = args.get(1);
            PrintWriter writer;
            try {
                writer = new PrintWriter(filename);
            } catch (FileNotFoundException e) {
                throw new JEPLException(e);
            }
            writer.print(content);
            writer.close();
        });
        out.put("io_get_contents", args -> {
            final String filename = args.get(0);
            final String var = args.get(1);
            try {
                Values.addVar(var, Files.readString(Path.of(filename)));
            } catch (IOException e) {
                throw new JEPLException(e);
            }
        });
        out.put("io_file_rename", args -> {
           Path src = Path.of(args.get(0));
           final String newName = args.get(1);
            try {
                Files.move(src, src.resolveSibling(newName));
            } catch (IOException e) {
                throw new JEPLException(new RuntimeException(e));
            }
        });
        return out;
    }
}
