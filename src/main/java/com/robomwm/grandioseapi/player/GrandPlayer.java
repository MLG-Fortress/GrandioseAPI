package com.robomwm.grandioseapi.player;

import me.robomwm.usefulutil.UsefulUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

/**
 * Created on 3/8/2018.
 *
 * Primarily for holding "persistent metadata" for a Player
 * Which means just sharing a YamlConfiguration, lol.
 *
 * @author RoboMWM
 */
public class GrandPlayer
{
    private JavaPlugin plugin;
    private OfflinePlayer offlinePlayer;
    private YamlConfiguration yaml;

    GrandPlayer(JavaPlugin plugin, OfflinePlayer offlinePlayer)
    {
        this.plugin = plugin;
        this.offlinePlayer = offlinePlayer;
        yaml = UsefulUtil.loadOrCreateYamlFile(plugin, "grandPlayers" + File.separator + offlinePlayer.getUniqueId().toString());
    }

    public OfflinePlayer getOfflinePlayer()
    {
        return offlinePlayer;
    }

    public UUID getUUID()
    {
        return offlinePlayer.getUniqueId();
    }

    public YamlConfiguration getYaml()
    {
        return yaml;
    }

    public void saveYaml()
    {
        UsefulUtil.saveYamlFileDelayed(plugin, "grandPlayers" + File.separator + offlinePlayer.getUniqueId().toString(), yaml);
    }

    public boolean saveYamlNow()
    {
        return UsefulUtil.saveYamlFile(plugin, "grandPlayers" + File.separator + offlinePlayer.getUniqueId().toString(), yaml);
    }

    /**
     * Gets the color of the player's name
     * @return the stored color, or a random one determined from the player's UUID.
     */
    public ChatColor getNameColor()
    {
        String color = yaml.getString("nameColor");
        if (color == null)
        {
            //Get hash code of player's UUID
            int colorCode = offlinePlayer.getUniqueId().hashCode();
            //Ensure number is positive
            colorCode = Math.abs(colorCode);

            String[] acceptableColors = "2,3,4,5,6,9,a,b,c,d,e".split(",");
            //Divide hash code by length of acceptableColors, and use remainder
            //to determine which index to use (like a hashtable/map/whatever)
            colorCode = (colorCode % acceptableColors.length);
            return ChatColor.getByChar(acceptableColors[colorCode]);
        }
        return ChatColor.valueOf(color);
    }

    public void setNameColor(ChatColor color)
    {
        yaml.set("nameColor", color.name());
        saveYaml();
    }

    //Convenience (basically useless other than minimizing the amount of chars in a call so feel free to do this busywork if you wanna
    public Object get(String key)
    {
        return yaml.get(key);
    }

    public String getString(String key)
    {
        return yaml.getString(key);
    }
}
