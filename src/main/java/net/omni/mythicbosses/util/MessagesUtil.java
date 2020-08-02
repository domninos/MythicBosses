package net.omni.mythicbosses.util;

import net.omni.mythicbosses.MythicBosses;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class MessagesUtil {

    private final MythicBosses plugin;
    private String prefix;
    private String pre_announcement;
    private String bossSpawn;
    private String bossDeath;
    private String rewardedPlayers;

    public MessagesUtil(MythicBosses plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        this.prefix = plugin.translate(plugin.getMessagesHandler().getString("prefix"));

        List<String> preAnnounceMsg = plugin.getMessagesHandler().getConfig().getStringList("pre_announcement");
        this.pre_announcement = plugin.translate(StringUtils.join(preAnnounceMsg, "\n"));

        List<String> bossSpawnMsg = plugin.getMessagesHandler().getConfig().getStringList("bossSpawn");
        this.bossSpawn = plugin.translate(StringUtils.join(bossSpawnMsg, "\n"));

        List<String> bossDeathMsg = plugin.getMessagesHandler().getConfig().getStringList("bossDeath");
        this.bossDeath = plugin.translate(StringUtils.join(bossDeathMsg, "\n"));

        List<String> rewardedPlayersMsg = plugin.getMessagesHandler().getConfig().getStringList("rewardedPlayers");
        this.rewardedPlayers = plugin.translate(StringUtils.join(rewardedPlayersMsg, "\n"));
    }

    public String getPreAnnouncement(String bossName, int time) {
        return pre_announcement.replace("%boss%", bossName).replace("%time%", String.valueOf(time));
    }

    public String getBossSpawn(String bossName, int x, int y, int z) {
        return bossSpawn.replace("%boss%", bossName).
                replace("%x%", String.valueOf(x)).
                replace("%y%", String.valueOf(y)).
                replace("%z%", String.valueOf(z));
    }

    public String getBossDeath(String bossName, String killer) {
        return bossDeath.replace("%boss%", bossName).replace("%killer%", killer);
    }

    public String getRewardedPlayers(String bossName, String top_1_damager, String top_2_damager, String top_3_damager) {
        return rewardedPlayers.replace("%boss%", bossName).
                replace("%top_1_damager%", top_1_damager == null ? "None" : top_1_damager).
                replace("%top_2_damager%", top_2_damager == null ? "None" : top_2_damager).
                replace("%top_3_damager%", top_3_damager == null ? "None" : top_3_damager);
    }

    public String getPrefix() {
        return prefix;
    }
}