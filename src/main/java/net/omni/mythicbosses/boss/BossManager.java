package net.omni.mythicbosses.boss;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import net.md_5.bungee.api.ChatColor;
import net.omni.mythicbosses.MythicBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BossManager {
    private final Set<Boss> bosses = new HashSet<>();
    private final ConcurrentHashMap<Boss, Integer> schedule = new ConcurrentHashMap<>();
    private final MythicBosses plugin;
    private int pre_announcement_time;

    public BossManager(MythicBosses plugin) {
        this.plugin = plugin;

        loadBosses();
    }

    public void loadBosses() {
        bosses.clear();

        ConfigurationSection section = plugin.getConfigHandler().getConfig().getConfigurationSection("bosses");

        if (section != null) {
            for (String boss : section.getKeys(false)) {
                if (boss == null)
                    continue;

                boolean enabled = plugin.getConfigHandler().getConfig().getBoolean("bosses." + boss + ".enabled");

                String intervalString = plugin.getConfigHandler().getString("bosses." + boss + ".interval");
                int interval;

                try {
                    interval = Integer.parseInt(intervalString);
                } catch (NumberFormatException e) {
                    plugin.sendConsole("&cCould not load interval for " + boss
                            + ". " + intervalString + " is not a number!");
                    continue;
                }

                String spawnChanceString = plugin.getConfigHandler().getString("bosses." + boss + ".spawnChance");
                int spawnChance;

                try {
                    spawnChance = Integer.parseInt(spawnChanceString);
                } catch (NumberFormatException e) {
                    plugin.sendConsole("&cCould not load spawn chance for " + boss
                            + ". " + spawnChanceString + " is not a number!");
                    continue;
                }

                List<String> locationsList = plugin.getConfigHandler().getConfig().
                        getStringList("bosses." + boss + ".locations");

                List<Location> locations = new ArrayList<>();

                if (!locationsList.isEmpty()) {
                    for (String locString : locationsList) {
                        if (locString == null)
                            continue;

                        String[] splitLocation = locString.split(",");

                        if (splitLocation.length <= 2) {
                            plugin.sendConsole("&cCould not load " + boss
                                    + "'s location. Argument needs to be greater than 2");
                            continue;
                        }

                        String worldString = splitLocation[0]; // world
                        World worldInstance = Bukkit.getWorld(worldString);

                        if (worldInstance == null) {
                            plugin.sendConsole("&cCould not load '" + worldString + "' world of location for " + boss);
                            continue;
                        }

                        int x;
                        int y;
                        int z;

                        try {
                            x = Integer.parseInt(splitLocation[1]);
                            y = Integer.parseInt(splitLocation[2]);
                            z = Integer.parseInt(splitLocation[3]);
                        } catch (NumberFormatException e) {
                            plugin.sendConsole("&cSomething went wrong loading locations for " + boss);
                            continue;
                        }

                        locations.add(new Location(worldInstance, x, y, z));
                    }
                }

                Boss bossInstance = new Boss(enabled, boss, interval, locations, spawnChance);

                plugin.sendConsole("&aThe boss " + bossInstance.getName() + " is "
                        + (bossInstance.isEnabled() ? "enabled" : "disabled") + ".");

                if (bossInstance.getMythicMob() != null)
                    plugin.sendConsole("&aMythic mob found: " + bossInstance.getMythicMob().getDisplayName().toString());
                else {
                    plugin.sendConsole("&cMythic mob with the name '" + boss + "' not found.");
                    continue;
                }

                bosses.add(bossInstance);
                plugin.sendConsole("&aSuccessfully loaded &b" + bossInstance.getName());
            }
        }

        this.pre_announcement_time = plugin.getConfigHandler().getInt("pre_announcement_time");

        plugin.sendConsole("&aStarting schedulers.");
        startSchedulers();
    }

    // TODO schedule
    public void startSchedulers() {
        schedule.clear();
        bosses.stream().filter(Objects::nonNull).forEach(boss -> schedule.put(boss, boss.getInterval()));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Map.Entry<Boss, Integer> entry : schedule.entrySet()) {
                if (entry.getValue() == pre_announcement_time) {
                    plugin.broadcast(plugin.getMessagesUtil().
                            getPreAnnouncement(entry.getKey().getMythicMobName().get(), pre_announcement_time));
                }

                if (entry.getValue() <= 0) {
                    spawnBoss(entry.getKey(), false);
                    schedule.put(entry.getKey(), entry.getKey().getInterval());
                    continue;
                }

                entry.setValue(entry.getValue() - 1);
            }
        }, 20, 20);

        plugin.sendConsole("&aSuccessfully started scheduler!");
    }

    public Boss getBoss(String mythicMobName) {
        return bosses.stream().filter(boss -> ChatColor.stripColor(boss.getName()).
                equalsIgnoreCase(ChatColor.stripColor(mythicMobName))).findFirst().
                orElse(null);
    }

    public void flush() {
        bosses.clear();
    }

    public void spawnBoss(Boss boss, boolean player) {
        Location location = boss.getLocationToSpawn();

        if (player && boss.isSetLocation())
            location = boss.getSetLocationInstance();

        if (location == null) {
            plugin.sendConsole("&aTried to spawn " + boss.getMythicMobName().toString()
                    + " but location to spawn not found!");
            return;
        }

        try {
            MythicMobs.inst().getAPIHelper().spawnMythicMob(boss.getMythicMob(), location, 1);
            plugin.broadcast(plugin.getMessagesUtil().getBossSpawn(boss.getMythicMobName().get(),
                    location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        } catch (InvalidMobTypeException e) {
            e.printStackTrace();
        }
    }
}
