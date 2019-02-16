package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Particle;
import org.anhcraft.spaciouslib.protocol.Title;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FireThrower extends EnchantmentBox implements Listener{
    public FireThrower(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.8+1.5");
        initConfig("burn_time", "{lv}+1.5");
        initConfig("damage", "{lv}+1.2");
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
                if(isTimeout("firethrower", p, mathConfig("cooldown", item))){
                    setCooldown("firethrower", p);
                    int ft = (int) mathConfig("burn_time", item) * 20;
                    double dm = mathConfig("damage", item);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Title.create("&c&lFireThrower", Title.Type.TITLE).sendPlayer(p);
                            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 4f, 5f);
                            double x = ThreadLocalRandom.current().nextDouble(-1, 1);
                            double y = ThreadLocalRandom.current().nextDouble(-1, 1);
                            double z = ThreadLocalRandom.current().nextDouble(-1, 1);
                            for(int g = 0; g < 20; g++) {
                                Location target = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(g));
                                target.setX(target.getX() + x);
                                target.setY(target.getY() + y);
                                target.setZ(target.getZ() + z);
                                Particle.create(Particle.Type.CLOUD, target, 1).sendNearby(target, 20);
                                Particle.create(Particle.Type.LAVA, target, 1).sendNearby(target, 20);
                                List<LivingEntity> n = new ArrayList<>();
                                for(Entity ent : p.getWorld().getNearbyEntities(target, 3.5, 3.5, 3.5)) {
                                    if(ent instanceof LivingEntity && !ent.equals(p)) {
                                        if(!ent.hasMetadata("shopkeeper") && !ent.hasMetadata("NPC")) {
                                            ent.getWorld().playSound(target, Sound.BLOCK_FIRE_AMBIENT, 4f, 5f);
                                            n.add((LivingEntity) ent);
                                        }
                                    }
                                }
                                if(n.size() > 0){
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {

                                            for(Entity ent : n) {
                                                ent.setFireTicks(ft);
                                                ((LivingEntity) ent).damage(dm);
                                            }
                                        }
                                    }.runTaskLater(EnchantNangCao.plugin, 0);
                                }
                            }
                        }
                    }.runTaskLaterAsynchronously(EnchantNangCao.plugin, 0);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft("firethrower", p, (int) mathConfig("cooldown", item))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "FireThrower";
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
                "Chuột phải để phun lửa",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Thời gian cháy: &e"+getConfig().getString("burn_time"),
                "&c* Sát thương: &e"+getConfig().getString("damage")
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
