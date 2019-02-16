package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.BlockUtils;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Explosive extends EnchantmentBox implements Listener {
    public Explosive(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*10");
        initConfig("radius", "{lv}/10+2");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void breakblock(BlockBreakEvent event){
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        if(checkRequirements(item, p)) {
            int radius = (int) mathConfig("radius", item);
            double chance = mathConfig("chance", item) / 100;
            if(Math.random() <= chance) {
                for(Block b : BlockUtils.getNearbyBlocks(block.getLocation(), radius, radius, radius)) {
                    if(b.getType().equals(block.getType())
                            && b.getData() == block.getData()){
                        b.breakNaturally(item);
                    }
                }
                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 4f, 5f);
            }
        }
    }

    @Override
    public String enchantName() {
        return "Explosive";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_PICKAXE");
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
                "Có một tỉ lệ để phá huỷ cùng lúc các block",
                "cùng loại trong bán kính nhất định",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Bán kính: &e"+getConfig().getString("radius")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemPickaxe();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
