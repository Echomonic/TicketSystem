package me.themoonis.ticketSystem.file.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractJsonFile implements JsonFile {

    protected final File path;
    protected final String name;
    protected File file;

    protected ObjectNode root;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected boolean firstTime = false;

    protected AbstractJsonFile(File path, String name) {
        this.path = path;
        this.name = name.endsWith(".json") ? name : name + ".json";
    }

    @SneakyThrows
    @Override
    public void load() {
        if (!path.exists()) path.mkdirs();

        file = new File(path, name);

        if (!file.exists()) {
            file.createNewFile();
            root = mapper.createObjectNode();
            firstTime = true;
        } else {
            JsonNode read = mapper.readTree(file);
            root = read != null && read.isObject() ? (ObjectNode) read : mapper.createObjectNode();
        }

        if (firstTime) {
            loadDefaults();
            firstTime = false;
        }
    }

    @SneakyThrows
    @Override
    public void save() {
        if (file != null && root != null) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
        }
    }

    @Override
    public void unload() {
        save();
        this.file = null;
        this.root = null;
        System.gc();
    }

    @Override
    public void reload() {
        try {
            JsonNode read = mapper.readTree(file);
            root = read != null && read.isObject() ? (ObjectNode) read : mapper.createObjectNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        save();
    }

    protected void loadDefaults() {
    }

    @Override
    public boolean exists(String path) {
        return getNode(path) != null;
    }

    @Override
    public boolean exists(JsonNode section, String path) {
        return getNode(section, path) != null;
    }

    @Override
    public void set(String path, Object value) {
        set(root, path, value);
    }

    @Override
    public void set(ObjectNode section, String path, Object value) {
        String[] keys = path.split("\\.");
        ObjectNode current = section;

        for (int i = 0; i < keys.length - 1; i++) {
            JsonNode next = current.get(keys[i]);
            if (!(next instanceof ObjectNode)) {
                next = current.putObject(keys[i]);
            }
            current = (ObjectNode) next;
        }

        current.set(keys[keys.length - 1], mapper.valueToTree(value));
    }

    @Override
    public ObjectNode section(String path) {
        JsonNode node = getNode(path);
        if (node == null || !node.isObject()) {
            set(path, mapper.createObjectNode());
        }
        return (ObjectNode) getNode(path);
    }

    @Override
    public ObjectNode section(String path, Map<?, ?> map) {
        set(path, map);
        return (ObjectNode) getNode(path);
    }

    @Override
    public <T> T get(String path) {
        return get(root, path);
    }

    @Override
    public <T> T get(String path, T def) {
        return get(root, path, def);
    }

    @Override
    public <T> T get(String path, Class<T> cast) {
        return get(root, path, cast);
    }

    @Override
    public <T> T get(String path, T def, Class<T> cast) {
        return get(root, path, def, cast);
    }

    @Override
    public <T> T get(JsonNode section, String path) {
        JsonNode node = getNode(section, path);
        return node != null ? (T) convert(node, Object.class) : null;
    }

    @Override
    public <T> T get(JsonNode section, String path, Class<T> cast) {
        JsonNode node = getNode(section, path);
        return node != null ? convert(node, cast) : null;
    }

    @Override
    public <T> T get(JsonNode section, String path, T def) {
        JsonNode node = getNode(section, path);
        return node != null ? (T) convert(node, def.getClass()) : def;
    }

    @Override
    public <T> T get(JsonNode section, String path, T def, Class<T> cast) {
        JsonNode node = getNode(section, path);
        return node != null ? convert(node, cast) : def;
    }

    private JsonNode getNode(String path) {
        return getNode(root, path);
    }

    private JsonNode getNode(JsonNode base, String path) {
        String[] keys = path.split("\\.");
        JsonNode current = base;
        for (String key : keys) {
            if (current == null || !current.has(key)) return null;
            current = current.get(key);
        }
        return current;
    }

    private <T> T convert(JsonNode node, Class<T> type) {
        try {
            return mapper.treeToValue(node, type);
        } catch (Exception e) {
            return null;
        }
    }
}
