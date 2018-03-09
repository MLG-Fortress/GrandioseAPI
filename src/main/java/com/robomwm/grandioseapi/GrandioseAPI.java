package com.robomwm.grandioseapi;

import com.robomwm.grandioseapi.player.GrandPlayerManager;
import me.robomwm.usefulutil.UsefulUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 3/8/2018.
 *
 * Welcome to the GrandioseAPI, which is mostly a collection of persistent wrappers for plugins to share.
 *
 * Feel free to PR whatever you want.
 * However, if your contribution doesn't require any persistence, you might be better off contributing to UsefulUtil.
 * UsefulUtil is currently located at https://github.com/RoboMWM/UsefulUtil
 *
 * @author RoboMWM
 */
public class GrandioseAPI extends JavaPlugin
{
    private GrandPlayerManager grandPlayerManager;

    public GrandPlayerManager getGrandPlayerManager()
    {
        return grandPlayerManager;
    }

    public void onEnable()
    {
        saveConfig(); //Ensure directory is created

        //Add your classes here
        grandPlayerManager = new GrandPlayerManager(this);
    }

    public void onDisable()
    {
        grandPlayerManager.onDisable();
    }
}
