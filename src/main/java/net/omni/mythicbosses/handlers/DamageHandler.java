package net.omni.mythicbosses.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DamageHandler {
    private final ConcurrentHashMap<Entity, Map<Player, Double>> playerDamage = new ConcurrentHashMap<>();
    private final Map<Entity, Player> lastDamaged = new HashMap<>();

    public Player[] getTop3Damagers(Entity entity) {
        if (entity == null)
            return null;

        Player[] players = new Player[2];

        for (Map.Entry<Entity, Map<Player, Double>> entry : playerDamage.entrySet()) {
            Entity found = entry.getKey();

            if (found == null)
                continue;

            Map<Player, Double> playerMap = entry.getValue();
            Player[] sorted = playerMap.keySet().toArray(new Player[2]);

            Arrays.sort(sorted);

            for (int index = 0; index < sorted.length; index++) {
                if (sorted[index] != null)
                    players[index] = sorted[index];
            }
        }

        return players;
    }

    public void setLastDamager(Entity entity, Player player) {
        lastDamaged.put(entity, player);
    }

    public Player getLastDamager(Entity entity) {
        return lastDamaged.getOrDefault(entity, null);
    }

    public void addDamage(Entity entity, Player player, double damage) {
        Map<Player, Double> entityMap = new HashMap<>();

        if (hasDamaged(entity, player))
            playerDamage.get(entity).put(player, playerDamage.get(entity).get(player) + damage);
        else
            entityMap.put(player, damage);

        playerDamage.put(player, entityMap);
    }

    public void removeDamage(Entity entity, Player player) {
        if (!hasDamaged(entity, player))
            return;

        Map<Player, Double> playerMap = playerDamage.get(player);
        playerMap.remove(player);

        playerDamage.put(entity, playerMap);
    }

    public void clear(Entity entity) {
        if (playerDamage.containsKey(entity)) {
            playerDamage.get(entity).clear();
            playerDamage.remove(entity);
        }
    }

    public void flush() {
        playerDamage.clear();
    }

    public boolean hasDamaged(Entity entity, Player player) {
        return playerDamage.containsKey(entity) && playerDamage.get(entity).containsKey(player);
    }
}
