package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.MaterialUtils;
import org.anhcraft.spaciouslib.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Lucky extends EnchantmentBox {
    public Lucky(){
        EnchantsAPI.register(this);

        initConfig("chance", "{lv}*5");
        initConfig("effects_amount", "{lv}+1");

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getServer().getOnlinePlayers()){
                    for(ItemStack i : p.getInventory().getArmorContents()) {
                        if(checkRequirements(i, p)
                                && MaterialUtils.getArmorTypes().contains(i.getType())) {
                            if(Math.random() < mathConfig("chance", i)/100){
                                List<PotionEffectType> q = hasBadEffect(p);
                                if(0 < q.size()) {
                                    List<PotionEffectType> t = new ArrayList<>();
                                    for(int x = 0; x < (int) mathConfig("effects_amount", i); x++){
                                        t.add(RandomUtils.pickRandom(q));
                                    }
                                    for(PotionEffectType s : t){
                                        p.removePotionEffect(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(EnchantNangCao.plugin, 60, 60);
    }

    private List<PotionEffectType> hasBadEffect(Player p) {
        List<PotionEffectType> types = new ArrayList<>();
        types.add(PotionEffectType.SLOW);
        types.add(PotionEffectType.CONFUSION);
        types.add(PotionEffectType.WITHER);
        types.add(PotionEffectType.BLINDNESS);
        types.add(PotionEffectType.LEVITATION);
        types.add(PotionEffectType.POISON);
        types.add(PotionEffectType.SLOW_DIGGING);
        types.add(PotionEffectType.UNLUCK);
        types.add(PotionEffectType.WEAKNESS);
        types.add(PotionEffectType.INVISIBILITY);
        List<PotionEffectType> active = new ArrayList<>();
        for(PotionEffect e : p.getActivePotionEffects()){
            if(types.contains(e.getType())){
                active.add(e.getType());
            }
        }
        return active;
    }

    @Override
    public String enchantName() {
        return "Lucky";
    }

    @Override
    public String enchantAuthor() {
        return getDefaultAuthor();
    }

    @Override
    public Boolean canEnchantItem(ItemStack itemStack) {
        return MaterialUtils.getArmorTypes().contains(itemStack.getType());
    }

    @Override
    public int enchantMaxLevel() {
        return 5;
    }

    @Override
    public Boolean enchantVersion(GameVersion gVersion) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Có một tỉ lệ giúp tự động xóa bỏ các hiệu ứng xấu",
                "&c* Tỉ lệ: &e"+getConfig().getString("chance"),
                "&c* Số hiệu ứng sẽ xóa: &e"+getConfig().getString("effects_amount")
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
}
