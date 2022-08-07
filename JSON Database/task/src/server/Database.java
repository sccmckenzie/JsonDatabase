package server;

import java.io.File;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {

    private String filepath = "C:/Users/Scott/IdeaProjects/JSON Database/JSON Database/task/src/server/data/db.json";
    private Gson gson = new Gson();
    final ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();

    public Map dbRead() throws IOException {

        synchronized (readLock) {
            Map out = gson.fromJson(new String(Files.readAllBytes(Paths.get(filepath))), HashMap.class);

            if (out == null) {
                return new HashMap<String, String>();
            } else {
                return out;
            }
        }
    }

    public String get(List<String> path) throws Exception {
        Map map = dbRead();
        try {
            for (int i = 0; i < path.size() - 1; i++) {
                map = (Map) map.get(path.get(i));
            }
            var out = new Gson().toJson(map.get(path.get(path.size() - 1)));
//            var out = map.get(path.get(path.size() - 1));
//            if (!out.getClass().equals(String.class)) {
//                out = gson.toJson(out).;
//            }
            return (String) out;
        } catch (Exception e) {
            throw new Exception("No such key");
        }
    }

    public void delete(List<String> path) throws Exception {
        List<Map> maps = new ArrayList<>();
        maps.add(dbRead());

        try {
            for (int i = 0; i < path.size() - 1; i++) {
                maps.add((Map) maps.get(i).get(path.get(i)));
            }
            if (maps.get(path.size() - 1).remove(path.get(path.size() - 1)) == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("No such key");
        }

        synchronized (writeLock) {
            try (FileWriter writer = new FileWriter(filepath, false)) {
                writer.write(gson.toJson(maps.get(0)).replaceAll("\\\\", "").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}").replaceAll("\"\"", "\""));
            }
        }
    }

    public void set(List<String> path, String value) throws Exception {
        List<Map> maps = new ArrayList<>();
        maps.add(dbRead());

        for (int i = 0; i < path.size() - 1; i++) {
            maps.get(i).putIfAbsent(path.get(i), new HashMap<>());
            maps.add((Map) maps.get(i).get(path.get(i)));
        }
        maps.get(path.size() - 1).put(path.get(path.size() - 1), value);

        synchronized (writeLock) {
            try (FileWriter writer = new FileWriter(filepath, false)) {
                writer.write(gson.toJson(maps.get(0)).replaceAll("\\\\", "").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}").replaceAll("\"\"", "\""));
            }
        }
    }
}