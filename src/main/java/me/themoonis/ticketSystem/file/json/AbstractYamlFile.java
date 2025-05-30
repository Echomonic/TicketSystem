package me.themoonis.ticketSystem.file.json;

import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public abstract class AbstractYamlFile implements YamlFile {

    private final File path;
    private final String name;

    protected AbstractYamlFile(File path, String name) {
        this.path = path;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
    }

    private File file;
    private YamlConfiguration configuration;

    private boolean firstTime = false;

    @SneakyThrows
    @Override
    public void load() {
        if (!path.exists())
            path.mkdirs();

        file = new File(path, name);


        if (!file.exists()) {
            file.createNewFile();
            firstTime = true;
        }
        this.configuration = YamlConfiguration.loadConfiguration(file);

        if (firstTime) {
            loadDefaults();
            firstTime = false;
        }
    }

    @Override
    public void unload() {
        save();
        this.file = null;
        this.configuration = null;
        System.gc();
    }

    @Override
    @SneakyThrows
    public void save() {
        if (file == null) return;
        configuration.save(file);
    }


    @Override
    public void set(String path, Object a) {
        set(configuration, path, a);
    }

    @Override
    public void set(ConfigurationSection section, String path, Object value) {
        section.set(path, value);
    }

    public void reload() {
        save();
        this.configuration = YamlConfiguration.loadConfiguration(new File(file.toURI()));
    }

    protected void loadDefaults() {
    }

    @Override
    public ConfigurationSection section(String path) {
        if (!exists(path))
            return configuration.createSection(path);

        return configuration.getConfigurationSection(path);
    }

    @Override
    public ConfigurationSection section(String path, Map<?, ?> map) {
        if (exists(path))
            configuration.set(path, null);

        return configuration.createSection(path, map);
    }

    @Override
    public boolean exists(String path) {
        return configuration.contains(path);
    }

    @Override
    public boolean exists(ConfigurationSection section, String path) {
        return section.contains(path);
    }

    @Override
    public <T> T get(String path) {
        return get(configuration, path);
    }

    @Override
    public <T> T get(String path, T def) {
        return get(configuration, path, def);
    }

    @Override
    public <T> T get(String path, Class<T> cast) {
        return get(configuration, path, cast);
    }

    @Override
    public <T> T get(String path, T def, Class<T> cast) {
        return get(configuration, path, def, cast);
    }

    @Override
    public <T> T get(ConfigurationSection section, String path) {
        return (T) section.get(path);
    }

    @Override
    public <T> T get(ConfigurationSection section, String path, Class<T> cast) {
        return cast.cast(section.get(path));
    }

    @Override
    public <T> T get(ConfigurationSection section, String path, T def) {
        return (T) section.get(path, def);
    }

    @Override
    public <T> T get(ConfigurationSection section, String path, T def, Class<T> cast) {
        return !exists(section, path) ? def : get(section, path, cast);
    }

}
