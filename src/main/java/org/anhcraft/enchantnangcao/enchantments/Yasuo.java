package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.effect.TornadoEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.annotations.PlayerCleaner;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.scheduler.DelayedTask;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Yasuo extends EnchantmentBox implements Listener {
    @PlayerCleaner
    private static LinkedHashMap<UUID, List<LivingEntity>> entities = new LinkedHashMap<>();

    public static void destroy(){
        AnnotationHandler.unregister(Yasuo.class, null);
    }

    public Yasuo(){
        EnchantsAPI.register(this);

        AnnotationHandler.register(Yasuo.class, null);

        initConfig("high", "{lv}*0.5");
        initConfig("damage", "{lv}*1.5+2");
        initConfig("damage_time", "{lv}*1.2+3");
        initConfig("potion_effect_time", "{lv}*2+3");
    }

    @EventHandler
    public void trigger(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR)
                || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(checkRequirements(p.getInventory().getItemInMainHand(), p)){
                if(!isTimeout("yasuo-2-sk", p, 5)){
                    skill_2(p, p.getInventory().getItemInMainHand());
                    return;
                }
                if(isTimeout("yasuo-1", p, 15)) {
                    LineEffect l = new LineEffect(EnchantNangCao.effect);
                    l.particle = Particle.CLOUD;
                    l.length = 200;
                    l.asynchronous = true;
                    l.particles = 200;
                    l.duration = 3;
                    l.setLocation(p.getEyeLocation());
                    l.start();
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 5f, 4f);

                    if(isTimeout("yasuo-tg-1", p, 5)){
                        setCooldown("yasuo-tg-1", p);
                        setCooldown("yasuo-tg-2", p);
                    } else {
                        if(!isTimeout("yasuo-tg-3", p, 2)){
                            skill_1(p, p.getInventory().getItemInMainHand(), p.getLocation());
                        }
                        else if(!isTimeout("yasuo-tg-2", p, 2)){
                            setCooldown("yasuo-tg-3", p);
                        }
                    }
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft("yasuo-1", p, 15)
                            + " &cgiây để dùng tiếp chiêu Bão Kiếm!", p);
                }
            }
        }
    }

    private void skill_1(Player p, ItemStack item, Location location) {
        setCooldown("yasuo-1", p);
        setCooldown("yasuo-2-sk", p);
        Title.create("&f&l&nBão Kiếm", Title.Type.TITLE).sendPlayer(p);
        for(int i = 0; i < 50; i++){
            int x = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(x == 49){
                        entities.remove(p.getUniqueId());
                        return;
                    }
                    Location loc = location.clone().add(location.getDirection().normalize().multiply(x*1.5));
                    if(x > 25 && !loc.getBlock().getType().equals(Material.AIR)){
                        return;
                    }
                    TornadoEffect t = new TornadoEffect(EnchantNangCao.effect);
                    t.maxTornadoRadius = 2;
                    t.tornadoHeight = 6;
                    t.tornadoParticle = Particle.SWEEP_ATTACK;
                    t.distance = 1;
                    t.showCloud = false;
                    t.asynchronous = true;
                    t.duration = 2;
                    t.setLocation(loc);
                    t.start();
                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_BIG_FALL, 5f, 4f);

                    for(Entity e : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)){
                        if(e instanceof LivingEntity){
                            if(e.getEntityId() == p.getEntityId()){
                                continue;
                            }
                            if(e instanceof Wolf && LifeGuard.data.containsKey(p) && LifeGuard.data.get(p).contains(e)){
                                return;
                            }
                            if(e.hasMetadata("EnchantNoelSkill3")){
                                return;
                            }
                            e.setVelocity(e.getVelocity().setY(mathConfig("high", item)));
                            List<LivingEntity> d = new ArrayList<>();
                            if(entities.containsKey(p.getUniqueId())){
                                d = entities.get(p.getUniqueId());
                            }
                            if(!d.contains(e)){
                                d.add((LivingEntity) e);
                            }
                            entities.put(p.getUniqueId(), d);
                        }
                    }
                }
            }.runTaskLater(EnchantNangCao.plugin,4 * i);
        }
    }

    private void skill_2(Player p, ItemStack item) {
        if(isTimeout("yasuo-2", p, 30)) {
            Title.create("&e&l&nTrăn Trối", Title.Type.TITLE).sendPlayer(p);
            setCooldown("yasuo-2", p);
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) mathConfig("potion_effect_time", item)*20, 1));
            List<LivingEntity> d = new ArrayList<>();
            if(entities.containsKey(p.getUniqueId())){
                d = entities.get(p.getUniqueId());
            }
            Location old = p.getLocation();
            int i = 0;
            for(LivingEntity e : d){
                new DelayedTask(() -> {
                    LineEffect se = new LineEffect(EnchantNangCao.effect);
                    se.setLocation(p.getLocation());
                    se.setTargetLocation(e.getLocation());
                    se.particle = Particle.CRIT_MAGIC;
                    se.duration = 3;
                    se.asynchronous = true;
                    se.start();
                    p.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.2));
                }, i).run();
                i++;
                if(i == 10){
                    break;
                }
            }
            new DelayedTask(() -> {
                p.setVelocity(old.toVector().subtract(p.getLocation().toVector()));
            }, i+1).run();
        } else {
            EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                    timeLeft("yasuo-2", p, 30)
                    + " &cgiây để dùng tiếp chiêu Trăn Trối!", p);
        }
    }

    @Override
    public String enchantName() {
        return "Yasuo";
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
        return !version.toString().contains("1_8");
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Yasuo... Kẻ Bất Dung Thứ",
                "Chuột phải 3 lần liên tục để thực hiện chiêu 1",
                "+ Chiêu 1 - Bão Kiếm (thời gian chờ 10s): tạo ra",
                "lốc xoáy hướng thẳng về trước, hất tung mọi thực",
                "thể lên cao, trong vòng 5s, nếu ấn chuột phải lần",
                " nữa thi dùng thêm chiêu 2",
                "+ Chiêu 2 - Trăn Trối (thời gian chờ 30s): liên tục",
                "di chuyển tới thực thể bị hất gần nhất trong 5s,",
                "gây sát thương liên tục trong một khoảng thời gian,",
                "nhận thuốc hồi phục trong 5s",
                "&c* Độ cao bị hất tung: &e"+getConfig().getString("high"),
                "&c* Sát thương: &e"+getConfig().getString("damage"),
                "&c* Thời gian gây sát thương: &e"+getConfig().getString("damage_time"),
                "&c* Thời gian của thuốc (giây): &e"+getConfig().getString("potion_effect_time")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAllItems();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }
}