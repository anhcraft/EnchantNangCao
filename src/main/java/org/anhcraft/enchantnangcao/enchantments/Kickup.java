package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Kickup extends EnchantmentBox implements Listener {
    @EventHandler
    public void enitityDamage(EntityDamageByEntityEvent ev){
        if(ev.getDamager() instanceof Player && ev.getEntity() instanceof LivingEntity){
            Player d = (Player) ev.getDamager();
            LivingEntity e = (LivingEntity) ev.getEntity();
            ItemStack i = d.getInventory().getItemInMainHand();
            if(checkRequirements(i, d) && ev.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                if(Math.random() < mathConfig("chance", i)/100){
                    e.setVelocity(e.getVelocity().setY(mathConfig("height", i)));
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Kickup";
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
                "Có tỉ lệ hất tung thực thể bị đánh lên cao",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Độ cao: &e"+getConfig().getString("height")
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

    public Kickup(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*10");
        initConfig("height", "{lv}*1.2+1");
    }
}
