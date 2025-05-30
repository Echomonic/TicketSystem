package me.themoonis.ticketSystem.file.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Map;

public interface JsonFile {

    void load();

    void save();

    void unload();

    void reload();

    <T> T get(String path);

    <T> T get(String path, T def);

    <T> T get(String path, Class<T> cast);

    <T> T get(String path, T def, Class<T> cast);

    <T> T get(JsonNode section, String path);

    <T> T get(JsonNode section, String path, Class<T> cast);

    <T> T get(JsonNode section, String path, T def);

    <T> T get(JsonNode section, String path, T def, Class<T> cast);

    void set(String path, Object value);

    void set(ObjectNode section, String path, Object value);

    ObjectNode section(String path);

    ObjectNode section(String path, Map<?, ?> map);

    boolean exists(String path);

    boolean exists(JsonNode section, String path);

    static JsonFile create(File path, String name) {
        return new AbstractJsonFile(path, name){};
    }
}
