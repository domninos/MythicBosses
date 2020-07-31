package net.omni.mythicbosses.boss;

import net.omni.mythicbosses.MythicBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BossManager {
    private final Set<Boss> bosses = new HashSet<>();
    private final MythicBosses plugin;

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

                plugin.sendConsole("Mythic mob found: " + bossInstance.getMythicMob().getDisplayName());
                bosses.add(bossInstance);
                plugin.sendConsole("&aSuccessfully loaded &b" + bossInstance.getName());
            }
        }
    }

    // TODO schedule

    public Boss getBoss(String name) {
        return bosses.stream().filter(boss -> boss.getName().equals(name)).findFirst().orElse(null);
    }

    public void spawnBoss(Boss boss) {

    }
}
