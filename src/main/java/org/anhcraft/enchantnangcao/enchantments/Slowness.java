package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Slowness extends EnchantmentBox implements Listener {
    public Slowness(){
        EnchantsAPI.register(this);

        initConfig("time", "{lv}*1.2+2");
        initConfig("chance", "{lv}*10");
    }

    @Override
    public String enchantName() {
        return "Slowness";
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
        return 10;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ làm cho thực thể đã đánh đi chậm",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Thời gian bị (giây): &e"+getConfig().getString("time")
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

    @EventHandler
    public void a(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player p = (Player) e.getDamager();
            LivingEntity target = (LivingEntity) e.getEntity();
            if(checkRequirements(p.getInventory().getItemInMainHand(), p)) {
                ItemStack i = p.getInventory().getItemInMainHand();
                double seconds = mathConfig("time", i);
                double chance = mathConfig("chance", i)/100;
                if(Math.random() <= chance){
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                            (int) (seconds * 20), 1));
                }
            }
        }
    }
}