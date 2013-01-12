package net.battlenexus.bukkit.verify;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.battlenexus.bukkit.verify.api.Api;
import net.battlenexus.bukkit.verify.listeners.BattleCommands;
import net.battlenexus.bukkit.verify.sql.SqlClass;

import org.apache.commons.lang.WordUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleVerify extends JavaPlugin {

    SqlClass sql;
    boolean connected = false;
    
    @Override
    public void onEnable(){
        File config = new File(this.getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveDefaultConfig();
            getLogger()
                    .info("Configuration file created, please edit it before attempting to load this plugin");
            getServer().getPluginManager().disablePlugin(this);
        }
        getConfig();

        try {
            setupSQL();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        if (sql.connect(getConfig().getString("sql.host"), getConfig()
                .getString("sql.port"), getConfig().getString("sql.database"),
                getConfig().getString("sql.username"),
                getConfig().getString("sql.password"))) {
            connected = true;
            sql.prefix = getConfig().getString("sql.prefix");
            if (getConfig().getBoolean("sql.auto-create")) {
                setupMysql();
                getConfig().set("sql.auto-create", false);
                saveConfig();
            }
            getLogger().info("Connected to sql server!");
        } else {
            getLogger().info("Couldn't connect to mysql");
            getLogger().info("Plugin not loaded");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Api.sql = sql;
        
        BattleCommands command = new BattleCommands(sql);
        getCommand("bv").setExecutor(command);
    }
    
    @Override
    public void onDisable(){
        if (connected) {
            sql.disconnect();
            if (!sql.isConnected())
                getLogger().info("Disconnected from mysql");
            else
                getLogger().info(
                        "There were errors trying to disconnect from mysql");
        }        
    }
    
    private void setupSQL() throws ClassNotFoundException,
        NoSuchMethodException, SecurityException, InstantiationException,
        IllegalAccessException, IllegalArgumentException,
        InvocationTargetException {
    Class<?> class_ = Class.forName("net.battlenexus.bukkit.verify.sql.drivers."
            + WordUtils.capitalizeFully(getConfig().getString("sql.driver",
                    "Mysql")));
    Class<? extends SqlClass> runClass = class_.asSubclass(SqlClass.class);
    Constructor<? extends SqlClass> constructor = runClass.getConstructor();
    sql = constructor.newInstance();
    }
    
    private void setupMysql() {
        getLogger().info("Creating mysql tables...");
        sql.build("CREATE TABLE IF NOT EXISTS " + sql.prefix + "verify ("
                + "  economy_key varchar(20) NOT NULL,"
                + "  user_id int(11) NOT NULL,"
                + "  balance decimal(19,2) NOT NULL,"
                + "  PRIMARY KEY (economy_key,user_id)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        sql.executeUpdate();
        getLogger().info("Mysql tables created successfully...");
    }
}
