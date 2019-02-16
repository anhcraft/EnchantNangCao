package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LifeGuard extends EnchantmentBox implements Listener {
    public static LinkedHashMap<OfflinePlayer, List<Wolf>> data = new LinkedHashMap<>();

    public static void destroy(){
        for(OfflinePlayer p : LifeGuard.data.keySet()) {
            for(Wolf a : LifeGuard.data.get(p)){
                if(!a.isDead()){
                    a.remove();
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "LifeGuard";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
    }

    public LifeGuard(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*10");
        initConfig("wolfs_amount", "{lv}*1.5+3");
        initConfig("life_time", "{lv}*1.2+10");
    }

    @Override
    public int enchantMaxLevel() {
        return 10;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ giúp gọi ra đàn sói bảo vệ bạn",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Số lượng sói: &e"+getConfig().getString("wolfs_amount"),
                "&c* Thời gian sói sẽ sống: &e"+getConfig().getString("life_time")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemArmors();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.AUTO_ACTIVATE;
    }

    @EventHandler
    public void attack(EntityDamageByEntityEvent ev){
        if(ev.getEntity() instanceof Player) {
            Player e = (Player) ev.getEntity();
            for(ItemStack item : e.getInventory().getArmorContents()){
                if(checkRequirements(item, e) &&
                        MaterialUtils.getArmorTypes().contains(item.getType()) &&
                        ev.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
                    if(!data.containsKey(e) && Math.random() < mathConfig("chance", item)/100) {
                        Title.create("&a&lTRIỆU HỒI SÓI!", Title.Type.TITLE).sendPlayer(e);
                        List<Wolf> w = new ArrayList<>();
                        int i = 0;
                        int x = (int) mathConfig("wolfs_amount", item);
                        while(i < x) {
                            Wolf a = ev.getDamager().getLocation().getWorld().spawn(
                                    ev.getDamager().getLocation(), Wolf.class);
                            a.setAngry(false);
                            a.setCustomName("Sói của "+e.getName());
                            a.setMaxHealth(e.getMaxHealth());
                            a.setHealth(e.getMaxHealth());
                            a.setTamed(true);
                            a.setOwner(e);
                            w.add(a);
                            i++;
                        }
                        data.put(e, w);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EnchantNangCao.plugin,
                                () -> {
                                    for(Wolf w1 : data.get(e)){
                                        w1.remove();
                                    }
                                    data.remove(e);
                                }, (long) (20 * mathConfig("life_time", item)));
                    }
                }
            }
        }
    }
}
