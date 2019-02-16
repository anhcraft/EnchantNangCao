package org.anhcraft.enchantnangcao.enchantments;

import de.slikey.effectlib.effect.DnaEffect;
import de.slikey.effectlib.effect.LineEffect;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class NurseHeal extends EnchantmentBox {
    @Override
    public String enchantName() {
        return "NurseHeal";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack i) {
        return MaterialUtils.getArmorTypes().contains(i.getType());
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
                "Khi còn dưới 25% máu, bạn có khả năng",
                "ăn cắp máu của các thực thể xung quanh",
                "&c* Thời gian chờ: &e"+getConfig().getString("cooldown"),
                "&c* Số máu lấy từ mỗi thực thể: &e"+getConfig().getString("heal"),
                "&c* Số máu tối đa có thể lấy: &e"+getConfig().getString("heal_max"),
                "&c* Bán kính tìm kiếm thực thể: &e"+getConfig().getString("entities_around")
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemArmors();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.AUTO_ACTIVATE;
    }

    public NurseHeal(){
        EnchantsAPI.register(this);

        initConfig("cooldown", "({maxlv}-{lv})+3");
        initConfig("heal", "{lv}+3");
        initConfig("heal_max", "{lv}*2+8");
        initConfig("entities_around", "{lv}+3");
        initConfig("protect_afk_players", true);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                    for(ItemStack i : p.getInventory().getArmorContents()){
                        if(!InventoryUtils.isNull(i)
                                && MaterialUtils.getArmorTypes().contains(i.getType())){
                            if(checkRequirements(i, p) &&
                                    p.getHealth() <= (p.getMaxHealth()/100*25)){
                                if(isTimeout(enchantName(), p, mathConfig("cooldown", i))) {
                                    Title.create("&2&lHỒI MÁU!!!", Title.Type.TITLE).sendPlayer(p);
                                    setCooldown(enchantName(), p);
                                    int around = (int) mathConfig(
                                            "entities_around", i);
                                    double heal =   mathConfig(
                                            "heal", i);
                                    double healMax =   mathConfig(
                                            "heal_max", i);
                                    double healTotal = 0.0;
                                    for(Entity a : p.getNearbyEntities(around, around, around)) {
                                        if(!(a instanceof LivingEntity)) {
                                            return;
                                        }
                                        LivingEntity l = (LivingEntity) a;
                                        if(l instanceof Player && EnchantNangCao.essentials != null && getConfig().getBoolean(
                                                "protect_afk_players")
                                                && EnchantNangCao.essentials.getUser(l.getUniqueId()).isAfk()){
                                            return;
                                        }
                                        LineEffect eff = new LineEffect(EnchantNangCao.effect);
                                        eff.particle = Particle.CLOUD;
                                        eff.particles = 5;
                                        eff.asynchronous = true;
                                        eff.duration = 3;
                                        eff.start();
                                        l.damage(heal);
                                        healTotal += heal;
                                        if(healMax <= healTotal){
                                            break;
                                        }
                                    }
                                    double newheal = healTotal + p.getHealth();
                                    if(p.getMaxHealth() < newheal){
                                        newheal = p.getMaxHealth();
                                    }
                                    p.setHealth(newheal);
                                    DnaEffect se = new DnaEffect(EnchantNangCao.effect);
                                    se.particleBase1 = Particle.CLOUD;
                                    se.particleBase2 = Particle.REDSTONE;
                                    se.colorBase1 = Color.WHITE;
                                    se.colorBase1 = Color.RED;
                                    se.colorBase1 = Color.GREEN;
                                    se.asynchronous = true;
                                    se.duration = 5;
                                    se.setLocation(p.getLocation());
                                    se.start();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(EnchantNangCao.plugin, 60, 60);
    }
}
