package net.omni.mythicbosses.handlers;

import net.omni.mythicbosses.MythicBosses;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final YamlConfiguration config;
    private final File file;

    public ConfigHandler(MythicBosses plugin, String fileName) {

        plugin.getDataFolder().mkdir();

        this.file = new File(plugin.getDataFolder(), fileName + ".yml");

        if (!file.exists())
            plugin.saveResource(fileName + ".yml", false);

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException var2) {
            var2.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public String getStringOrDefault(String path, String def) {
        return config.getString(path, def);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public void set(String s, Object o) {
        this.config.set(s, o);
        save();
    }
}
