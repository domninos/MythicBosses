package net.omni.mythicbosses.boss;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Boss {
    private final MythicMob mythicMob;
    private final String name;
    private final int interval;
    private final List<Location> locations;
    private final int spawnChance;
    private boolean enabled;

    public Boss(boolean enabled, String name, int interval, List<Location> locations, int spawnChance) {
        this.mythicMob = MythicMobs.inst().getMobManager().getMythicMob(name);
        this.interval = interval;
        this.locations = locations;
        this.spawnChance = spawnChance;
        this.enabled = enabled;
        this.name = mythicMob.getDisplayName().get();

    }

    public String getName() {
        return name;
    }

    public int getInterval() {
        return interval;
    }

    public List<Location> getLocations() {
        return locations;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
