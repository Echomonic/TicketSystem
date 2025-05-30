package me.themoonis.ticketSystem.file.json;

import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Map;

public interface YamlFile {

    void load();

    void save();

    void unload();

    void reload();

    <T> T get(String path);

    <T> T get(String path, T def);

    <T> T get(String path, Class<T> cast);

    <T> T get(String path, T def, Class<T> cast);

    <T> T get(ConfigurationSection section, String path);

    <T> T get(ConfigurationSection section, String path, Class<T> cast);

    <T> T get(ConfigurationSection section, String path, T def);

    <T> T get(ConfigurationSection section, String path, T def, Class<T> cast);


    void set(String path, Object a);

    void set(ConfigurationSection section, String path, Object value);

    ConfigurationSection section(String path);
    ConfigurationSection section(String path, Map<?,?> map);

    boolean exists(String path);

    boolean exists(ConfigurationSection section, String path);
    static YamlFile create(File path, String name){
        return new AbstractYamlFile(path,name){};
    }


}
