package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.events.PlayerJumpEvent;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DoubleJump extends EnchantmentBox implements Listener {
    public DoubleJump(){
        EnchantsAPI.register(this);

        initConfig("high", "{lv}*0.5");
    }

    @EventHandler
    public void jump(PlayerJumpEvent e){
        Player p = e.getPlayer();
        if(!e.isOnSpot()){
            return;
        }
        for(ItemStack item : p.getInventory().getArmorContents()){
            if(checkRequirements(item, p)){
                if(isTimeout("doublejump", p, 1)){
                    setCooldown("doublejump", p);
                } else {
                    p.setVelocity(p.getVelocity().setY(mathConfig("high", item)));
                }
                break;
            }
        }
    }

    @Override
    public String enchantName() {
        return "DoubleJump";
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
                "Giúp bạn nhảy cao hơn khi nhảy hai lần liên tục tại chỗ",
                "&c* Độ cao: &e"+getConfig().getString("high")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemArmors();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
