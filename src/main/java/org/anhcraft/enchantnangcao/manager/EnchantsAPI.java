package org.anhcraft.enchantnangcao.manager;

import org.anhcraft.enchantnangcao.utils.RomanNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EnchantsAPI {
    public static LinkedHashMap<String,EnchantmentBox> enchants = new LinkedHashMap<>();

    public static void register(EnchantmentBox eb){
        List<String> x = new ArrayList<>();
        x.add("world_nether");
        x.add("world_the_end");
        eb.initConfig("enable", true);
        eb.initConfig("custom_name", eb.enchantName());
        eb.initConfig("worlds", x);
        eb.initConfig("worlds_as_blacklist", true);
        enchants.put(eb.enchantName().toUpperCase(), eb);
    }

    public static boolean isEnchantListed(String enchant){
        for(EnchantmentBox enc : enchants.values()){
            if(enc.customName().equals(enchant)) {
                return true;
            }
        }
        return false;
    }

    public static EnchantmentBox getEnchant(String enchant){
        for(EnchantmentBox enc : enchants.values()){
            if(enc.customName().equals(enchant)) {
                return enc;
            }
        }
        return null;
    }

    private ItemStack item;

    public EnchantsAPI(ItemStack item){
        this.item = item;
    }

    public boolean hasEnchants(){
        if(this.item.hasItemMeta()){
            for(EnchantmentBox enc : enchants.values()){
                if(hasEnchant(enc)){
                    return true;
                }
            }
        }
        return false;
    }

    public List<EnchantmentBox> getEnchants(){
        List<EnchantmentBox> r = new ArrayList<>();
        if(this.item.hasItemMeta()) {
            for(EnchantmentBox enc : enchants.values()) {
                if(hasEnchant(enc)) {
                    r.add(enc);
                }
            }
        }
        return r;
    }

    public boolean hasEnchant(EnchantmentBox enchant){
        if(this.item.hasItemMeta()){
            ItemMeta m = this.item.getItemMeta();
            if(m.hasLore()) {
                for(String lore : m.getLore()) {
                    if(lore.indexOf("§2§6§7" + enchant.customName()) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getEnchantLevel(EnchantmentBox enchant){
        int r = -1;
        if(this.item.hasItemMeta()) {
            ItemMeta m = this.item.getItemMeta();
            if(m.hasLore()) {
                for(String lore : m.getLore()) {
                    if(lore.indexOf("§2§6§7" + enchant.customName()) == 0) {
                        r = RomanNumber.toDecimal(lore.split(" ")[1]);
                        break;
                    }
                }
            }
        }
        return r;
    }

    public void removeEnchant(EnchantmentBox enchant){
        if(hasEnchant(enchant) && this.item.getItemMeta().hasLore()){
            List<String> newLores = new ArrayList<>();
            if(this.item.hasItemMeta()) {
                ItemMeta m = this.item.getItemMeta();
                for(String lore : m.getLore()) {
                    if(lore.indexOf("§2§6§7" + enchant.customName()) != 0) {
                        newLores.add(lore);
                    }
                }
                m.setLore(newLores);
                this.item.setItemMeta(m);
            }
        }
    }

    public void addEnchant(EnchantmentBox enchant, int level){
        if(hasEnchant(enchant) && this.item.getItemMeta().hasLore()) {
            removeEnchant(enchant);
        }
        ItemMeta m = this.item.getItemMeta();
        List<String> newLores = new ArrayList<>();
        newLores.add("§2§6§7" + enchant.customName() + " " + RomanNumber.toRoman(level));
        if(this.item.getItemMeta().hasLore()) {
            newLores.addAll(m.getLore());
        }
        m.setLore(newLores);
        this.item.setItemMeta(m);
    }

    public ItemStack getItem(){
        return this.item.clone();
    }
}
