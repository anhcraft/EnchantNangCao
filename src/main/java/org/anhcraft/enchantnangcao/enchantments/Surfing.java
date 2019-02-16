package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.LineEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class Surfing extends EnchantmentBox implements Listener {
    public Surfing(){
        EnchantsAPI.register(this);

        initConfig("distance", "{lv}*1.5+3");
        initConfig("cooldown", "{lv}*1.2+1");
    }

    @EventHandler
    public void surf(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)){
            return;
        }
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        Player p = e.getPlayer();
        if(checkRequirements(p.getInventory().getItemInMainHand(), p)){
            ItemStack i = p.getInventory().getItemInMainHand();
            if(isTimeout(enchantName(), p, mathConfig("cooldown", i)))
                setCooldown(enchantName(), p);{
                Location loc = p.getLocation();
                Vector vec = loc.getDirection()
                        .normalize().multiply(mathConfig("distance", i)).setY(0);
                LineEffect eff = new LineEffect(EnchantNangCao.effect);
                eff.particle = Particle.CLOUD;
                eff.asynchronous = true;
                eff.duration = 3;
                eff.setLocation(loc);
                eff.setTargetLocation(loc.add(vec));
                eff.start();
                p.setVelocity(vec);
                List<Player> a = p.getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
                a.remove(p);
                for(Player op : a){
                    op.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Surfing";
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
    protected String[] enchantDescription() {
        return new String[]{
                "Chuột phải để lướt về trước, làm mù kẻ thù",
                "&c* Quãng đường lướt: &e"+getConfig().getString("distance"),
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
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
}
