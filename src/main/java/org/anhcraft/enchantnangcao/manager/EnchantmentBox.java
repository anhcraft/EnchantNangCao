package org.anhcraft.enchantnangcao.manager;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.utils.WorldGuardHook;
import org.anhcraft.spaciouslib.annotations.PlayerCleaner;
import org.anhcraft.spaciouslib.utils.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class EnchantmentBox {
    @PlayerCleaner
    public static final HashMap<UUID, LinkedHashMap<String, Cooldown>> cooldown = new HashMap<>();

    public abstract String enchantName();
    public abstract String enchantAuthor();
    public String enchantProposer(){
        return null;
    }
    public abstract Boolean canEnchantItem(ItemStack i);
    public abstract int enchantMaxLevel();
    public abstract Boolean enchantVersion(GameVersion version);
    protected abstract String[] enchantDescription();
    public abstract String enchantItemStack();
    public abstract EnchantType enchantType();

    public YamlConfiguration getConfig() {
        return EnchantsConfig.getConfig(this);
    }

    public void initConfig(String n, Object v) {
        YamlConfiguration config = getConfig();
        if(!config.isSet(n)) {
            config.set(n, v);
            EnchantsConfig.register(this, config);
        }
    }

    public String getDefaultAuthor(){
        return "ENC";
    }
    public String getDefaultItemArmors(){
        return "Giáp";
    }
    public String getDefaultItemAllItems(){
        return "Mọi vật phẩm";
    }
    public String getDefaultItemAxe(){
        return "Rìu";
    }
    public String getDefaultItemPickaxe(){
        return "Cúp";
    }
    public String getDefaultItemShovel(){
        return "Xẻng";
    }
    public String getDefaultItemHoe(){
        return "Cuốc";
    }
    public String getDefaultItemBow(){
        return "Cung";
    }
    public String getDefaultItemChestplate() {
        return "Áo";
    }
    public String getDefaultItemSword() {
        return "Kiếm";
    }
    public String getDefaultItemHelmet() {
        return "Nón";
    }
    public String getDefaultItemLeggings() {
        return "Quần";
    }
    public String getDefaultItemBoots() {
        return "Ủng";
    }
    public String getDefaultItemElytra() {
        return "Cánh cứng";
    }

    public boolean checkRequirements(ItemStack item, Entity entity){
        if(EnchantNangCao.worldGuard && !WorldGuardHook.check(entity)){
            return false;
        }
        boolean b;
        if(getConfig().getBoolean("worlds_as_blacklist")){
            b = !getConfig().getStringList("worlds")
                    .contains(entity.getLocation().getWorld().getName());
        } else {
            b = getConfig().getStringList("worlds")
                    .contains(entity.getLocation().getWorld().getName());
        }
        return !InventoryUtils.isNull(item) && b
                && getConfig().getBoolean("enable") && new EnchantsAPI(item).hasEnchant(this);
    }

    private String replacePlaceholder(String s, ItemStack item){
        if(InventoryUtils.isNull(item)){
            return s.replace("{lv}", "0").replace("{maxlv}", Integer.toString(enchantMaxLevel()));
        }
        return s.replace("{lv}", Integer.toString(new EnchantsAPI(item).getEnchantLevel(this)))
                .replace("{maxlv}", Integer.toString(enchantMaxLevel()));
    }

    public double mathConfig(String s, ItemStack item){
        return MathUtils.eval(replacePlaceholder(getConfig().getString(s), item));
    }

    public List<String> getEnchantDescription(){
        List<String> a = new ArrayList<>();
        for(String b : enchantDescription()){
            a.add("&f"+b.replace("{lv}", Chat.color("&e&l<&f&lCẤP ĐỘ&e&l>&r")));
        }
        return a;
    }

    public boolean isTimeout(String id, Player p, double seconds) {
        LinkedHashMap<String, Cooldown> cd = new LinkedHashMap<>();
        if(cooldown.containsKey(p.getUniqueId())) {
            cd = cooldown.get(p.getUniqueId());
        }
        return !cd.containsKey(id) || cd.get(id).isTimeout(seconds);
    }

    public void setCooldown(String id, Player p) {
        LinkedHashMap<String, Cooldown> cd = new LinkedHashMap<>();
        if(cooldown.containsKey(p.getUniqueId())) {
            cd = cooldown.get(p.getUniqueId());
        }
        cd.put(id, new Cooldown());
        cooldown.put(p.getUniqueId(), cd);
    }

    public double timeLeft(String id, Player p, double seconds) {
        LinkedHashMap<String, Cooldown> cd = new LinkedHashMap<>();
        if(cooldown.containsKey(p.getUniqueId())) {
            cd = cooldown.get(p.getUniqueId());
        }
        return cd.containsKey(id) ? MathUtils.round(cd.get(id).timeLeft(seconds)) : 0;
    }

    public String customName() {
        return getConfig().getString("custom_name", enchantName());
    }
}
