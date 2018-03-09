package com.robomwm.grandioseapi.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 3/8/2018.
 *
 * @author RoboMWM
 */
public class GrandPlayerManager
{
    private JavaPlugin plugin;
    private long autoSaveInterval;
    private Map<OfflinePlayer, GrandPlayer> grandPlayers = new HashMap<>();

    public GrandPlayerManager(JavaPlugin plugin)
    {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection grandSection = config.getConfigurationSection("grandPlayer");
        if (grandSection == null)
            grandSection = config.createSection("grandPlayer");

        grandSection.addDefault("autoSaveInterval", 0);

        config.options().copyDefaults();

        autoSaveInterval = grandSection.getInt("autoSaveInterval");

        if (autoSaveInterval < 1)
            return;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (GrandPlayer grandPlayer : grandPlayers.values())
                    grandPlayer.saveYamlNow();
            }
        }.runTaskTimer(plugin, autoSaveInterval, autoSaveInterval);
    }

    public void onDisable()
    {
        for (GrandPlayer grandPlayer : grandPlayers.values())
            grandPlayer.saveYamlNow();
    }

    public GrandPlayer getGrandPlayer(OfflinePlayer player)
    {
        if (!grandPlayers.containsKey(player))
            grandPlayers.put(player, new GrandPlayer(plugin, player));
        return grandPlayers.get(player);
    }
}
