package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Mjolnir extends EnchantmentBox implements Listener {
    public Mjolnir(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*5");
        initConfig("power", "{lv}*1.2+3");
    }

    @EventHandler
    public void breakLog(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity)){
            return;
        }
        Player p = (Player) e.getDamager();
        LivingEntity le = (LivingEntity) e.getEntity();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(!checkRequirements(item, p)) {
            return;
        }
        if(!(Math.random() < mathConfig("chance", item)/100)) {
            return;
        }
        le.getWorld().strikeLightning(le.getLocation());
        if(Math.random() <= 0.2){
            le.getWorld().createExplosion(le.getLocation().getX(),
                    le.getLocation().getY(), le.getLocation().getZ(),
                    (float) mathConfig("power", item), false, false);
        }
    }

    private static boolean isMaterialCanUse(Material mts){
        boolean r = false;
        List<Material> mt = new ArrayList<>();
        mt.add(Material.DIAMOND_AXE);
        mt.add(Material.IRON_AXE);
        mt.add(Material.STONE_AXE);
        if(GameVersion.is1_13Above()){
            mt.add(Material.GOLDEN_AXE);
            mt.add(Material.WOODEN_AXE);
        } else {
            mt.add(Material.valueOf("GOLD_AXE"));
            mt.add(Material.valueOf("WOOD_AXE"));
        }
        for(Material m : mt){
            if(m.equals(mts)){
                r = true;
                break;
            }
        }
        return r;
    }

    @Override
    public String enchantName() {
        return "Mjolnir";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack itemStack) {
        return isMaterialCanUse(itemStack.getType());
    }

    @Override
    public int enchantMaxLevel() {
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion gVersion) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ, giúp triệu hồi sấm sét lên thực thể bạn đang đánh",
                "Có 20% tạo ra vụ nổ",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Sát thương vụ nổ: &e"+getConfig().getString("power"),
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAxe();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
