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

import java.util.concurrent.ThreadLocalRandom;

public class Pull extends EnchantmentBox implements Listener {
    public Pull(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.8+1.2");
        initConfig("pull_amount", "{lv}+1");
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
                if(isTimeout("pull", p, mathConfig("cooldown", item))){
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 4f, 5f);
                    double x = ThreadLocalRandom.current().nextDouble(-1, 1);
                    double y = ThreadLocalRandom.current().nextDouble(-1, 1);
                    double z = ThreadLocalRandom.current().nextDouble(-1, 1);
                    double pa = mathConfig("pull_amount", item);
                    Sound s = GameVersion.is1_13Above() ? Sound.BLOCK_SLIME_BLOCK_HIT : Sound.valueOf("BLOCK_SLIME_HIT");
                    int amount = 0;
                    for(int g = 0; g < 80; g++) {
                        Location target = p.getLocation().clone().add(p.getLocation().getDirection().normalize()
                                .multiply(g));
                        target.setX(target.getX() + x);
                        target.setY(target.getY() + y);
                        target.setZ(target.getZ() + z);
                        Particle.create(Particle.Type.VILLAGER_HAPPY, target, 1).sendNearby(target, 20);
                        for(Entity ent : p.getWorld().getNearbyEntities(target, 3.5, 3.5, 3.5)) {
                            if(ent instanceof LivingEntity && !ent.equals(p)) {
                                if(!ent.hasMetadata("shopkeeper") && !ent.hasMetadata("NPC")) {
                                    ent.setVelocity(p.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize());
                                    ent.getWorld().playSound(target, s, 4f, 5f);
                                    ((LivingEntity) ent).damage(new EnchantsAPI(item).getEnchantLevel(this));
                                    amount++;
                                    if(pa <= amount){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    setCooldown("pull", p);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft("pull", p, (int) mathConfig("cooldown", item))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Pull";
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
        return 10;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    protected String[] enchantDescription() {
        return new String[]{
                "Chuột phải để tạo ra sợi dây kéo thực thể về phía bạn",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Số lượng thực thể tối đa: &e"+getConfig().getString("pull_amount"),
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