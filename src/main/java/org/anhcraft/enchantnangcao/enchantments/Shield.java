package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Shield extends EnchantmentBox implements Listener {
    @Override
    public String enchantName() {
        return "Shield";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
    }

    public Shield(){
        EnchantsAPI.register(this);
        initConfig("distance", "{lv}*0.1+1");
        initConfig("chance", "{lv}*10");
    }

    @Override
    public int enchantMaxLevel() {
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ hất văng người đánh bạn",
                "Bạn sẽ chỉ nhận nửa sát thương so với ban đầu",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Khoảng cách hất văng: &e"+getConfig().getString("distance"),
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
        if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player) {
            Player d = (Player) ev.getDamager();
            Player e = (Player) ev.getEntity();
            for(ItemStack item : e.getInventory().getArmorContents()){
                if(checkRequirements(item, e) &&
                        MaterialUtils.getArmorTypes().contains(item.getType())
                        && ev.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
                    ev.setCancelled(true);
                    ev.setDamage(ev.getDamage()/2);
                    if(Math.random() < mathConfig("chance", item)/100){
                        d.setVelocity(d.getEyeLocation().getDirection().multiply
                                (mathConfig("distance", item)));
                    }
                }
            }
        }
    }
}
