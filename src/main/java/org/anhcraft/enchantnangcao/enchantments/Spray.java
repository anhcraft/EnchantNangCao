package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Particle;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.scheduler.DelayedTask;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Spray extends EnchantmentBox implements Listener {
    public Spray(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+1.2");
        initConfig("level", "{lv}/3");
    }

    @EventHandler
    public void trigger(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR)
                || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = p.getInventory().getItemInMainHand();
            if(checkRequirements(item, p)){
                if(isTimeout(enchantName(), p, mathConfig("cooldown", item))) {
                    Title.create("&b&lSpray", Title.Type.TITLE).sendPlayer(p);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 4f, 5f);
                    new DelayedTask(() -> {
                        for(int i = 0; i < 10; i++) {
                            double y = ThreadLocalRandom.current().nextDouble(-1, 1);
                            for(int g = 0; g < 80; g++) {
                                Location target = p.getLocation().clone().add(p.getLocation().getDirection().normalize()
                                        .multiply(g));
                                target.setY(target.getY() + y);
                                Particle.create(Color.cyan, target).sendNearby(target, 20);
                                if(i == 4) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            int lv = (int) mathConfig("cooldown", item);
                                            if(lv < 0) {
                                                lv = 0;
                                            }
                                            for(Entity ent : p.getWorld().getNearbyEntities(target, 3.5, 3.5, 3.5)) {
                                                if(ent instanceof LivingEntity && !ent.equals(p)) {
                                                    if(!ent.hasMetadata("shopkeeper") && !ent.hasMetadata("NPC")) {
                                                        ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, lv));
                                                        ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, lv));
                                                        ((LivingEntity) ent).damage(ThreadLocalRandom.current().nextDouble(1, 3));
                                                    }
                                                }
                                            }
                                        }
                                    }.runTaskLater(EnchantNangCao.plugin, 0);
                                }
                            }
                        }
                    }, 0).run();
                    setCooldown(enchantName(), p);
                } else{
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft(enchantName(), p, (int) mathConfig("cooldown", item))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Spray";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_SWORD");
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
    protected String[] enchantDescription() {
        return new String[]{
                "Chuột phải để phóng ra luồng nước về phía trước",
                "khiến kẻ thù đi chậm và bị mù.",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Mức độ đi chậm/bị mù: &e"+getConfig().getString("level"),
        };
    }

    @Override
    public String enchantItemStack() {
        return "Kiếm";
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}