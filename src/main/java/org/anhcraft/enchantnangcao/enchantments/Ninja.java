package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.annotations.PlayerCleaner;
import org.anhcraft.spaciouslib.events.ArmorEquipEvent;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ninja extends EnchantmentBox implements Listener {
    @PlayerCleaner
    private static List<UUID> players = new ArrayList<>();

    public static void destroy(){
        AnnotationHandler.unregister(Ninja.class, null);
    }

    public Ninja() {
        EnchantsAPI.register(this);
        AnnotationHandler.register(Ninja.class, null);
    }

    @EventHandler
    public void equip(ArmorEquipEvent e) {
        Player p = e.getPlayer();
        if(players.contains(p.getUniqueId())) {
            if(checkRequirements(e.getOldArmor(), p) && !checkRequirements(e.getNewArmor(), p)) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.removePotionEffect(PotionEffectType.REGENERATION);
                players.remove(p.getUniqueId());
            }
        } else {
            if(checkRequirements(e.getNewArmor(), p) && !checkRequirements(e.getOldArmor(), p)) {
                int lv = new EnchantsAPI(e.getNewArmor()).getEnchantLevel(this)-1;
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, lv));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, lv));
                players.add(p.getUniqueId());
            }
        }
    }

    @Override
    public String enchantName() {
        return "Ninja";
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
                "Giúp bạn chạy nhanh và được hồi máu",
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