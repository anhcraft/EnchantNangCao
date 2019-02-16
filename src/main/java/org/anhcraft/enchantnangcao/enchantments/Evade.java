package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Evade extends EnchantmentBox {
    public Evade(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*2+15");
        initConfig("radius", "{lv}*1.5+4");

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                    for(ItemStack i : p.getInventory().getArmorContents()){
                        if(!InventoryUtils.isNull(i)
                                && MaterialUtils.getArmorTypes().contains(i.getType())){
                            if(checkRequirements(i, p) &&
                                    p.getHealth() <= (p.getMaxHealth()/100*20)){
                                if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                                    Title.create("&6&lEvade!", Title.Type.TITLE).sendPlayer(p);
                                    setCooldown(enchantName(), p);
                                    double around = mathConfig("radius", i);
                                    for(Entity a : p.getNearbyEntities(around, around, around)) {
                                        if(a instanceof Monster) {
                                            Monster l = (Monster) a;
                                            l.setVelocity(l.getLocation().getDirection().normalize().multiply(-2));
                                        }
                                    }
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
        return "Evade";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack itemStack) {
        return MaterialUtils.getArmorTypes().contains(itemStack.getType());
    }

    @Override
    public int enchantMaxLevel() {
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion gameVersion) {
        return true;
    }

    @Override
    protected String[] enchantDescription() {
        return new String[]{
                "Giúp đẩy lùi quái ở xung quanh khi bạn dưới 20% máu",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Bán kính: &e"+getConfig().getString("radius"),
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
