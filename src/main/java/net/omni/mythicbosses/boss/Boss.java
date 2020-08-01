package net.omni.mythicbosses.boss;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Boss {
    private final MythicMob mythicMob;
    private final PlaceholderString mythicMobName;
    private final String name;
    private final int interval;
    private final List<Location> locations;
    private final int spawnChance;
    private boolean enabled;
    private boolean setLocation;
    private Location setLocationInstance;

    public Boss(boolean enabled, String name, int interval, List<Location> locations, int spawnChance) {
        this.name = name;
        this.mythicMob = MythicMobs.inst().getMobManager().getMythicMob(name);
        this.interval = interval;
        this.locations = locations;
        this.spawnChance = spawnChance;
        this.mythicMobName = mythicMob.getDisplayName();
        setEnabled(enabled);
        setSetLocation(false);
        setSetLocationInstance(null);
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
}
