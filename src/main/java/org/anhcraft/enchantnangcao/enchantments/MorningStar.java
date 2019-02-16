package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.entity.ArmorStand;
import org.anhcraft.spaciouslib.protocol.EntityDestroy;
import org.anhcraft.spaciouslib.protocol.Particle;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.scheduler.TimerTask;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.ReflectionUtils;
import org.anhcraft.spaciouslib.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class MorningStar extends EnchantmentBox implements Listener {
    public MorningStar(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+2");
        initConfig("power", "{lv}*1.8+2.5");
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        ItemStack i = p.getInventory().getItemInMainHand();
        Material stm = GameVersion.is1_13Above() ? Material.OAK_BUTTON : Material.valueOf("WOOD_BUTTON");
        if(checkRequirements(i, p) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                Title.create("&b&lMorning&d&lStar", Title.Type.TITLE).sendPlayer(p);
                setCooldown(enchantName(), p);
                Location loc = p.getLocation();
                double pw = mathConfig("power", i);
                double length = 5;
                double offset = 0.1;
                int ncr_rad = 10;
                int ma_rad = 360;
                int speed = 4;

                LinkedHashMap<ArmorStand, Vector> parts = new LinkedHashMap<>();
                Vector v = loc.getDirection().normalize();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(double r = 0; r < length; r+= offset){
                            Vector vec = v.clone().multiply(r);
                            parts.put(new ArmorStand(loc.clone().add(vec)).setMarker(true).setGravity(false).setVisible(false).setHelmet(new ItemStack(stm)).setSmall(true).setHeadPose(new EulerAngle(v.getX(), v.getY(), v.getZ())).buildPackets(), vec);
                        }
                        Vector vec = v.clone().multiply(length-offset/2);
                        parts.put(new ArmorStand(loc.clone().add(vec)).setMarker(true).setGravity(false).setVisible(false).setHelmet(new ItemStack(Material.IRON_BLOCK)).setSmall(true).setHeadPose(new EulerAngle(v.getX(), v.getY(), v.getZ())).buildPackets(), vec);
                        Set<UUID> viewers = new HashSet<>();
                        for(Entity ent : p.getNearbyEntities(length,length,length)){
                            if(ent instanceof Player){
                                viewers.add(ent.getUniqueId());
                            }
                        }
                        viewers.add(p.getUniqueId());
                        for(ArmorStand part : parts.keySet()){
                            part.setViewers(viewers);
                        }
                    }
                }.runTaskLaterAsynchronously(EnchantNangCao.plugin, 0);
                for(int r = 0; r < ma_rad; r+= ncr_rad){
                    double cr = r;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<LivingEntity> n = new ArrayList<>();
                            int g = 0;
                            try {
                                for(Map.Entry<ArmorStand, Vector> part : parts.entrySet()) {
                                    Location location = loc.clone()
                                            .add(VectorUtils.rotateAroundAxisY(part.getValue(), ncr_rad));
                                    part.getKey().teleport(location);
                                    if(g == parts.size() - 1) {
                                        Particle.create(Particle.Type.FIREWORKS_SPARK, location, 1).sendNearby(location, 50);
                                        for(Entity ent : location.getWorld().getNearbyEntities(
                                                location, 3, 3, 3)) {
                                            if(ent instanceof LivingEntity && !ent.equals(p)) {
                                                if(!ent.hasMetadata("shopkeeper")
                                                        && !ent.hasMetadata("NPC")) {
                                                    n.add((LivingEntity) ent);
                                                }
                                            }
                                        }
                                    }
                                    g++;
                                }
                                if(n.size() > 0) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            for(Entity ent : n) {
                                                ((LivingEntity) ent).damage(pw, p);
                                            }
                                        }
                                    }.runTaskLater(EnchantNangCao.plugin, 0);
                                }
                                if(cr == ma_rad - ncr_rad) {
                                    for(ArmorStand part : parts.keySet()) {
                                        // bugs???
                                        int id = (int) ReflectionUtils.getField("id",
                                                ArmorStand.class, part);
                                        new TimerTask(() -> {
                                            EntityDestroy.create(id).sendPlayer(p);
                                        }, 0, 1, 5).run();
                                        part.remove();
                                    }
                                }
                            } catch(Exception ignored){}
                        }
                    }.runTaskLaterAsynchronously(EnchantNangCao.plugin, r/speed);
                }
            } else {
                EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                        timeLeft(enchantName(), p, mathConfig("cooldown", i))
                        + " &cgiây để dùng tiếp!", p);
            }
        }
    }

    @Override
    public String enchantName() {
        return "MorningStar";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_AXE");
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
                "Chuột trái để tạo ra cây chùy (MorningStar) của Rem",
                "Giúp gây sát thương lên kẻ thù xung quanh bạn",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Sức mạnh: &e"+getConfig().getString("power"),
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAxe();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }
}
