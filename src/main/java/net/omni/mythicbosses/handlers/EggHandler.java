package net.omni.mythicbosses.handlers;

import net.omni.mythicbosses.boss.Boss;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EggHandler {
    private final Map<Player, Boss> eggs = new HashMap<>();

    public Boss get(Player player) {
        return eggs.getOrDefault(player, null);
    }

    public Player getPlayer(Boss boss) {
        if (boss == null)
            return null;

        Player player = null;

        for (Map.Entry<Player, Boss> entry : eggs.entrySet()) {
            if (entry.getKey() == null) continue;

            if (entry.getValue().getName().equalsIgnoreCase(boss.getName())) {
                player = entry.getKey();
                break;
            }
        }

        return player;
    }

    public void set(Player player, Boss boss) {
        eggs.put(player, boss);
    }

    public boolean contains(Player player) {
        return eggs.containsKey(player);
    }

    public void remove(Player player) {
        eggs.remove(player);
    }

    public void flush() {
        eggs.clear();
    }
}