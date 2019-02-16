package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Kickback extends EnchantmentBox implements Listener {
    public Kickback(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+3");
        initConfig("for_animals", false);
        initConfig("entities_around", "{lv}+3");
    }

    @Override
    public String enchantName() {
        return "Kickback";
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
                "Đẩy lùi mọi thực thể trước mặt bạn (Chuột phải)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Bán kính tìm kiếm thực thể: &e"+getConfig().getString("entities_around")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAllItems();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        ItemStack i = p.getInventory().getItemInMainHand();
            if(checkRequirements(i, p)
                    && ((e.getAction().equals(Action.RIGHT_CLICK_AIR)) || e.getAction()
                    .equals(Action.RIGHT_CLICK_BLOCK))) {
                if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                    Title.create("&6&lĐẨY LÙI!!!!!!", Title.Type.TITLE).sendPlayer(p);
                    setCooldown(enchantName(), p);
                    int around = (int) mathConfig("entities_around", i);
                    for(Entity a : p.getNearbyEntities(around, around, around)){
                        if(!(a instanceof LivingEntity)){
                            return;
                        }
                        LivingEntity l = (LivingEntity) a;
                        Vector toEntity = l.getLocation().toVector().subtract(p.getLocation().toVector());
                        double dot = toEntity.normalize().dot(p.getLocation().getDirection());
                        if(dot > 0.5D){
                            if(l instanceof Monster){
                                l.setVelocity(l.getLocation().toVector()
                                        .subtract(p.getLocation().toVector()).normalize().multiply(2.0));
                            }
                            if(l instanceof Animals && getConfig().getBoolean("for_animals")){
                                l.setVelocity(l.getLocation().toVector()
                                        .subtract(p.getLocation().toVector()).normalize().multiply(2.0));
                            }
                        }
                    }
            }
        }
    }
}