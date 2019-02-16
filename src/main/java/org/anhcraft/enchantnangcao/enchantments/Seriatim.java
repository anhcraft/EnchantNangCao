package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.annotations.PlayerCleaner;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Seriatim extends EnchantmentBox implements Listener {
    @Override
    public String enchantName() {
        return "Seriatim";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
    }

    @PlayerCleaner
    private static LinkedHashMap<UUID, Double> dameStorage = new LinkedHashMap<>();
    @PlayerCleaner
    private static LinkedHashMap<UUID, Player> players = new LinkedHashMap<>();

    public static void destroy(){
        AnnotationHandler.unregister(Seriatim.class, null);
    }

    public Seriatim(){
        EnchantsAPI.register(this);
        initConfig("chance", "{lv}*10");
        initConfig("health", "{lv}*2+5");
        AnnotationHandler.register(Seriatim.class, null);
    }

    @EventHandler
    public void dame(EntityDamageByEntityEvent event){
        Entity e1 = event.getDamager();
        Entity e2 = event.getEntity();
        if(e1 instanceof Player && e2 instanceof Player){
            Player p1 = (Player) e1;
            Player p2 = (Player) e2;
            for(ItemStack item : p2.getInventory().getArmorContents()) {
                if(checkRequirements(item, p2) &&
                        MaterialUtils.getArmorTypes().contains(item.getType())
                                && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    if(Math.random() < mathConfig("chance",
                            item)/100){
                        double a = mathConfig("health",
                                item);
                        if(event.getDamage() < a){
                            a = event.getDamage();
                        }
                        event.setDamage(a);
                        players.put(p2.getUniqueId(), p1);
                        dameStorage.put(p2.getUniqueId(), a);
                        EnchantNangCao.chat.sendPlayer("&6Bạn đã bị đối thủ hút sát thương. Hãy cẩn thận!", p1);
                        EnchantNangCao.chat.sendPlayer("&aSát thương đã hút là: " + a, p2);
                        return;
                    }
                }
            }

            for(ItemStack item : p1.getInventory().getArmorContents()) {
                if(checkRequirements(item, p1) &&
                        MaterialUtils.getArmorTypes().contains(item.getType())
                        && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                 && players.containsKey(p1.getUniqueId())
                                && players.get(p1.getUniqueId()).equals(p2)
                                && dameStorage.containsKey(p1.getUniqueId())){
                            event.setDamage(event.getDamage() + dameStorage.get(p1.getUniqueId()));
                            EnchantNangCao.chat.sendPlayer(
                                    "&aĐã cộng dồn "+dameStorage.get(p1.getUniqueId())+" sát thương!", p1);
                            players.remove(p1.getUniqueId());
                            dameStorage.remove(p1.getUniqueId());
                }
            }
        }
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
                "Có tỉ lệ hút sát thương khi bị ai đó đánh",
                "và gộp lại sát thương đó khi bạn đánh lại họ",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Số sát thương sẽ lấy: &e"+getConfig().getString("health")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemArmors();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
