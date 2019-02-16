package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.annotations.PlayerCleaner;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.TimedSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Freeze extends EnchantmentBox implements Listener {
    @PlayerCleaner
    private static TimedSet<UUID> data = new TimedSet<>();

    public static void destroy(){
        AnnotationHandler.unregister(Freeze.class, null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev){
        if((ev.getFrom().getBlockX() != ev.getTo().getBlockX())
                || (ev.getFrom().getBlockZ() != ev.getTo().getBlockZ())) {
            if(data.contains(ev.getPlayer().getUniqueId())) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayer(EntityDamageByEntityEvent ev) {
        if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player) {
            Player d = (Player) ev.getDamager();
            ItemStack i = d.getInventory().getItemInMainHand();
            if(checkRequirements(i, d) && ev.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                Player e = (Player) ev.getEntity();
                if(isTimeout(enchantName()+"_cooldown", d, mathConfig("cooldown", i))) {
                    setCooldown(enchantName()+"_cooldown", d);
                    Title.create("&b&lĐÓNG BĂNG!!!", Title.Type.TITLE).sendPlayer(d);
                    double time = mathConfig("freezing_time", i);
                    if(!data.contains(e.getUniqueId())) {
                        EnchantNangCao.chat.sendPlayer(
                                "&bBạn đã bị " + d.getName() + " đóng băng trong " + time + " giây", e);
                        data.add(e.getUniqueId(), (long) time);
                    }
                }
            }
        }
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAllItems();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }

    @Override
    public String enchantName() {
        return "Freeze";
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
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Giúp bạn đóng băng người nào đó khi đánh họ",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Thời gian đóng băng: &e"+getConfig().getString("freezing_time")
        };
    }

    public Freeze(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "{lv}*1.5+4");
        initConfig("freezing_time", "{lv}*1.5+1");
        AnnotationHandler.register(Freeze.class, null);
    }
}
