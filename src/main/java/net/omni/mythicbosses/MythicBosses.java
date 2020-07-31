package net.omni.mythicbosses;

import net.omni.mythicbosses.commands.BossCommand;
import net.omni.mythicbosses.handlers.DamageHandler;
import net.omni.mythicbosses.listeners.BossDamageListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicBosses extends JavaPlugin {

    private DamageHandler damageHandler;

    @Override
    public void onEnable() {
        this.damageHandler = new DamageHandler();

        registerListeners();
        registerCommands();

        sendConsole("&aSuccessfully enabled MythicBosses v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {

        damageHandler.flush();
        sendConsole("&aSuccessfully disabled MythicBosses");
    }

    public void sendConsole(String text) {
        sendMessage(Bukkit.getConsoleSender(), text);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate("[PREFIX] " + message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public DamageHandler getDamageHandler() {
        return damageHandler;
    }

    private void registerListeners() {
        new BossDamageListener(this).register();
    }

    private void registerCommands() {
        new BossCommand(this).register();
    }
}
