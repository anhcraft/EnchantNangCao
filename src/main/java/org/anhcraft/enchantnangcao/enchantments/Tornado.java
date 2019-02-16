package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.TornadoEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;

public class Tornado extends EnchantmentBox implements Listener {
    public static LinkedHashMap<ArmorStand, Player> data = new LinkedHashMap<>();

    public Tornado(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+10");
        initConfig("height", "{lv}*1.3+5");
    }

    @EventHandler
    public void tornadoExecute(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }
        if(e.getHand() != null && e.getHand().equals(EquipmentSlot.OFF_HAND)){
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if(!checkRequirements(item, p)){
            return;
        }
        if(data.containsValue(p)){
            return;
        }
        if(!isTimeout("Tornado", p,
                (int) mathConfig("cooldown", item))){
            EnchantNangCao.chat.sendPlayer("&aBạn còn &e"+timeLeft("Tornado", p,
                    (int) mathConfig("cooldown", item))
                    +" &agiây nữa để dùng tiếp!", p);
            return;
        }
        Title.create("&f&lLỐC XOÁY THẦN GIÓ!", Title.Type.TITLE).sendPlayer(p);
        setCooldown("Tornado", p);

        int i = 3;
        while(i < 20) {
            int a = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = p.getLocation().clone().add(p.getLocation().clone().getDirection().normalize().multiply(a*1.5));
                    TornadoEffect t = new TornadoEffect(EnchantNangCao.effect);
                    t.maxTornadoRadius = 4;
                    t.tornadoHeight = 6;
                    t.tornadoParticle = Particle.SWEEP_ATTACK;
                    t.distance = 1;
                    t.showCloud = false;
                    t.asynchronous = true;
                    t.duration = 2;
                    t.setLocation(loc);
                    t.start();
                    int r = 8;
                    for(Entity xx : loc.getWorld().getNearbyEntities(loc, r, r, r)) {
                        if(!xx.equals(p) && !xx.hasMetadata("NPC")
                                && !xx.hasMetadata("shopkeeper")) {
                            xx.setVelocity(xx.getVelocity().setY(mathConfig("height", item)));
                        }
                    }
                }
            }.runTaskLater(EnchantNangCao.plugin, 10*i);
            i++;
        }
    }

    @Override
    public String enchantName() {
        return "Tornado";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }

    @Override
    public int enchantMaxLevel() {
        return 3;
    }

    @Override
    public Boolean enchantVersion(GameVersion gVersion) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Bắn ra lốc xoáy hất tung mọi thực thể gần đó lên cao (Chuột phải)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Độ cao hất tung: &e"+getConfig().getString("height")
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
