package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ProtectionOfTearySkies extends EnchantmentBox implements Listener {
    public ProtectionOfTearySkies(){
        EnchantsAPI.register(this);

        initConfig("power", "{lv}*1.2+1.5");
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player p = (Player) event.getDamager();
            ItemStack i = p.getInventory().getChestplate();
            if(checkRequirements(i, p) && p.getWorld().hasStorm()) {
                event.setDamage(event.getDamage()+mathConfig("power", i));
                return;
            }
        }
        if(event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            ItemStack i = p.getInventory().getChestplate();
            if(checkRequirements(i, p) && p.getWorld().hasStorm()) {
                event.setDamage(event.getDamage()-mathConfig("power", i));
            }
        }
    }

    @Override
    public String enchantName() {
        return "ProtectionOfTearySkies";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_CHESTPLATE");
    }

    @Override
    public int enchantMaxLevel() {
        return 3;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    protected String[] enchantDescription() {
        return new String[]{
                "Giúp bạn khỏe hơn khi trời mưa",
                "&c* Sức mạnh: &e"+getConfig().getString("power")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemChestplate();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }
}
