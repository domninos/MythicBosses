package net.omni.mythicbosses.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import net.omni.mythicbosses.MythicBosses;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
        // TODO manage rewards

        Player lastDamager = plugin.getDamageHandler().getLastDamager(entity);
        Player[] top3Damagers = plugin.getDamageHandler().getTop3Damagers(entity);


        plugin.getDamageHandler().clear(entity);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}