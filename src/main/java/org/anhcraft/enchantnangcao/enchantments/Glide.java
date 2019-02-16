package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Particle;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Glide extends EnchantmentBox implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev){
        Player d = ev.getPlayer();
        if(d.isGliding() && GameVersion.is1_9Above()){
            ItemStack i = d.getInventory().getChestplate();
            if(i != null) {
                if(checkRequirements(i, d) && i.getType().equals(Material.ELYTRA)) {
                    Particle.create(Particle.Type.CLOUD, ev.getTo(), 10);
                }
            }
        }
    }

    @Override
    public String enchantName() {
        return "Glide";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().equals(Material.ELYTRA);
    }

    @Override
    public int enchantMaxLevel() {
        return 1;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return !version.toString().contains("1_8");
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
             "Khi lượn với cánh cứng sẽ có hiệu ứng",
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemElytra();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.AUTO_ACTIVATE;
    }

    public Glide(){
        EnchantsAPI.register(this);
    }
}
