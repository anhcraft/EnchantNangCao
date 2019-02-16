package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ColorSheep extends EnchantmentBox implements Listener {
    @EventHandler
    public void PlayerDamageSheep(EntityDamageByEntityEvent ev){
        if(ev.getEntity() instanceof Sheep && ev.getDamager() instanceof Player) {
            Player d = (Player) ev.getDamager();
            ItemStack i = d.getInventory().getItemInMainHand();
            if(checkRequirements(i, d)){
                Sheep s = (Sheep) ev.getEntity();
                if(!s.isSheared()){
                    List<DyeColor> colors = Arrays.asList(DyeColor.values());
                    Collections.shuffle(colors,new Random(System.nanoTime()));
                    for(DyeColor e: colors) {
                        s.setColor(e);
                    }
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "ColorSheep";
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
        return 1;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Thay đổi màu cừu khi bạn đánh nó"
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

    public ColorSheep(){
        EnchantsAPI.register(this);
    }
}
