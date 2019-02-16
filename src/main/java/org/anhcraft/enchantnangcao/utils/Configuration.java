package org.anhcraft.enchantnangcao.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Configuration {
    public static FileConfiguration config;
    public static File d = new File("plugins/EnchantNangCao/config.yml");

    public static void loadConfigFile(){
        config = YamlConfiguration.loadConfiguration(d);
    }

    public static void load(){
        loadConfigFile();
    }
}
