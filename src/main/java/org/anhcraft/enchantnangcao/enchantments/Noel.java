package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Noel extends EnchantmentBox implements Listener {
    public static LinkedHashMap<OfflinePlayer, Integer> data = new LinkedHashMap<>();
    public static LinkedHashMap<OfflinePlayer, List<ArmorStand>>
            gift = new LinkedHashMap<>();

    public static void destroy(){
        for(OfflinePlayer p : Noel.gift.keySet()) {
            for(ArmorStand a : Noel.gift.get(p)){
                if(!a.isDead()){
                    a.remove();
                }
            }
        }
    }

    public Noel(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*2+3");
        initConfig("gifts_amount", "{lv}*0.7+5");
    }

    @Override
    public String enchantName() {
        return "Noel";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack itemStack) {
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
    public String[] enchantDescription() {
        return new String[]{
                "Chuột phải kích hoạt lần lượt 3 chiêu",
                "Chiêu 1 (Giáng sinh may mắn): cooldown là 20-30s bắn ra từ một lượng",
                "món quà ra xung quanh, khi bạn di chuyển vào món quà sẽ tự động nhận,",
                "quà gồm: hiệu ứng cấp I tùy loại (10-15s), hồi máu 1-5 tim, hồi đầy ",
                "thanh thức ăn. Mọi loại thực thể đều có thể được nhận. Hộp quà sẽ biến",
                "mất sau 5-10s.",
                "Chiêu 2 (Giáng sinh băng giá): Mọi thực thể ở xung quanh sẽ bị hiệu ứng ",
                "chậm 5-10s. Nếu có từ 3 thực thể trở lên, một trong số đó sẽ bị thêm hiệu",
                "ứng độc và mất 10% từ 20% máu hiện có cho người dùng chiêu.",
                "Chiêu 3 (Giáng sinh chết chóc): Sinh ra sói. Sói này có 50-250 máu và được",
                "hiệu ứng vĩnh viễn như chạy nhanh, hồi máu, chống lửa.... Sói này khi đánh",
                "thì sẽ bị hiệu ứng chậm 10s, độc 5s. Sói sẽ sống trong 15s và khi chết sẽ",
                "tạo ra vụ nổ (không phá block)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Số lượng quà: &e"+getConfig().getString("gifts_amount")
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

    @EventHandler
    public void join(PlayerJoinEvent e){
        if(!data.containsKey(e.getPlayer())){
            data.put(e.getPlayer(), 1);
        }
    }

    @EventHandler
    public void iteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }
        if(GameVersion.is1_9Above()) {
            if(e.getHand() != null  && e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if(!checkRequirements(item, p)){
            return;
        }
        int lv = new EnchantsAPI(item).getEnchantLevel(this);
        if(!data.containsKey(p)){
            data.put(p, 1);
        }
        int i = data.get(p);
        if(3 < i){
            data.put(p, 1);
            i = 1;
        }
        switch(i){
            case 1:
                if(isTimeout("Noel-1", p,mathConfig("cooldown", item))){
                    data.put(p, 2);
                    setCooldown("Noel-1", p);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            data.put(p, 1);
                        }
                    }.runTaskLater(EnchantNangCao.plugin, 60);
                    skill_1(p, lv, item);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn cần chờ thêm "+
                            timeLeft("Noel-1", p,
                                    (int) mathConfig("cooldown", item))+" giây!", p);
                }
                break;
            case 2:
                data.put(p, 3);
                if(isTimeout("Noel-2", p, 3)){
                    setCooldown("Noel-1", p);
                    setCooldown("Noel-2", p);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            data.put(p, 1);
                        }
                    }.runTaskLater(EnchantNangCao.plugin, 60);
                    skill_2(p, lv);
                } else {
                    data.put(p, 1);
                    if(!isTimeout("Noel-1", p,mathConfig("cooldown", item))){
                        EnchantNangCao.chat.sendPlayer("&cBạn cần chờ thêm "+
                                timeLeft("Noel-1", p, (int) mathConfig("cooldown", item))+" giây!", p);
                    }
                }
                break;
            case 3:
                data.put(p, 1);
                if(isTimeout("Noel-3", p, 3)){
                    setCooldown("Noel-1", p);
                    setCooldown("Noel-3", p);
                    skill_3(p);
                } else {
                    if(!isTimeout("Noel-1", p, mathConfig("cooldown", item))){
                        EnchantNangCao.chat.sendPlayer("&cBạn cần chờ thêm "+
                                timeLeft("Noel-1", p,
                                        (int) mathConfig("cooldown", item))
                                +" giây!", p);
                    }
                }
                break;
        }
    }

    private static class RandomEnum<E extends Enum> {
        private static final Random RND = new Random();
        private final E[] values;
        RandomEnum(Class<E> token) {
            values = token.getEnumConstants();
        }
        E random() {
            return values[RND.nextInt(values.length)];
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e){
        try {
            for(OfflinePlayer p : gift.keySet()) {
                List<ArmorStand> v = gift.get(p);
                for(ArmorStand a : gift.get(p)) {
                    if(!a.getLocation().getWorld().equals(e.getTo().getWorld())) {
                        return;
                    }
                    if(a.getLocation().distance(e.getPlayer().getLocation()) < 2) {
                        a.remove();
                        v.remove(a);
                        GiftType g = new RandomEnum<>(GiftType.class).random();
                        switch(g) {
                            case HEAL:
                                int heal = ThreadLocalRandom.current().nextInt(1,
                                        5);
                                EnchantNangCao.chat.sendPlayer("&aBạn nhận được &f" + heal + "&a tim!",
                                        e.getPlayer());
                                double q = e.getPlayer().getHealth() + heal;
                                if(e.getPlayer().getMaxHealth() < q) {
                                    q = e.getPlayer().getMaxHealth();
                                }
                                e.getPlayer().setHealth(q);
                                return;
                            case EFFECT:
                                PotionEffectType type = PotionEffectType.values()[ThreadLocalRandom.current()
                                        .nextInt(0, PotionEffectType.values().length)];
                                int seconds = ThreadLocalRandom.current().nextInt(10, 16);
                                EnchantNangCao.chat.sendPlayer("&aBạn nhận được &e" + seconds +
                                        " giây&a hiệu ứng &f" + type.getName() + " I&a !", e.getPlayer());
                                e.getPlayer().addPotionEffect(new PotionEffect(type,
                                        seconds * 20,
                                        1));
                                return;
                            case FULL_FOOD_LV:
                                e.getPlayer().setFoodLevel(20);
                                EnchantNangCao.chat.sendPlayer("&aBạn nhận được &eHồi đầy thanh thức ăn &a !",
                                        e.getPlayer());
                        }
                    }
                }
                gift.put(p, v);
            }
        } catch(Exception ignored){

        }
    }

    public enum GiftType {
        HEAL,
        FULL_FOOD_LV,
        EFFECT,
    }

    @EventHandler
    public void dame(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Wolf && e.getEntity() instanceof LivingEntity){
            if(e.getDamager().hasMetadata("EnchantNoelSkill3")){
                e.setDamage(0);
                if(((Wolf) e.getDamager()).getOwner().getName()
                        .equals(e.getEntity().getName())){
                    return;
                }
                LivingEntity d = (LivingEntity) e.getEntity();
                if(!d.hasMetadata("shopkeeper") && !d.hasMetadata("NPC")){
                    d.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                                200, 1));
                    d.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                            100, 1));
                }
            }
        }
    }

    @EventHandler
    public void death(EntityDeathEvent e){
        if(e.getEntity() instanceof Wolf && e.getEntity().hasMetadata("EnchantNoelSkill3")) {
            Location loc = e.getEntity().getLocation();
            e.getEntity().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 5,
                    false, false);
        }
    }

    private void skill_3(Player p) {
        Title.create("&5&lMerry Christmas!", Title.Type.TITLE).sendPlayer(p);
        Title.create("&2&l&nGiáng sinh chết chóc [3]", Title.Type.SUBTITLE).sendPlayer(p);
        double health = ThreadLocalRandom.current().nextDouble(50, 251);
        Wolf w = p.getWorld().spawn(p.getLocation(), Wolf.class);
        w.setMaxHealth(health);
        w.setHealth(health);
        w.setAngry(true);
        w.setOwner(p);
        w.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1));
        w.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1));
        w.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, 3));
        w.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000, 3));
        w.setMetadata("EnchantNoelSkill3", new FixedMetadataValue(
                EnchantNangCao.plugin, p.getName()));
        w.setCustomNameVisible(true);
        w.setCustomName(p.getName()+"'s Noel Wolf");
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!w.isDead()){
                    w.remove();
                }
            }
        }.runTaskLater(EnchantNangCao.plugin, 20*
                ThreadLocalRandom.current().nextInt(20, 30));
    }

    private void skill_2(Player p, int lv) {
        Title.create("&5&lMerry Christmas!", Title.Type.TITLE).sendPlayer(p);
        Title.create("&b&l&nGiáng sinh băng giá [2]", Title.Type.SUBTITLE).sendPlayer(p);
        List<LivingEntity> q = new ArrayList<>();
        for(Entity e : p.getNearbyEntities(lv+2, lv+2, lv+2)){
            if(e instanceof LivingEntity){
                q.add((LivingEntity) e);
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                        ThreadLocalRandom.current().nextInt(5, 11),
                        ThreadLocalRandom.current().nextInt(1, 4)));
            }
        }
        if(2 < q.size()){
            LivingEntity le = q.get(new Random().nextInt(q.size()));
            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                    ThreadLocalRandom.current().nextInt(5, 11),
                    ThreadLocalRandom.current().nextInt(1, 4)));
            double gethealth = le.getHealth()/100*ThreadLocalRandom
                    .current().nextInt(10, 21);
            double newhealth1 = le.getHealth()-gethealth;
            double newhealth2 = p.getHealth()+gethealth;
            if(newhealth1 < 0){
                newhealth1 = 0;
            }
            if(p.getMaxHealth() < newhealth2){
                newhealth2 = p.getMaxHealth();
            }
            le.setHealth(newhealth1);
            p.setHealth(newhealth2);
        }
    }

    private void skill_1(Player p, int lv, ItemStack item) {
        Title.create( "&5&lMerry Christmas!", Title.Type.TITLE).sendPlayer(p);
        Title.create("&6&l&nGiáng sinh may mắn [1]", Title.Type.SUBTITLE).sendPlayer(p);
        int i = 1;
        int m = (int) mathConfig("gifts_amount", item);
        List<ArmorStand> gifts = new ArrayList<>();
        Location location = p.getEyeLocation();
        for (int degree = 0; degree < 360; degree++) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * ThreadLocalRandom.current().nextInt(2, 5);
            double z = Math.sin(radians) * ThreadLocalRandom.current().nextInt(2, 5);
            location.add(x,degree/180,z);
            if(Math.round(360/m*i) == degree){
                ArmorStand a = location.getWorld().spawn(location, ArmorStand.class);
                a.setVisible(false);
                a.setCustomNameVisible(true);
                a.setSmall(true);
                a.setVelocity(location.add(0, ThreadLocalRandom.current().nextInt(3, 6),
                        0).getDirection());
                a.setGravity(true);
                a.setCustomName(ChatColor.GOLD+"Hộp quà");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!a.isDead()){
                            a.remove();
                            gifts.remove(a);
                            gift.put(p, gifts);
                        }
                    }
                }.runTaskLater(EnchantNangCao.plugin, 80);
                gifts.add(a);
                i++;
            }
            location.getWorld().spawnParticle(Particle.SNOWBALL, location, 1);
            location.subtract(x,degree/180,z);
        }
        gift.put(p, gifts);
    }
}
