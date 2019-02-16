package org.anhcraft.enchantnangcao.listeners;

import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SoulboundDefault implements Listener {
    private EnchantmentBox s;

    public SoulboundDefault(EnchantmentBox s) {
        this.s = s;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamage(PlayerDeathEvent ev){
        if(!ev.getKeepInventory()) {
            ev.setKeepInventory(true);
            List<ItemStack> items = new ArrayList<>();
            for(ItemStack i : ev.getDrops()) {
                if(i != null && !i.getType().equals(Material.AIR)) {
                    if(s.checkRequirements(i, ev.getEntity())) {
                        items.add(i);
                    } else {
                        ev.getEntity().getWorld().dropItemNaturally(ev.getEntity().getLocation(), i);
                    }
                }
            }
            ev.getEntity().getInventory().setContents(items.toArray(new ItemStack[0]));
        }
    }
}