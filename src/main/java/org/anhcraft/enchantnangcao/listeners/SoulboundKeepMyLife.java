package org.anhcraft.enchantnangcao.listeners;

import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.keepmylife.events.PlayerKeepItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class SoulboundKeepMyLife implements Listener {
    private EnchantmentBox s;

    public SoulboundKeepMyLife(EnchantmentBox s) {
        this.s = s;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamage(PlayerKeepItemEvent ev) {
        for(Iterator<ItemStack> it = ev.getDropItems().iterator(); it.hasNext(); ) {
            ItemStack i = it.next();
            if(s.checkRequirements(i, ev.getPlayer())) {
                ev.getKeepItems().add(i);
                it.remove();
            }
        }
    }
}