package org.anhcraft.enchantnangcao;

import com.earth2me.essentials.Essentials;
import de.slikey.effectlib.EffectManager;
import org.anhcraft.enchantnangcao.enchantments.*;
import org.anhcraft.enchantnangcao.manager.EnchantmentBox;
import org.anhcraft.enchantnangcao.manager.EnchantsAPI;
import org.anhcraft.enchantnangcao.manager.EnchantsConfig;
import org.anhcraft.enchantnangcao.utils.Configuration;
import org.anhcraft.enchantnangcao.utils.WorldGuardHook;
import org.anhcraft.spaciouslib.annotations.AnnotationHandler;
import org.anhcraft.spaciouslib.builders.command.ArgumentType;
import org.anhcraft.spaciouslib.builders.command.ChildCommandBuilder;
import org.anhcraft.spaciouslib.builders.command.CommandBuilder;
import org.anhcraft.spaciouslib.builders.command.CommandCallback;
import org.anhcraft.spaciouslib.inventory.CenterPosition;
import org.anhcraft.spaciouslib.inventory.InventoryManager;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.io.DirectoryManager;
import org.anhcraft.spaciouslib.io.FileManager;
import org.anhcraft.spaciouslib.utils.Chat;
import org.anhcraft.spaciouslib.utils.CommonUtils;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class EnchantNangCao extends JavaPlugin {
    public static EnchantNangCao plugin;
    public static String version;
    public static Essentials essentials;
    public static EffectManager effect;
    public static Chat chat;
    public static boolean worldGuard = false;

    @Override
    public void onLoad(){
        if(getServer().getPluginManager().isPluginEnabled("Essentials")) {
            essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
            getLogger().info("Hooked to Essentials!");
        }
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = true;
            WorldGuardHook.init();
            getLogger().info("Hooked to WorldGuard! Registered the flag ENC");
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        effect = new EffectManager(this);
        version = plugin.getDescription().getVersion();

        if(184 > Integer.parseInt(getServer().getPluginManager().getPlugin("SpaciousLib").getDescription().getVersion().replace(".", ""))){
            getLogger().info("");
            getLogger().info("");
            getLogger().info("***************************************************************");
            getLogger().info("");
            getLogger().info("Bạn phải cài SpaciousLib từ bản 1.8.4 trở lên để ENC hoạt động!");
            getLogger().info("");
            getLogger().info("***************************************************************");
            getLogger().info("");
            getLogger().info("");
            new BukkitRunnable() {
                @Override
                public void run() {
                    getServer().shutdown();
                }
            }.runTaskLater(this, 100);
            return;
        }

        new DirectoryManager("plugins/EnchantNangCao/data/").mkdirs();

        try {
            new FileManager("plugins/EnchantNangCao/config.yml")
                    .initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/config.yml")));
            new FileManager("plugins/EnchantNangCao/data/AgonyTraps.yml")
                    .initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/data/AgonyTraps.yml")));
            new FileManager("plugins/EnchantNangCao/data/BounceTraps.yml")
                    .initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/data/BounceTraps.yml")));
        } catch(IOException e) {
            e.printStackTrace();
        }

        AnnotationHandler.register(EnchantmentBox.class, null);

        getServer().getPluginManager().registerEvents(new ColorSheep(), this);
        getServer().getPluginManager().registerEvents(new Kickup(), this);
        getServer().getPluginManager().registerEvents(new Rolling(), this);
        getServer().getPluginManager().registerEvents(new Freeze(), this);
        getServer().getPluginManager().registerEvents(new Magixel(), this);
        getServer().getPluginManager().registerEvents(new Agony(), this);
        getServer().getPluginManager().registerEvents(new Bounce(), this);
        getServer().getPluginManager().registerEvents(new Fireball(), this);
        getServer().getPluginManager().registerEvents(new Shield(), this);
        getServer().getPluginManager().registerEvents(new LifeGuard(), this);
        getServer().getPluginManager().registerEvents(new Seriatim(), this);
        getServer().getPluginManager().registerEvents(new Kickback(), this);
        getServer().getPluginManager().registerEvents(new Noel(), this);
        getServer().getPluginManager().registerEvents(new Darkness(), this);
        getServer().getPluginManager().registerEvents(new Dizziness(), this);
        getServer().getPluginManager().registerEvents(new Starve(), this);
        getServer().getPluginManager().registerEvents(new Slowness(), this);
        getServer().getPluginManager().registerEvents(new Mjolnir(), this);
        getServer().getPluginManager().registerEvents(new Illuminati(), this);
        getServer().getPluginManager().registerEvents(new DoubleJump(), this);
        getServer().getPluginManager().registerEvents(new Escape(), this);
        getServer().getPluginManager().registerEvents(new Explosive(), this);
        getServer().getPluginManager().registerEvents(new Ninja(), this);
        getServer().getPluginManager().registerEvents(new FireThrower(), this);
        getServer().getPluginManager().registerEvents(new Pull(), this);
        getServer().getPluginManager().registerEvents(new Vampire(), this);
        getServer().getPluginManager().registerEvents(new Surfing(), this);
        getServer().getPluginManager().registerEvents(new Wither(), this);
        getServer().getPluginManager().registerEvents(new Savior(), this);
        getServer().getPluginManager().registerEvents(new Smelter(), this);
        getServer().getPluginManager().registerEvents(new AquaticShield(), this);
        getServer().getPluginManager().registerEvents(new Chef(), this);
        getServer().getPluginManager().registerEvents(new MorningStar(), this);
        getServer().getPluginManager().registerEvents(new Doctor(), this);
        getServer().getPluginManager().registerEvents(new Laser(), this);
        getServer().getPluginManager().registerEvents(new ProtectionOfTearySkies(), this);
        getServer().getPluginManager().registerEvents(new Tornado(), this);
        getServer().getPluginManager().registerEvents(new PushFlower(), this);
        getServer().getPluginManager().registerEvents(new Spray(), this);
        if(GameVersion.is1_9Above()) {
            getServer().getPluginManager().registerEvents(new Glide(), this);
            getServer().getPluginManager().registerEvents(new Yasuo(), this);
            getServer().getPluginManager().registerEvents(new ProtectionOfClairvoyance(), this);
        }
        if(GameVersion.is1_11Above()){
            getServer().getPluginManager().registerEvents(new Thunder(), this);
            getServer().getPluginManager().registerEvents(new Wing(), this);
            getServer().getPluginManager().registerEvents(new Detective(), this);
            getServer().getPluginManager().registerEvents(new Bomb(), this);
            getServer().getPluginManager().registerEvents(new Eyeless(), this);
        }
        new Soulbound();
        new Lucky();
        new NurseHeal();
        new Evade();

        Configuration.load();
        chat = new Chat(Configuration.config.getString("prefix"));
        init();

        new BukkitRunnable() {
            @Override
            public void run() {
                chat.sendConsole("&aĐã bật plugin! Code bởi anhcraft");
                chat.sendConsole("&eThích plugin này? Hãy donate nhé <3");
                chat.sendConsole("&eFB: https://fb.com/anhcraft/");
            }
        }.runTaskLaterAsynchronously(this, 30);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Agony.conf.save(Agony.f);
                    Bounce.conf.save(Bounce.f);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(this, 100, 100);

        new CommandBuilder("enc", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                if(commandSender instanceof Player) {
                    openMenuHelp((Player) commandSender, 0);
                } else {
                    chat.sendCommandSender("&cBạn phải ở trong game!", commandSender);
                }
            }
        }).addChild("nạp lại cấu hình", new ChildCommandBuilder().path("reload", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                if(commandSender.hasPermission("enc.reload")) {
                    Configuration.load();
                    init();
                    chat.sendCommandSender("&aĐã nạp lại tệp tin cấu hình!", commandSender);
                } else {
                    chat.sendCommandSender("&cBạn không có quyền!", commandSender);
                }
            }
        }).build()).addChild("xem danh sách phù phép", new ChildCommandBuilder().path("list", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                chat.sendCommandSenderNoPrefix(listENC(), commandSender);
            }
        }).build()).addChild("xem danh sách lệnh ENC", new ChildCommandBuilder().path("help", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                commandBuilder.sendHelpMessages(commandSender, true, true);
            }
        }).build()).addChild("thêm phù phép ENC", new ChildCommandBuilder().path("add").var("enchant", ArgumentType.ANYTHING).var("level", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if(player.hasPermission("enc.add")) {
                        if(EnchantsAPI.isEnchantListed(strings[i1-1])) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            if(InventoryUtils.isNull(item)) {
                                chat.sendCommandSender("&cBạn phải cầm item trong tay", player);
                            } else {
                                int lv = CommonUtils.toInteger(s);
                                if(0 < lv) {
                                    EnchantmentBox eb = EnchantsAPI.getEnchant(strings[i1-1]);
                                    assert eb != null;
                                    if(eb.enchantMaxLevel() < lv && !Configuration.config.getBoolean(
                                            "unsafe_enchantment")){
                                        EnchantNangCao.chat.sendPlayer("&4Enchant này chỉ cho phép đến cấp "+eb.enchantMaxLevel()+"!", player);
                                    } else if(!eb.canEnchantItem(item) &&
                                            !Configuration.config.getBoolean("unsafe_enchantment")) {
                                        EnchantNangCao.chat.sendPlayer("&4Enchant này không sử dụng được với item bạn đang cầm!", player);
                                    } else if(!eb.enchantVersion(GameVersion.getVersion())) {
                                        EnchantNangCao.chat.sendPlayer("&4Enchant này không dùng cho phiên bản hiện tại!", player);
                                    } else {
                                        new EnchantsAPI(item).addEnchant(eb, lv);
                                    }
                                } else {
                                    chat.sendCommandSender("&cCấp enchant phải từ 1 trở lên", player);
                                }
                            }
                        } else {
                            chat.sendCommandSender("&cKhông tìm thấy enchant bạn cần", player);
                            chat.sendCommandSenderNoPrefix(listENC(), player);
                        }
                    } else {
                        chat.sendCommandSender("&cBạn không có quyền!", player);
                    }
                } else {
                    chat.sendCommandSender("&cBạn phải ở trong game!", commandSender);
                }
            }
        }, ArgumentType.POSITIVE_INTEGER).build()).addChild("xóa phù phép ENC", new ChildCommandBuilder().path("del").var("enchant", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if(player.hasPermission("enc.del")) {
                        if(EnchantsAPI.isEnchantListed(s)) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            if(InventoryUtils.isNull(item)) {
                                chat.sendCommandSender("&cBạn phải cầm item trong tay", player);
                            } else {
                                EnchantmentBox eb = EnchantsAPI.getEnchant(s);
                                if(new EnchantsAPI(item).hasEnchant(eb)) {
                                    new EnchantsAPI(item).removeEnchant(eb);
                                } else {
                                    EnchantNangCao.chat.sendPlayer("&4Không có enchant này trong item bạn cầm!", player);
                                }
                            }
                        } else {
                            chat.sendCommandSender("&cKhông tìm thấy enchant bạn cần", player);
                            chat.sendCommandSenderNoPrefix(listENC(), player);
                        }
                    } else {
                        chat.sendCommandSender("&cBạn không có quyền!", player);
                    }
                } else {
                    chat.sendCommandSender("&cBạn phải ở trong game!", commandSender);
                }
            }
        }, ArgumentType.ANYTHING).build()).addAlias("enchantnangcao").build(this);
    }

    private String listENC() {
        StringBuilder ecs = new StringBuilder();
        for(EnchantmentBox e : EnchantsAPI.enchants.values()) {
            ecs.append(", ").append(e.customName());
        }
        return "&f"+ecs.toString().replaceFirst(", ", "").toLowerCase();
    }

    public static void openMenuHelp(Player p, int in) {
        InventoryManager s = new InventoryManager("&c&lThông tin Enchant", 54);
        s.fill(new ItemManager("&a", GameVersion.is1_13Above() ? Material
                .LIGHT_GRAY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8).getItem(), (player, itemStack, clickType, i, inventoryAction, inventory) -> {

        });
        List<EnchantmentBox> l = CommonUtils.getPageItems(new ArrayList<>(EnchantsAPI.enchants.values()), in, 45);
        if(l.size() == 0){
            return;
        }
        int i = 0;
        for(EnchantmentBox eb : l){
            ItemManager si = new ItemManager("&a"+eb.customName()+" &r&f["+eb.enchantAuthor()+"]", Material.ENCHANTED_BOOK, 1)
                    .setLores(eb.getEnchantDescription())
                    .addLore("&bCấp tối đa: &f" + eb.enchantMaxLevel())
                    .addLore("&bDành cho vật phẩm: &f" + eb.enchantItemStack())
                    .addLore("&bThuộc loại: &f" + eb.enchantType().str());
            if(eb.enchantProposer() != null){
                si.addLore("&bĐề xuất bởi: " + eb.enchantProposer());
            }
            s.set(i, si.getItem(), (player, itemStack, clickType, i13, inventoryAction, inventory) -> {
            });
            i++;
        }
        s.set(45 + CenterPosition.CENTER_3_A, new ItemManager("&dTrang trước", Material.ARROW,
                1).getItem(), (player, itemStack, clickType, i14, inventoryAction, inventory) -> openMenuHelp(player, in-1));
        s.set(45 + CenterPosition.CENTER_3_B, new ItemManager("&dTrang sau", Material.ARROW,
                1).getItem(), (player, itemStack, clickType, i16, inventoryAction, inventory) -> openMenuHelp(player, in+1));
        s.open(p);
    }

    public void init() {
        File f = new File("plugins/EnchantNangCao/enchants.yml");
        new FileManager(f).create();
        EnchantsConfig.load(YamlConfiguration.loadConfiguration(f));
        try {
            EnchantsConfig.save().save(f);
        } catch(IOException e) {
            e.printStackTrace();
        }
        chat.sendConsole("&aĐã đăng ký "+EnchantsAPI.enchants.size()+" phù phép");
    }

    @Override
    public void onDisable() {
        Freeze.destroy();
        Yasuo.destroy();
        Seriatim.destroy();
        Noel.destroy();
        Ninja.destroy();
        LifeGuard.destroy();
        Illuminati.destroy();
        ProtectionOfClairvoyance.destroy();
        try {
            Agony.conf.save(Agony.f);
            Bounce.conf.save(Bounce.f);
        } catch(IOException e) {
            e.printStackTrace();
        }
        effect.dispose();
        getServer().getScheduler().cancelTasks(this);
        AnnotationHandler.unregister(EnchantmentBox.class, null);
        chat.sendConsole("&cĐã tắt plugin!");
    }
}