package com.robomwm.aliasauditor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created on 10/6/2018.
 *
 * @author RoboMWM
 */
public class AliasAuditor extends JavaPlugin implements Listener
{
    private Storage storage;

    @Override
    public void onEnable()
    {
        File storageFile = new File(getDataFolder() + File.separator + "aliasauditor.db");
        try
        {
            storageFile.getParentFile().mkdirs();
            storageFile.createNewFile();
        }
        catch (Throwable rock)
        {
            rock.printStackTrace();
            getPluginLoader().disablePlugin(this);
            return;
        }
        storage = new Storage(this, storageFile);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {
        if (storage == null)
            return;
    }

    @EventHandler
    private void onConnect(AsyncPlayerPreLoginEvent event)
    {
        storage.login(event.getAddress(), event.getUniqueId(), event.getName());
    }
}
