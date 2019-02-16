package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class Smelter extends EnchantmentBox implements Listener {
    public Smelter(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*4.2");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event){
        Player p = event.getPlayer();
        if(checkRequirements(p.getInventory().getItemInMainHand(), p)){
            double chance = mathConfig("chance", p.getInventory().getItemInMainHand())/100;
            if(Math.random() <= chance){
                boolean has = false;
                Location loc = event.getBlock().getLocation().add(0, 0.5, 0);
                for(Iterator<ItemStack> it = event.getBlock().getDrops().iterator(); it.hasNext(); ) {
                    ItemStack drop = it.next();
                    switch(drop.getType()) {
                        case IRON_ORE:
                            event.getBlock().getWorld().dropItemNaturally(loc,
                                    new ItemStack(Material.IRON_INGOT, drop.getAmount()));
                            it.remove();
                            break;
                        case GOLD_ORE:
                            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT, drop.getAmount()));
                            break;
                        case SAND:
                            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.GLASS, drop.getAmount()));
                            break;
                        case COBBLESTONE:
                            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.STONE, drop.getAmount()));
                            break;
                        case CLAY:
                            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.BRICK, drop.getAmount()));
                            break;
                        case NETHERRACK:
                            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(
                                    GameVersion.is1_13Above() ? Material.NETHER_BRICK : Material
                                            .valueOf("NETHER_BRICK_ITEM"), drop.getAmount()));
                            break;
                        default:
                            return;
                    }
                    has = true;
                }
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
                if(has){
                    p.playSound(p.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 2f, 3f);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Smelter";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_PICKAXE") || i.getType().toString().endsWith("_SPADE");
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
                "Có một tỉ lệ giúp tự động nung khối đã đào",
                "Xem thêm: https://minecraft.gamepedia.com/Smelting",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemPickaxe() + ", " + getDefaultItemShovel();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
