package net.omni.mythicbosses.commands;

import net.omni.mythicbosses.MythicBosses;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BossCommand implements CommandExecutor {

    private final MythicBosses plugin;
    private final String[] help;

    public BossCommand(MythicBosses plugin) {
        this.plugin = plugin;

        this.help = new String[]{
                "&l&bMythicBosses",
                "&9/boss &7» Shows the help messages.",
                "&9/boss spawn <mythicMob> <location> &7» (Location needs to be separated by ',') " +
                        "Spawns a boss to the specified location.",
                "&9/boss give <mythicMob> <location> &7» Gives the player a spawn egg of the boss which will be spawned\n" +
                        "at the specified location.",
                "&9/boss reload &7» Reloads the plugin.",
                "&9/boss timers &7» Shows the next boss scheduled to be spawned and time left before it spawns."
        };
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("mythicbosses.use")) {
            plugin.sendMessage(sender, "&cYou do not have permissions to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(sender, StringUtils.join(help, "\n"));
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.getMessagesHandler().reload();
                plugin.getMessagesUtil().load();
                plugin.getConfigHandler().reload();
                plugin.getBossManager().loadBosses();

                // TODO others to reload
                plugin.sendMessage(sender, "&aSuccessfully reloaded plugin.");
            } else if (args[0].equalsIgnoreCase("timers")) {
                // TODO
            } else if (args[0].equalsIgnoreCase("spawn")) {
                // TODO
            } else if (args[0].equalsIgnoreCase("give")) {
                if (!(sender instanceof Player)) {
                    plugin.sendMessage(sender, "&cOnly players can use this command.");
                    return true;
                }

                // TODO
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("spawn")) {
                // TODO: get location
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
