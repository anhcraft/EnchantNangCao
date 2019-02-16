package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.enchantnangcao.utils.Configuration;
import org.anhcraft.spaciouslib.protocol.Particle;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Magixel extends EnchantmentBox implements Listener {
    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Gọi ra vòng tròn chết chóc, càng lúc càng to (Chuột phải)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Số lượng vòng tròn: &e"+getConfig().getString("circle_amount"),
                "&c* Bán kính: &e"+getConfig().getString("entities_around"),
                "&c* Sát thương trong mỗi vòng tròn: &e"+getConfig().getString("damage")
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
    public void playerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        Location l = p.getLocation();
        Vector v = l.getDirection().normalize().multiply(2);
        if(checkRequirements(i, p)) {
            if((e.getAction().equals(Action.RIGHT_CLICK_AIR)) ||
                    e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                int lv = new EnchantsAPI(i).getEnchantLevel(this);
                if(isTimeout(enchantName(),
                        p, (int) mathConfig("cooldown", i))) {
                    Title.create("&5&lMAGIXEL", Title.Type.TITLE).sendPlayer(p);
                    setCooldown(enchantName(), p);
                    double damage = mathConfig("damage", i);
                    int size = (int) mathConfig("circle_amount", i);
                    int er = (int) mathConfig("entities_around", i);
                    double dist = 1.2;
                    int delay = 20;
                    double h = 0.5;
                    for(int w = 0; w < size; w++) {
                        int wd = w;
                        Bukkit.getServer().getScheduler()
                                .scheduleSyncDelayedTask(EnchantNangCao.plugin, new Runnable() {
                            @Override
                            public void run() {
                                for(int degree = 0; degree < 360; degree++) {
                                    double radians = Math.toRadians(degree);
                                    double x = Math.cos(radians) * (wd + dist);
                                    double z = Math.sin(radians) * (wd + dist);
                                    v.setX(x);
                                    v.setY(h);
                                    v.setZ(z);
                                    l.add(v);
                                    if(wd % 2 == 0) {
                                        Particle.create(Particle.Type.FLAME, l, 1);
                                    } else {
                                        for(int k = 0; k < 5; k++) {
                                            Particle.create(Particle.Type.PORTAL, l, 1);
                                        }
                                    }
                                    l.subtract(x, h, z);
                                }

                                double a = size + dist;
                                for(Entity d : p.getNearbyEntities(a, h * er, a)) {
                                    if(!d.hasMetadata("shopkeeper") && !d.hasMetadata("NPC")){
                                        if(d instanceof Monster) {
                                            ((Monster) d).damage(damage);
                                        }
                                        if(d instanceof Animals) {
                                            ((Animals) d).damage(damage);
                                        }
                                        if(d instanceof NPC) {
                                            ((NPC) d).damage(damage);
                                        }
                                        if(d instanceof Player) {
                                            ((Player) d).damage(damage);
                                        }
                                    }
                                }
                            }
                        }, delay * w);
                    }
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft(enchantName(), p,
                                    Configuration.config.getInt("enchant.MAGIXEL.cooldown") + lv)
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Magixel";
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

    public Magixel(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}+3");
        initConfig("circle_amount", "{lv}+3");
        initConfig("entities_around", "{lv}+3");
        initConfig("damage", "{lv}*1.5+2");
    }
}
