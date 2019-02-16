package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.BleedEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Vampire extends EnchantmentBox implements Listener {
    public Vampire(){
        EnchantsAPI.register(this);
        initConfig("chance", "{lv}*10");
        initConfig("bloodsucking_amount", "{lv}*2+5");
    }

    @EventHandler(ignoreCancelled = true)
    public void attack(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player p = (Player) event.getDamager();
            ItemStack i = p.getInventory().getItemInMainHand();
            if(checkRequirements(i, p) && p.getHealth() <= (p.getMaxHealth()/2)){
                double d = mathConfig("chance", i);
                LivingEntity x = ((LivingEntity) event.getEntity());
                if(Math.random() < d/100) {
                    double c = mathConfig("bloodsucking_amount", i);
                    double q = p.getHealth() + c;
                    if(p.getMaxHealth() < q) {
                        q = p.getMaxHealth();
                    }
                    double g = x.getHealth() - c;
                    if(g < 0){
                        g = 0;
                    }

                    BleedEffect se = new BleedEffect(EnchantNangCao.effect);
                    se.asynchronous = true;
                    se.setLocation(p.getLocation());
                    se.start();
                    x.setHealth(g);
                    p.setHealth(q);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Vampire";
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
                "Có một tỉ lệ để hút máu thực thể bị đánh khi bạn dưới nửa cây máu",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Số máu sẽ hút: &e"+getConfig().getString("bloodsucking_amount"),
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
}
