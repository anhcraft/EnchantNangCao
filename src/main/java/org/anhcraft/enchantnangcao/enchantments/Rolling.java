package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Rolling extends EnchantmentBox implements Listener {
    @EventHandler
    public void onPlayerJump(EntityDamageEvent ev){
        if(ev.getEntity() instanceof Player){
            Player d = (Player) ev.getEntity();
            if(ev.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                for(ItemStack item : d.getInventory().getArmorContents()) {
                    if(checkRequirements(item, d) && MaterialUtils.getArmorTypes().contains(item.getType())) {
                        if(Math.random() < mathConfig("chance", item) / 100) {
                                            ev.setDamage(0);
                                            ev.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Rolling";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
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
                "Có tỉ lệ giúp bạn không bị mất máu khi rơi",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance")
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

    public Rolling(){
        initConfig("chance", "{lv}*10");
        EnchantsAPI.register(this);
    }
}
