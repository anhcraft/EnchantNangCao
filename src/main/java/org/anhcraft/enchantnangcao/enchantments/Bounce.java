package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.HashAlgorithm;
import org.anhcraft.spaciouslib.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.security.NoSuchAlgorithmException;

public class Bounce extends EnchantmentBox implements Listener {
    public static final File f = new File("plugins/EnchantNangCao/data/BounceTraps.yml");
    public static FileConfiguration conf;

    public Bounce() {
        EnchantsAPI.register(this);
        conf = YamlConfiguration.loadConfiguration(f);

        initConfig("height", "{lv}+1");
    }

    @EventHandler
    public void move(PlayerMoveEvent ev){
        Location loc = ev.getTo().getBlock().getLocation();
        loc.subtract(0, 1, 0);
        String id = Bounce.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        if(Bounce.exitsTrap(id)){
            EnchantNangCao.chat.sendPlayer("&6Bạn đã bị dính bẫy nhảy!", ev.getPlayer());
            loc.add(0, 1, 0);
            for(int i = 0; i < Bounce.getTrap(id)+2; i++){
                loc.add(0, 1, 0);
                ev.getPlayer().teleport(loc);
            }
            Bounce.removeTrap(id);
        }
    }

    @EventHandler
    public void breakblock(BlockBreakEvent ev){
        Location loc = ev.getBlock().getLocation();
        String id = Bounce.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        if(Bounce.exitsTrap(id)){
            ev.setCancelled(true);
        }
    }

    public static String newCacheID(World w, int x, int y, int z){
        String c = w.getName()+"#"+x+"#"+y+"#"+z;
        try {
            return StringUtils.hash(HashAlgorithm.MD5,c);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean exitsTrap(String id){
        return conf.isSet(id);
    }

    public static int getTrap(String id){
        return conf.getInt(id);
    }

    public static void removeTrap(String id){
        conf.set(id, null);
    }

    public static void createCache(String id, int damage){
        conf.set(id, damage);
    }

    @EventHandler
    public void trap(PlayerInteractEvent ev){
        if(GameVersion.is1_9Above()) {
            if(ev.getHand() != null  && ev.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        Player p = ev.getPlayer();
        Action a = ev.getAction();
        ItemStack i = p.getInventory().getItemInMainHand();
        if(checkRequirements(i, p) && i.getType().equals(Material.STICK)){
            if(a.equals(Action.LEFT_CLICK_BLOCK)) {
                ev.setCancelled(true);
                Location loc = ev.getClickedBlock().getLocation();
                String id = Bounce.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
                if(!Bounce.exitsTrap(id)){
                    createCache(id, (int) mathConfig("height", i));
                    EnchantNangCao.chat.sendPlayer("&aĐã đặt bẫy bẫy nhảy!", ev.getPlayer());
                    if(0 < i.getAmount()) {
                        i.setAmount(i.getAmount() - 1);
                        p.getInventory().setItemInMainHand(i);
                    } else {
                        p.getInventory().setItemInMainHand(null);
                    }
                } else {
                    EnchantNangCao.chat.sendPlayer("&6Đã có bẫy nhảy ở đây!", ev.getPlayer());
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Bounce";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().equals(Material.STICK);
    }

    @Override
    public int enchantMaxLevel() {
        return 30;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Giúp tạo tạo ra bẫy nhảy",
                "Chuột trái vào khối bạn muốn đặt",
                "&c* Độ cao: &e"+getConfig().getString("height")
        };
    }

    @Override
    public String enchantItemStack() {
        return "Gậy";
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.TRAP;
    }
}
