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

public class Agony extends EnchantmentBox implements Listener {
    public static final File f = new File("plugins/EnchantNangCao/data/AgonyTraps.yml");
    public static FileConfiguration conf;

    public Agony() {
        EnchantsAPI.register(this);
        conf = YamlConfiguration.loadConfiguration(f);

        initConfig("power", "{lv}+1");
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


    @EventHandler
    public void move(PlayerMoveEvent ev){
        Location loc = ev.getTo().getBlock().getLocation();
        loc.subtract(0, 1, 0);
        String id = Agony.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        if(Agony.exitsTrap(id)){
            EnchantNangCao.chat.sendPlayer("&6Bạn đã bị dính bẫy nổ!", ev.getPlayer());
            loc.add(0, 2, 0);
            loc.getWorld().createExplosion((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(),
                    Agony.getTrap(id) / 5, false, false);
            Agony.removeTrap(id);
        }
    }

    @EventHandler
    public void breakblock(BlockBreakEvent ev){
        Location loc = ev.getBlock().getLocation();
        String id = Agony.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        if(Agony.exitsTrap(id)){
            ev.setCancelled(true);
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
                String id = Agony.newCacheID(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
                if(!Agony.exitsTrap(id)){
                    createCache(id, (int) mathConfig("power", i));
                    EnchantNangCao.chat.sendPlayer("&aĐã đặt bẫy bẫy nổ!", ev.getPlayer());
                    if(0 < i.getAmount()) {
                        i.setAmount(i.getAmount() - 1);
                        p.getInventory().setItemInMainHand(i);
                    } else {
                        p.getInventory().setItemInMainHand(null);
                    }
                } else {
                    EnchantNangCao.chat.sendPlayer("&6Đã có bẫy nổ ở đây!", ev.getPlayer());
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Agony";
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
                "Giúp tạo tạo ra bẫy nổ",
                "Chuột trái vào khối bạn muốn đặt",
                "&c* Sức mạnh vụ nổ: &e"+getConfig().getString("power")
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
