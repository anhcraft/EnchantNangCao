package org.anhcraft.enchantnangcao.listeners;

import net.minefs.DeathDropsAPI.PlayerDeathDropEvent;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoulboundDeathDropsAPI implements Listener {
    private EnchantmentBox s;

    public SoulboundDeathDropsAPI(EnchantmentBox s) {
        this.s = s;
    }

    @EventHandler
    public void PlayerDamage(PlayerDeathDropEvent ev) {
        if(s.checkRequirements(ev.getItem(), ev.getPlayer())) {
            ev.setCancelled(true);
        }
    }
}
