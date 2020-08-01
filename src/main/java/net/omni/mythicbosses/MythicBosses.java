package net.omni.mythicbosses;

import net.omni.mythicbosses.boss.BossManager;
import net.omni.mythicbosses.commands.BossCommand;
import net.omni.mythicbosses.handlers.ConfigHandler;
import net.omni.mythicbosses.handlers.DamageHandler;
import net.omni.mythicbosses.listeners.BossDamageListener;
import net.omni.mythicbosses.util.MessagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicBosses extends JavaPlugin {

    private ConfigHandler configHandler;
    private ConfigHandler messagesHandler;
    private MessagesUtil messagesUtil;
    private DamageHandler damageHandler;
    private BossManager bossManager;

    @Override
    public void onEnable() {
        this.configHandler = new ConfigHandler(this, "config");
        this.messagesHandler = new ConfigHandler(this, "messages");
        this.messagesUtil = new MessagesUtil(this);

        this.bossManager = new BossManager(this);

        this.damageHandler = new DamageHandler();

        registerListeners();
        registerCommands();

        sendConsole("&aSuccessfully enabled MythicBosses v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {

        bossManager.flush();
        damageHandler.flush();
        sendConsole("&aSuccessfully disabled MythicBosses");
    }

    public void broadcast(String message) {
        Bukkit.broadcastMessage(translate(message));

        for (Player player : Bukkit.getServer().getOnlinePlayers())
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.2f, 1f);
    }

    public void sendConsole(String text) {
        sendMessage(Bukkit.getConsoleSender(), text);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate(getMessagesUtil().getPrefix() + " " + message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public DamageHandler getDamageHandler() {
        return damageHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public ConfigHandler getMessagesHandler() {
        return messagesHandler;
    }

    public MessagesUtil getMessagesUtil() {
        return messagesUtil;
    }

    private void registerListeners() {
        new BossDamageListener(this).register();
    }

    private void registerCommands() {
        new BossCommand(this).register();
    }
}