package net.omni.mythicbosses.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

            Map<Player, Double> resultSorted = playerMap.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .limit(3)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            Player[] sorted = resultSorted.keySet().toArray(new Player[0]);

            for (int index = 0; index < sorted.length; index++) {
                if (sorted[index] != null) {
                    players[index] = sorted[index];
                    System.out.println("sorted: " + index + " = " + sorted[index].getName());
                }
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
        lastDamaged.clear();
    }

    public boolean hasDamaged(Entity entity, Player player) {
        return playerDamage.containsKey(entity) && playerDamage.get(entity).containsKey(player);
    }
}
