package net.omni.mythicbosses.commands;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import net.omni.mythicbosses.MythicBosses;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

public class BossCommand implements CommandExecutor {

    private final MythicBosses plugin;
    private final String[] help;

    public BossCommand(MythicBosses plugin) {
        this.plugin = plugin;
        this.help = new String[]{
                "",
                "",
                ""
        };

        MobManager mg = MythicMobs.inst().getMobManager();
        mg.spawnMob("", new Location(null, 0,0,0));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("")) {
            plugin.sendMessage(sender, "&cYou do not have permissions to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(sender, StringUtils.join(help, "\n"));
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // TODO
            } else if (args[0].equalsIgnoreCase("timers")) {
// TODO
            } else if (args[0].equalsIgnoreCase("spawn")) {
// TODO
            } else if (args[0].equalsIgnoreCase("give")) {
// TODO
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("spawn")) {
// TODO
            } else if (args[0].equalsIgnoreCase("give")) {
// TODO
            } else
                plugin.sendMessage(sender, StringUtils.join(help, "\n"));

            return true;
        } else {
            plugin.sendMessage(sender, StringUtils.join(help, "\n"));
            return true;
        }
    }

    public void register() {
        PluginCommand pluginCommand = plugin.getCommand("boss");

        if (pluginCommand != null)
            pluginCommand.setExecutor(this);
    }
}
