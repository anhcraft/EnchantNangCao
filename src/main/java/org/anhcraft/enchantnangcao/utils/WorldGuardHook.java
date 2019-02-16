package org.anhcraft.enchantnangcao.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public class WorldGuardHook {
    public static StateFlag flag;

    public static boolean check(Entity entity){
        RegionManager rm = WorldGuardPlugin.inst().getRegionManager(entity.getWorld());
        if(rm != null) {
            ApplicableRegionSet ar = rm.getApplicableRegions(entity.getLocation());
            Set<ProtectedRegion> x = ar.getRegions();
            if(x == null){
                x = new HashSet<>();
            }
            x.add(rm.getRegion("__global__"));
            int priority = -1;
            boolean start = false;
            boolean can = true;
            for(ProtectedRegion region : x) {
                if(region == null){
                    continue;
                }
                if(!start) {
                    start = true;
                    priority = region.getPriority();
                    StateFlag.State fs = region.getFlag(flag);
                    if(fs != null) {
                        can = fs.equals(StateFlag.State.ALLOW);
                    }
                } else {
                    if(region.getPriority() > priority) {
                        priority = region.getPriority();
                        StateFlag.State fs = region.getFlag(flag);
                        if(fs != null) {
                            can = fs.equals(StateFlag.State.ALLOW);
                        }
                    }
                }
            }
            return can;
        }
        return true;
    }

    public static void init() {
        flag = new StateFlag("enc", true);
        SimpleFlagRegistry fr = (SimpleFlagRegistry) WorldGuardPlugin.inst().getFlagRegistry();
        fr.register(flag);
    }
}
