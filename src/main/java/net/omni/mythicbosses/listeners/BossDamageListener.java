package net.omni.mythicbosses.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.EggManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileMaterial;
import net.omni.mythicbosses.MythicBosses;
import net.omni.mythicbosses.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BossDamageListener implements Listener {
    private final MythicBosses plugin;

    public BossDamageListener(MythicBosses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity())) return;

        if (!(event.getDamager() instanceof Player && event.getDamager() instanceof Arrow)) return;

        Player damager = null;

        if (event.getDamager() instanceof Player)
            damager = (Player) event.getDamager();
        else {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player)
                damager = (Player) arrow.getShooter();
        }

        if (damager == null)
            return;

        Entity entity = event.getEntity();

        plugin.getDamageHandler().setLastDamager(entity, damager);
        plugin.getDamageHandler().addDamage(entity, damager, event.getDamage());
    }

    @EventHandler
    public void onMythicMobsDeath(MythicMobDeathEvent event) {
        Entity entity = event.getEntity();

        Boss boss = plugin.getBossManager().getBoss(event.getMobType().getDisplayName().toString());

        if (boss == null) {
            plugin.sendConsole("&aCould not find boss with name: " + event.getMobType().getDisplayName().toString());
            return;
        }

        Player lastDamager = plugin.getDamageHandler().getLastDamager(entity);

        if (lastDamager == null) {
            plugin.sendConsole("&aLast damager not found.");
            return;
        }

        plugin.broadcast(plugin.getMessagesUtil().getBossDeath(boss.getMythicMobName().get(), lastDamager.getName()));

        Player[] top3Damagers = plugin.getDamageHandler().getTop3Damagers(entity);
        List<Player> top3DamagerList = Arrays.stream(top3Damagers).filter(Objects::nonNull).collect(Collectors.toList());

        if (top3DamagerList.isEmpty())
            plugin.sendConsole("&cTop 3 Damagers not found.");

        boss.rewardPlayer(lastDamager, "%killer%");

        Player top1 = top3DamagerList.get(0);
        Player top2 = top3DamagerList.get(1);
        Player top3 = top3DamagerList.get(2);

        if (top1 != null)
            boss.rewardPlayer(top1, "%top_1_damager%");

        if (top2 != null)
            boss.rewardPlayer(top2, "%top_2_damager%");

        if (top3 != null)
            boss.rewardPlayer(top3, "%top_3_damager%");

        plugin.getMessagesUtil().getRewardedPlayers(boss.getMythicMobName().get(),
                top1 != null ? top1.getName() : null,
                top2 != null ? top2.getName() : null,
                top3 != null ? top3.getName() : null);

        plugin.getDamageHandler().clear(entity);
    }

    @EventHandler
    public void onPlayerSpawnEgg(PlayerInteractEvent event) {
        if (!(event.getAction().name().startsWith("RIGHT_CLICK"))) return;
        if (event.getItem() == null) return;
        if (event.getClickedBlock() == null) return;
        if (event.getItem().getType() != VolatileMaterial.SPAWN_EGG) return;

        Block block = event.getClickedBlock();
        ItemStack egg = event.getItem();

        if (egg.getItemMeta() == null || !(egg.getItemMeta() instanceof SpawnEggMeta))
            return;

        SpawnEggMeta meta = (SpawnEggMeta) egg.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();

            if (lore != null && lore.get(2) != null) {
                Player player = event.getPlayer();

                MythicMob mythicMob = EggManager.getMythicMobFromEgg(lore.get(2));

                if (mythicMob == null) {
                    plugin.sendConsole("&cDebug! MythicMob not found from " + lore.get(2));
                    plugin.sendMessage(player, "&cSomething went wrong spawning your MythicMob egg.");
                    return;
                }

                Boss boss = plugin.getBossManager().getBoss(mythicMob.getDisplayName().toString());

                if (boss == null) {
                    plugin.sendConsole("&cDebug! Boss not found from " + mythicMob.getDisplayName().toString());
                    plugin.sendMessage(player,
                            "&cSomething went wrong finding the boss form your MythicMob egg");
                    return;
                }

                event.setCancelled(true);

                if (boss.isToSpawn()) {
                    plugin.sendMessage(player, "&cThere is a scheduled spawn in "
                            + plugin.secToTime(plugin.getBossManager().getSchedule().get(boss)));
                    return;
                }

                if (boss.isSetLocation()) {
                    Location toSpawn = boss.getSetLocationInstance();

                    if (toSpawn.distance(block.getLocation()) > plugin.getBossManager().getDistanceToBlock()) {
                        plugin.sendMessage(player, "&cYou cannot spawn that boss here.");
                        return;
                    }
                }

                plugin.getBossManager().spawnBoss(boss, boss.isSetLocation());
                plugin.getEggHandler().set(player, boss);
                plugin.sendConsole("&aSuccessfully spawned in boss from player");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getEggHandler().remove(event.getPlayer());
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}