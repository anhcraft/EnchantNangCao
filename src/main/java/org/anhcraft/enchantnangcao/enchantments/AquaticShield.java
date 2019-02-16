package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.SphereEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AquaticShield extends EnchantmentBox implements Listener {
    public AquaticShield(){
        EnchantsAPI.register(this);
        
        initConfig("cooldown", "{lv}*1.8+4");
        initConfig("duration", "{lv}*1.4+3");
        initConfig("level", "{lv}*1.2");
    }

    @EventHandler
    public void shift(PlayerToggleSneakEvent event){
        if(event.isSneaking()){
            Player p = event.getPlayer();
            ItemStack i = p.getInventory().getChestplate();
            if(checkRequirements(i, p) && isTimeout(enchantName(), p, mathConfig("cooldown", i))){
                double d = mathConfig("duration", i);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (d * 20),
                        (int) mathConfig("level", i)));
                SphereEffect ef = new SphereEffect(EnchantNangCao.effect);
                ef.particle = Particle.REDSTONE;
                ef.color = Color.BLUE;
                ef.asynchronous = true;
                ef.particles = 900;
                ef.radius = 3;
                ef.duration = 1;
                ef.setLocation(p.getLocation());
                ef.start();
                setCooldown(enchantName(), p);
            }
        }
    }

    @Override
    public String enchantName() {
        return "AquaticShield";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public String enchantProposer(){
        return "LinhDoraemon - play.satancraft.net";
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return i.getType().toString().endsWith("_CHESTPLATE");
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
    protected String[] enchantDescription() {
        return new String[]{
                "Mặc áo có enchant này khi shift sẽ tạo",
                "lá chắn hình cầu, giúp giảm sát thương phải nhận",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Thời gian bảo vệ: &e"+getConfig().getString("duration"),
                "&c* Cấp độ bảo vệ: &e"+getConfig().getString("level")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemChestplate();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.DEFAULT;
    }
}
