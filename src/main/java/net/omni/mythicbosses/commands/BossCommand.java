package net.omni.mythicbosses.commands;

import io.lumine.xikage.mythicmobs.mobs.EggManager;
import javafx.util.Pair;
import net.omni.mythicbosses.MythicBosses;
import net.omni.mythicbosses.boss.Boss;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BossCommand implements CommandExecutor {

    private final MythicBosses plugin;
    private final String msg;

    public BossCommand(MythicBosses plugin) {
        this.plugin = plugin;

        String[] help = new String[]{
                "&l&bMythicBosses",
                "&9/boss &7» Shows the help messages.",
                "&9/boss spawn <mythicMob> <location> &7» (Location needs to be separated by ',') " +
                        "Spawns a boss to the specified location.",
                "&9/boss give <mythicMob> <location> &7» Gives the player a spawn egg of the boss which will be spawned\n" +
                        "at the specified location.",
                "&9/boss reload &7» Reloads the plugin.",
                "&9/boss timers &7» Shows the next boss scheduled to be spawned and time left before it spawns."
        };

        this.msg = StringUtils.join(help, "\n");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("mythicbosses.use")) {
            plugin.sendMessage(sender, "&cYou do not have permissions to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(sender, msg);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.getMessagesHandler().reload();
                plugin.getMessagesUtil().load();
                plugin.getConfigHandler().reload();
                plugin.getBossManager().loadBosses();

                plugin.sendMessage(sender, "&aSuccessfully reloaded plugin.");
            } else if (args[0].equalsIgnoreCase("timers")) {
                plugin.sendMessage(sender, "&cScheduled boss spawn: ");

                if (!plugin.getBossManager().getSchedule().isEmpty()) {
                    for (Map.Entry<Boss, Integer> entry : plugin.getBossManager().getSchedule().entrySet()) {
                        if (entry.getKey() == null)
                            continue;

                        plugin.sendMessage(sender,
                                entry.getKey().getMythicMobName().get() + " &3- "
                                        + plugin.secToTime(entry.getValue()));
                    }
                } else
                    plugin.sendMessage(sender, "&3None");
            } else if (args[0].equalsIgnoreCase("spawn"))
                plugin.sendMessage(sender, "&cUsage: /boss spawn <mythicMob> <location>");
            else if (args[0].equalsIgnoreCase("give"))
                plugin.sendMessage(sender, "&cUsage: /boss give <mythicMob> <location>");
            else
                plugin.sendMessage(sender, msg);

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("spawn"))
                plugin.sendMessage(sender, "&cUsage: /boss spawn <mythicMob> <location>");
            else if (args[0].equalsIgnoreCase("give"))
                plugin.sendMessage(sender, "&cUsage: /boss give <mythicMob> <location>");
            else
                plugin.sendMessage(sender, msg);

            return true;
        } else if (args.length == 3) {
            if (!(args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("give"))) {
                plugin.sendMessage(sender, msg);
                return true;
            }

            Boss boss = plugin.getBossManager().getBoss(args[1]);

            if (boss == null) {
                plugin.sendMessage(sender, "&cBoss not found with name '" + args[1] + "'");
                return true;
            }

            if (!boss.isEnabled()) {
                plugin.sendMessage(sender, "&cThat boss is currently disabled.");
                return true;
            }

            String[] split = args[2].split(",");

            if (split.length <= 2) {
                plugin.sendMessage(sender, "&aThe location argument is too short! It needs more than 2 coordinates.");
                return true;
            }

            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(split[0]);
                y = Integer.parseInt(split[1]);
                z = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(sender,
                        "&cSomething went wrong parsing location string. Please check if arguments are correct.");
                return true;
            }

            World world = null;

            if (split.length == 4)
                world = Bukkit.getWorld(split[3]);

            if (world == null && sender instanceof Player)
                world = ((Player) sender).getWorld();

            if (world == null) {
                plugin.sendMessage(sender, "&cCould not parse location.");
                return true;
            }

            Location location = new Location(world, x, y, z);

            if (args[0].equalsIgnoreCase("spawn")) {
                boss.setSetLocation(true);
                boss.setSetLocationInstance(location);
                plugin.sendMessage(sender, "&aSuccessfully set spawn location for boss " + boss.getMythicMobName()
                        + "&a! X:" + x + " Y:" + y + " Z:" + z);

                Pair<Boolean, String> pair = plugin.getBossManager().spawnBoss(boss, true);

                plugin.sendMessage(sender, (pair.getKey() ? "&c" : "&a") + pair.getValue());
            } else { // /give
                if (!(sender instanceof Player)) {
                    plugin.sendMessage(sender, "&cOnly players can use this command.");
                    return true;
                }

                Player player = (Player) sender;
                boss.setSetLocation(true);
                boss.setSetLocationInstance(location);
                EggManager.giveMythicEgg(boss.getMythicMob(), player, 1);
                plugin.sendMessage(sender, "&aSuccessfully given a mythic boss egg of "
                        + boss.getMythicMobName() + " &ato be spawned in X:" + x + " Y: " + y + " Z:" + z);
            }

            return true;
        } else {
            plugin.sendMessage(sender, msg);
            return true;
        }
    }

    public void register() {
        PluginCommand pluginCommand = plugin.getCommand("boss");

        if (pluginCommand != null)
            pluginCommand.setExecutor(this);
    }
}
