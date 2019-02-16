package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.DnaEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Savior extends EnchantmentBox implements Listener {
    public Savior(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.2+8");
        initConfig("heal", "{lv}*1.2+2");
        initConfig("potion_time", "{lv}+7");

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                    for(ItemStack i : p.getInventory().getArmorContents()){
                        if(!InventoryUtils.isNull(i)
                                && MaterialUtils.getArmorTypes().contains(i.getType())){
                            if(checkRequirements(i, p) &&
                                    p.getHealth() <= (p.getMaxHealth()/100*25)){
                                if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                                    Title.create("&b&lSavior coming!", Title.Type.TITLE).sendPlayer(p);
                                    setCooldown(enchantName(), p);
                                    double heal =  mathConfig("heal", i);
                                    double newheal = heal + p.getHealth();
                                    if(p.getMaxHealth() < newheal){
                                        newheal = p.getMaxHealth();
                                    }
                                    p.setHealth(newheal);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                                            (int) mathConfig("potion_time", i), 1));
                                    DnaEffect se = new DnaEffect(EnchantNangCao.effect);
                                    se.particleBase1 = Particle.CLOUD;
                                    se.particleBase2 = Particle.REDSTONE;
                                    se.colorBase1 = Color.WHITE;
                                    se.colorBase1 = Color.RED;
                                    se.colorBase1 = Color.GREEN;
                                    se.asynchronous = true;
                                    se.duration = 5;
                                    se.setLocation(p.getLocation());
                                    se.start();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(EnchantNangCao.plugin, 60, 60);
    }

    @Override
    public String enchantName() {
        return "Savior";
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
        return 3;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Giúp bạn hồi phục và chạy nhanh hơn khi còn ít máu",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Lượng máu hồi: &e"+getConfig().getString("heal"),
                "&c* Thời gian chạy nhanh: &e"+getConfig().getString("potion_time")
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