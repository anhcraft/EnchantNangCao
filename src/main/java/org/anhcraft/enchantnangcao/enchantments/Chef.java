package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.TimedSet;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Chef extends EnchantmentBox implements Listener {
    private static TimedSet<Integer> entities = new TimedSet<>();

    public Chef(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*4.8");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event){
        Player p = event.getPlayer();
        if(checkRequirements(p.getInventory().getItemInMainHand(), p)){
            double chance = mathConfig("chance", p.getInventory().getItemInMainHand())/100;
            if(Math.random() <= chance){
                for(ItemStack drop : event.getBlock().getDrops()) {
                    if(drop.getType().toString().equals("POTATO_ITEM")
                            || drop.getType().toString().equals("POTATO")){
                        drop.setType(Material.BAKED_POTATO);
                        p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2f, 3f);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void damage(EntityDamageByEntityEvent ev){
        if(ev.getDamager() instanceof Player &&
                (ev.getEntity() instanceof Animals || ev.getEntity() instanceof Monster)) {
            LivingEntity le = (LivingEntity) ev.getEntity();
            if(le.getHealth() - ev.getDamage() <= 0) {
                Player p = (Player) ev.getDamager();
                if(checkRequirements(p.getInventory().getItemInMainHand(), p)) {
                    double chance = mathConfig("chance", p.getInventory().getItemInMainHand()) / 100;
                    if(Math.random() <= chance) {
                        entities.add(ev.getEntity().getEntityId(), 5);
                        p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2f, 3f);
                    }
                }
            }
        }
    }

    @EventHandler
    public void death(EntityDeathEvent event){
        if(entities.contains(event.getEntity().getEntityId())){
            entities.remove(event.getEntity().getEntityId());
            for(ItemStack drop : event.getDrops()) {
                String mt = drop.getType().toString();
                switch(mt) {
                    case "PORKCHOP":
                        drop.setType(Material.COOKED_PORKCHOP);
                        break;
                    case "PORK":
                        drop.setType(Material.valueOf("GRILLED_PORK"));
                        break;
                    case "RAW_BEEF":
                    case "BEEF":
                        drop.setType(Material.COOKED_BEEF);
                        break;
                    case "RAW_CHICKEN":
                    case "CHICKEN":
                        drop.setType(Material.COOKED_CHICKEN);
                        break;
                    case "COD":
                        drop.setType(Material.COOKED_COD);
                        break;
                    case "SALMON":
                        drop.setType(Material.COOKED_SALMON);
                        break;
                    case "RAW_FISH":
                        drop.setType(Material.valueOf("COOKED_FISH"));
                        break;
                    case "MUTTON":
                        drop.setType(Material.COOKED_MUTTON);
                        break;
                    case "RABBIT":
                        drop.setType(Material.COOKED_RABBIT);
                        break;
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Chef";
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
                "Có một tỉ lệ giúp tự động nấu thức ăn",
                "Xem thêm: https://minecraft.gamepedia.com/Smelting",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance")
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
