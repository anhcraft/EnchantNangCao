package org.anhcraft.enchantnangcao.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class EnchantsConfig {
    private static HashMap<String, YamlConfiguration> data = new HashMap<>();

    public static void register(EnchantmentBox enchantmentBox, YamlConfiguration config){
        data.put(enchantmentBox.enchantName(), config);
    }

    public static YamlConfiguration getConfig(EnchantmentBox enchantmentBox){
        if(data.containsKey(enchantmentBox.enchantName())){
            return data.get(enchantmentBox.enchantName());
        }
        return new YamlConfiguration();
    }

    public static YamlConfiguration save(){
        YamlConfiguration config = new YamlConfiguration();
        for(String eb : data.keySet()){
            YamlConfiguration ec = data.get(eb);
            for(String n : ec.getKeys(true)){
                String x = eb + "." + n;
                if(!config.isSet(x)) {
                    config.set(x, ec.get(n));
                }
            }
        }
        return config;
    }

    public static void load(YamlConfiguration configuration) {
        for(String n : configuration.getKeys(false)){
            ConfigurationSection x = configuration.getConfigurationSection(n);
            YamlConfiguration c = new YamlConfiguration();
            for(String t : x.getKeys(true)){
                c.set(t, x.get(t));
            }
            data.put(n, c);
        }
    }
}
