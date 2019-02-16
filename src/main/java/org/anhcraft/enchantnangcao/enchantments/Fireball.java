package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Fireball extends EnchantmentBox implements Listener {
    @EventHandler
    public void throwFireball(PlayerInteractEvent e){
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        Player p = e.getPlayer();
        if(checkRequirements(p.getInventory().getItemInMainHand(), p)){
            ItemStack i = p.getInventory().getItemInMainHand();
            if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                p.launchProjectile(org.bukkit.entity.SmallFireball.class)
                        .setVelocity(p.getLocation().getDirection().multiply(2));
                setCooldown(enchantName(), p);
            }
        }
    }

    @Override
    public String enchantName() {
        return "Fireball";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return true;
    }

    @Override
    public int enchantMaxLevel() {
        return 1;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Bắn ra cầu lửa (Chuột phải)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAllItems();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }

    public Fireball(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+2");
    }
}
