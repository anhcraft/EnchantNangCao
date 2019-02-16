package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Escape extends EnchantmentBox implements Listener {
    public Escape(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*3+10");
        initConfig("heal", "{lv}*0.5");
    }

    @EventHandler
    public void move(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(!p.isSprinting() || p.isSneaking()){
           return;
        }
        for(ItemStack item : p.getInventory().getArmorContents()){
            if(checkRequirements(item, p)) {
                double heal = mathConfig("heal", item);
                double chance = mathConfig("chance", item)/100;
                if(Math.random() <= chance){
                    double nh = p.getHealth()+heal;
                    if(p.getMaxHealth() < nh){
                        nh = p.getMaxHealth();
                    }
                    p.setHealth(nh);
                    break;
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Escape";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
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
    public String[] enchantDescription() {
        return new String[]{
                "Khi bạn sprint (chạy) thì sẽ có",
                "một tỉ lệ để hồi thêm máu",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Số lượng máu sẽ hồi: &e"+getConfig().getString("heal")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemArmors();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.AUTO_ACTIVATE;
    }
}
