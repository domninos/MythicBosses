package net.omni.mythicbosses.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.DropTable;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.EggManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileMaterial;
import net.omni.mythicbosses.MythicBosses;
import net.omni.mythicbosses.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        List<String> top3DamagerList = Arrays.stream(top3Damagers).filter(Objects::nonNull).
                map(Player::getName).collect(Collectors.toList());

        if (top3DamagerList.isEmpty())
            plugin.sendConsole("&cTop 3 Damagers not found.");

        ActiveMob activeMob = event.getMob();

        DropTable dropTable = boss.getMythicMob().getDropTable();

        for (Player damagers : top3Damagers) {
            if (damagers == null)
                plugin.sendConsole("&aDamager not found.");
            else {
                AbstractPlayer abstractPlayer = BukkitAdapter.adapt(damagers);

                dropTable.generate(new DropMetadata(activeMob, abstractPlayer)).give(abstractPlayer);
            }
        }

        AbstractPlayer abstractPlayer = BukkitAdapter.adapt(lastDamager);

        dropTable.generate(new DropMetadata(activeMob, abstractPlayer)).give(abstractPlayer);

        plugin.getMessagesUtil().getRewardedPlayers(boss.getMythicMobName().get(),
                top3DamagerList.get(0), top3DamagerList.get(1), top3DamagerList.get(2));
        plugin.getDamageHandler().clear(entity);
    }

    @EventHandler
    public void onPlayerSpawnEgg(PlayerInteractEvent event) {
        if (!(event.getAction().name().startsWith("RIGHT_CLICK"))) return;
        if (event.getItem() == null) return;

        if (event.getItem().getType() != VolatileMaterial.SPAWN_EGG) {
            ItemStack egg = event.getItem();

            if (egg.getItemMeta() == null || !(egg.getItemMeta() instanceof SpawnEggMeta))
                return;

            SpawnEggMeta meta = (SpawnEggMeta) egg.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && lore.get(2) != null) {
                    MythicMob mythicMob = EggManager.getMythicMobFromEgg(lore.get(2));

                    if (mythicMob == null) {
                        plugin.sendConsole("&cDebug! MythicMob not found from " + lore.get(2));
                        return;
                    }

                    Boss boss = plugin.getBossManager().getBoss(mythicMob.getDisplayName().toString());

                    if (boss == null) {
                        plugin.sendConsole("&cDebug! Boss not found from " + mythicMob.getDisplayName().toString());
                        return;
                    }

                    event.setCancelled(true);

                    // TODO add check -> summon a boss 30 mins before a scheduled boss spawn
                    //  if they are unable to  kill the boss within that time,
                    //  it should despawn and give the player their boss egg back

                    plugin.getBossManager().spawnBoss(boss, boss.isSetLocation());
                    plugin.sendConsole("&aSuccessfully spawned in boss from player");
                }
            }
        }
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}