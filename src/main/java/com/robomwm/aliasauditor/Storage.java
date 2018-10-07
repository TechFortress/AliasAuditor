package com.robomwm.aliasauditor;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created on 10/6/2018.
 *
 * @author RoboMWM
 */
public class Storage
{
    private JavaPlugin plugin;
    private File sqlFile;

    public Storage(JavaPlugin plugin, File file)
    {
        this.plugin = plugin;
        this.sqlFile = file;
        initializeTables();
    }

    public void initializeTables()
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.toPath());
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists ips " +
                    "(`ip` TEXT NOT NULL," +
                    " `uuid` TEXT NOT NULL," +
                    " `lastLogin` INTEGER NOT NULL);");
            statement.executeUpdate("create table if not exists `names` " +
                    "(`name` TEXT NOT NULL," +
                    " `uuid` TEXT NOT NULL," +
                    " `firstSeen` INTEGER NOT NULL);");
            statement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void login(InetAddress address, UUID uuid, String name)
    {
        System.out.println(address.getHostAddress());
        plugin.getLogger().info(address.getHostAddress());
        int time = (int)TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        try
        {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.toPath());
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select ip, uuid from ips where uuid='" +
                    uuid.toString() + "' and ip='" + address.getHostAddress() + "'");
            //update if present
            if (result.next())
            {
                statement.executeUpdate("update ips set lastLogin='" + time + "' where uuid='" +
                        uuid.toString() + "' and ip='" + address.getHostAddress() + "'");
            }

            //else insert
            else
            {
                statement.executeUpdate("insert into ips (ip, uuid, lastLogin) values ('" +
                        address.getHostAddress() + "','" + uuid.toString() + "','" +
                        time + "');");
            }

            result.close();

            result = statement.executeQuery("select name, uuid from names where uuid='" +
                    uuid.toString() + "' and name='" + name + "'");
            //add if not present
            if (!result.next())
            {
                statement.executeUpdate("insert into names (name, uuid, firstSeen) values ('" +
                        name + "','" + uuid.toString() + "','" +
                        time + "');");
            }

            result.close();

            statement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
