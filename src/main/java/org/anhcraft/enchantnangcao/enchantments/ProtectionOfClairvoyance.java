package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.annotations.PacketHandler;
import org.anhcraft.spaciouslib.internal.listeners.PacketListener;
import org.anhcraft.spaciouslib.inventory.InventoryManager;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.inventory.SkullManager;
import org.anhcraft.spaciouslib.protocol.Camera;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.anhcraft.spaciouslib.utils.MathUtils;
import org.bukkit.Material;
import org.bukkit.SkullType;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ProtectionOfClairvoyance extends EnchantmentBox implements Listener {
    private static Set<Integer> players = new HashSet<>();

    public static void destroy(){
        AnnotationHandler.unregister(ProtectionOfClairvoyance.class, null);
    }

    @PacketHandler
    public static void packet(PacketListener.Handler handler){
        if(handler.getBound().equals(PacketListener.BoundType.SERVER_BOUND) &&
                handler.getPacket() != null &&
                handler.getPacket().getClass().getSimpleName().equals("PacketPlayInUseEntity")){
            int id = (int) handler.getPacketValue("a");
            if(players.contains(id)){
                handler.setCancelled(true);
            }
        }
    }

    public ProtectionOfClairvoyance(){
        EnchantsAPI.register(this);
        AnnotationHandler.register(ProtectionOfClairvoyance.class, null);

        initConfig("cooldown", "{lv}*1.8+6");
        initConfig("duration", "{lv}*1.2+5");
        initConfig("radius", "{lv}*200+500");
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if(GameVersion.is1_9Above()) {
            if(event.getHand() != null  && event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        if(p.isSneaking() && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))){
            ItemStack i = p.getInventory().getHelmet();
            if(checkRequirements(i, p)) {
                if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                    setCooldown(enchantName(), p);
                    int lv = new EnchantsAPI(i).getEnchantLevel(this);
                    double d = mathConfig("duration", i);
                    double r = mathConfig("radius", i);
                    HashMap<LivingEntity, Double> entities = new HashMap<>();
                    for(Entity e : p.getNearbyEntities(r, r, r)) {
                        if(e instanceof LivingEntity) {
                            ItemStack helmet = ((LivingEntity) e).getEquipment().getHelmet();
                            if(!InventoryUtils.isNull(helmet) && new EnchantsAPI(helmet).hasEnchant(this)
                                    && new EnchantsAPI(helmet).getEnchantLevel(this) == lv) {
                                double dis = MathUtils.round(e.getLocation()
                                        .distance(p.getLocation()));
                                if(dis > 8){
                                    continue;
                                }
                                entities.put((LivingEntity) e, dis);
                            }
                        }
                        if(entities.size() == 54) {
                            break;
                        }
                    }
                    InventoryManager inv = new InventoryManager("&c&lCHỌN THỰC THỂ", 54);
                    inv.fill(new ItemManager("&a", GameVersion.is1_13Above() ? Material.LIGHT_GRAY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8).getItem(), (player, itemStack, clickType, i12, inventoryAction, inventory) -> {

                    });
                    int v = 0;
                    for(LivingEntity le : entities.keySet()) {
                        inv.set(v, new SkullManager(SkullType.PLAYER).setName(le.getName() + "#" + le.getEntityId()).addLore("&aKhoảng cách: " + entities.get(le)).getItem(), (player, itemStack, clickType, i1, inventoryAction, inventory) -> {
                            player.closeInventory();
                            players.add(player.getEntityId());
                            Camera.create(le.getEntityId()).sendPlayer(player);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Camera.create(player.getEntityId()).sendPlayer(player);
                                    players.remove(player.getEntityId());
                                }
                            }.runTaskLaterAsynchronously(EnchantNangCao.plugin,
                                    (long) (20 * d));
                        });
                        v++;
                    }
                    inv.open(p);
                } else {
                    EnchantNangCao.chat.sendPlayer("&cBạn còn &6" +
                            timeLeft(enchantName(), p, (int) mathConfig("cooldown", i))
                            + " &cgiây để dùng tiếp!", p);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "ProtectionOfClairvoyance";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_HELMET");
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
    protected String[] enchantDescription() {
        return new String[]{
                "Đội nón này rồi nhấn shift để có thể",
                "nhập vào bất kỳ ai (miễn là họ đang",
                "đeo nón có phù phép này và có cùng cấp độ)",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Thời gian xem: &e"+getConfig().getString("duration"),
                "&c* Bán kính: &e"+getConfig().getString("radius")
        };
    }

    @Override
    public String enchantItemStack() {
        return "Nón";
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.SKILL;
    }
}
