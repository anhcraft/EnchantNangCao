package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Particle;
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

public class Laser extends EnchantmentBox implements Listener {
    public Laser(){
        EnchantsAPI.register(this);

        initConfig("power", "{lv}+1");
        initConfig("cooldown", "{lv}*1.5+3");
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
                if(isTimeout(enchantName(), p, mathConfig("cooldown", item))){
                    float power = (float) mathConfig("power", item);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 4f, 5f);
                    main:
                    for(double g = 0; g < 150; g+= 0.5) {
                        Location target = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(g));
                        Particle.create(Particle.Type.VILLAGER_HAPPY, target, 1).sendNearby(target, 20);
                        for(Entity ent : p.getWorld().getNearbyEntities(target, 1, 1, 1)) {
                            if(ent instanceof LivingEntity && !ent.equals(p)) {
                                if(!ent.hasMetadata("shopkeeper") && !ent.hasMetadata("NPC")) {
                                    target.getWorld().createExplosion(target.getX(), target.getY(), target.getZ(), power, false, false);
                                    ent.setFireTicks(100);
                                    break main;
                                }
                            }
                        }
                    }
                    setCooldown(enchantName(), p);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft(enchantName(), p, (int) mathConfig("cooldown", item))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Laser";
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
    protected String[] enchantDescription() {
        return new String[]{
                "Bắn ra tia laser, giúp thiêu đốt kẻ thù",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Sức mạnh: &e"+getConfig().getString("power")
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
}
