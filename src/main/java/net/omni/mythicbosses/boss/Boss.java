package net.omni.mythicbosses.boss;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import net.omni.mythicbosses.MythicBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Boss {
    private final MythicMob mythicMob;
    private final PlaceholderString mythicMobName;
    private final String name;
    private final int interval;
    private final List<Location> locations;
    private final List<String> rewards;
    private final int spawnChance;
    private final MythicBosses plugin;
    private boolean enabled;
    private boolean setLocation;
    private Location setLocationInstance;
    private boolean toSpawn;
    private ActiveMob activeMob;

    public Boss(MythicBosses plugin, boolean enabled, String name, int interval, List<Location> locations,
                List<String> rewards, int spawnChance) {
        this.plugin = plugin;
        this.name = name;
        this.mythicMob = MythicMobs.inst().getMobManager().getMythicMob(name);
        this.interval = interval;
        this.locations = locations;
        this.rewards = rewards;
        this.spawnChance = spawnChance;
        this.mythicMobName = mythicMob.getDisplayName();
        setEnabled(enabled);
        setSetLocation(false);
        setSetLocationInstance(null);
        setActiveMob(null);
    }

    public void rewardPlayer(Player player, String toReplace) {
        player.sendMessage("REWARDING U");

        for (String reward : rewards) {
            if (reward.contains("{")) {
                reward = reward.replace("{", "").replace("}", "").
                        replace(toReplace, player.getName());

                // command
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward);
                continue;
            }

            String[] split = reward.split(",");
            Material material = Material.getMaterial(split[0].toUpperCase());

            if (material == null) {
                plugin.sendConsole("&cCould not reward " + player.getName() + " because "
                        + split[0].toUpperCase() + " is not a valid material type!");
                continue;
            }

            ItemStack itemReward = new ItemStack(material, 1);

            if (split.length > 1) {
                int amount;

                try {
                    amount = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    plugin.sendConsole("&c" + split[1] + " is not a valid number!");
                    continue;
                }

                itemReward.setAmount(amount);
            }

            player.getInventory().addItem(itemReward);
        }
    }

    public PlaceholderString getMythicMobName() {
        return mythicMobName;
    }

    public int getInterval() {
        return interval;
    }

    public Location getLocationToSpawn() {
        if (locations.isEmpty())
            return null;

        if (locations.size() == 1)
            return locations.get(0);

        return locations.get(ThreadLocalRandom.current().nextInt(0, locations.size()));
    }

    public int getSpawnChance() {
        return spawnChance;
    }

    public MythicMob getMythicMob() {
        return mythicMob;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSetLocation() {
        return this.setLocation && getSetLocationInstance() != null;
    }

    public void setSetLocation(boolean setLocation) {
        this.setLocation = setLocation;
    }

    public Location getSetLocationInstance() {
        return this.setLocationInstance;
    }

    public void setSetLocationInstance(Location location) {
        this.setLocationInstance = location;
    }

    public boolean isToSpawn() {
        return toSpawn;
    }

    public void setToSpawn(boolean toSpawn) {
        this.toSpawn = toSpawn;
    }

    public ActiveMob getActiveMob() {
        return activeMob;
    }

    public void setActiveMob(ActiveMob activeMob) {
        this.activeMob = activeMob;
    }
}
