package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.events.BowArrowHitEvent;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Bomb extends EnchantmentBox implements Listener {
    public Bomb(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*20");
        initConfig("damage", "{lv}+1.5");
    }

    @Override
    public String enchantName() {
        return "Bomb";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().equals(Material.BOW);
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
                "Cung có phù phép này nếu bắn sẽ có một tỉ",
                "lệ nhất định tạo ra vụ nổ khi mũi tên rớt, không",
                "phá khối, có sát thương tuỳ theo cấp độ",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Sát thương: &e"+getConfig().getString("damage")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemBow();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }

    @EventHandler
    public void a(BowArrowHitEvent e){
        if(e.getShooter() instanceof Player) {
            Player p = (Player) e.getShooter();
            ItemStack i = e.getBow();
            if(checkRequirements(i, p)) {
                double damage = mathConfig("damage", i);
                double chance = mathConfig("chance", i) / 100;
                if(Math.random() <= chance) {
                    Location loc = e.getArrow().getLocation();
                    e.getArrow().remove();
                    loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), (float) damage, false, false);
                }
            }
        }
    }
}