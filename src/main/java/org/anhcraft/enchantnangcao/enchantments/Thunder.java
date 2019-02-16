package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.events.BowArrowHitEvent;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Thunder extends EnchantmentBox implements Listener {
    public Thunder(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*10");
    }

    @Override
    public String enchantName() {
        return "Thunder";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().equals(Material.BOW);
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
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ để gọi sấm sét lên thực thể",
                "bị mũi tên đâm trúng",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemBow();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }

    @EventHandler
    public void a(BowArrowHitEvent e){
        if(e.getShooter() instanceof Player && e.getEvent().getHitEntity() != null) {
            Player p = (Player) e.getShooter();
            ItemStack i = e.getBow();
            Entity target = e.getEvent().getHitEntity();
            if(checkRequirements(i, p) && target instanceof LivingEntity) {
                double chance = mathConfig("chance", i) / 100;
                if(Math.random() <= chance) {
                    e.getArrow().remove();
                    target.getWorld().strikeLightning(target.getLocation());
                }
            }
        }
    }
}