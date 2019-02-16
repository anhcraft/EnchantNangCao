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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Wing extends EnchantmentBox implements Listener {
    public Wing(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*10");
        initConfig("time", "{lv}*1.2+1.5");
    }

    @Override
    public String enchantName() {
        return "Wing";
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
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return !version.toString().contains("1_8");
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Cung có phù phép này nếu bắn sẽ có một tỉ",
                "lệ nhất định cho thực thể bị bay lên",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Thời gian (giây): &e"+getConfig().getString("time")
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
                double seconds = mathConfig("time", i);
                double chance = mathConfig("chance", i) / 100;
                if(Math.random() <= chance) {
                    e.getArrow().remove();
                    ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                            (int) (seconds * 20), 1));
                }
            }
        }
    }
}