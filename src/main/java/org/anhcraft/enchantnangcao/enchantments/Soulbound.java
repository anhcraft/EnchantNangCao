package org.anhcraft.enchantnangcao.enchantments;

import org.anhcraft.enchantnangcao.manager.EnchantType;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.enchantnangcao.EnchantNangCao;
import org.anhcraft.enchantnangcao.listeners.SoulboundDeathDropsAPI;
import org.anhcraft.enchantnangcao.listeners.SoulboundDefault;
import org.anhcraft.enchantnangcao.listeners.SoulboundKeepMyLife;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Soulbound extends EnchantmentBox {
    @Override
    public String enchantName() {
        return "Soulbound";
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
        return 1;
    }

    @Override
    public Boolean enchantVersion(GameVersion version) {
        return true;
    }

    @Override
    public String[] enchantDescription() {
        return new String[]{
                "Giúp vật phẩm có phù phép này được",
                "keep khi bạn chết :D"
        };
    }

    @Override
    public String enchantItemStack() {
        return getDefaultItemAllItems();
    }

    @Override
    public EnchantType enchantType() {
        return EnchantType.AUTO_ACTIVATE;
    }

    public Soulbound(){
        EnchantsAPI.register(this);
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("KeepMyLife")){
            Bukkit.getServer().getPluginManager().registerEvents(new SoulboundKeepMyLife(this),
                    EnchantNangCao.plugin);
        } else {
            if(Bukkit.getServer().getPluginManager().isPluginEnabled("DeathDropsAPI")){
                Bukkit.getServer().getPluginManager().registerEvents(new SoulboundDeathDropsAPI(this),
                        EnchantNangCao.plugin);
            }else{
                Bukkit.getServer().getPluginManager().registerEvents(new SoulboundDefault(this),
                        EnchantNangCao.plugin);
            }
        }
    }
}
