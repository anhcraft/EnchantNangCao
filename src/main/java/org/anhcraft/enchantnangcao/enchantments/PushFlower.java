package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
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

import java.util.Collection;

public class PushFlower extends EnchantmentBox implements Listener {
    public PushFlower(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.8+1.2");
        initConfig("push_amount", "{lv}+1");
        initConfig("push_distance", "{lv}*2");
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
                if(isTimeout("pushflower", p, (int) mathConfig("cooldown", item))){
                    Title.create("&4&lPushflower", Title.Type.TITLE).sendPlayer(p);
                    Title.create("&aHOA XOÁY", Title.Type.SUBTITLE).sendPlayer(p);
                    p.getWorld().playSound(p.getLocation(), GameVersion.is1_13Above() ? Sound.BLOCK_SLIME_BLOCK_HIT : Sound.valueOf("BLOCK_SLIME_HIT"), 4f, 5f);
                    for(double i = 0; i < 50; i++){
                        Location target = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(i));
                        Collection<Entity> x = target.getWorld().getNearbyEntities(target, 5, 5, 5);
                        int amount = 0;
                        for(Entity ent : x) {
                            if(ent instanceof LivingEntity && !ent.equals(p)) {
                                if(!ent.hasMetadata("shopkeeper") && !ent.hasMetadata("NPC")) {
                                    ent.setVelocity(ent.getLocation().getDirection().multiply(-mathConfig("push_distance", item)));
                                    ent.getWorld().playSound(ent.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 4f, 5f);
                                    ((LivingEntity) ent).damage(new EnchantsAPI(item).getEnchantLevel(this));
                                    amount++;
                                    if(mathConfig("push_amount", item) <= amount){
                                        break;
                                    }
                                }
                            }
                        }
                        if(0 < amount){
                            break;
                        }
                    }
                    setCooldown("pushflower", p);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft("pushflower", p, (int) mathConfig("cooldown", item))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "PushFlower";
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
                "Chuột phải để bắn ra bông hoa có khả năng hất thực thể ở gần ra xa",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Số lượng thực thể tối đa: &e"+getConfig().getString("push_amount"),
                "&c* Khoảng cách hất: &e"+getConfig().getString("push_distance")
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