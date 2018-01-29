package com.tistory.hornslied.evitaonline.economy;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.tistory.hornslied.evitaonline.economy.commands.AdminCommand;
import com.tistory.hornslied.evitaonline.economy.commands.MoneyCommand;
import com.tistory.hornslied.evitaonline.economy.db.DB;
import com.tistory.hornslied.evitaonline.economy.listeners.JoinListener;
import com.tistory.hornslied.evitaonline.economy.vault.VaultEconomy;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	private FileConfiguration config;
	private FileConfiguration uuidStorage;
	private File uuidStorageFile;

	private DB db;

	@Override
	public void onEnable() {
		loadConfig();
		try {
			setUpDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BalanceManager.getInstance(this).initialize();
		registerEconomy();
		registerEvents();
		initCommands();
	}

	private void loadConfig() {
		uuidStorageFile = new File(getDataFolder(), "uuid.yml");
				
		if (!(new File(getDataFolder(), "config.yml").exists()))
			saveDefaultConfig();
		config = getConfig();

		if (!uuidStorageFile.exists())
			saveResource("uuid.yml", false);

		uuidStorage = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "uuid.yml"));
	}

	private void setUpDB() throws SQLException {
		String dburl;

		if (config.getBoolean("db.useSSL")) {
			dburl = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getString("db.port") + "/"
					+ config.getString("db.dbname");
		} else {
			dburl = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getString("db.port") + "/"
					+ config.getString("db.dbname") + "?useSSL=false";
		}

		db = new DB(dburl, config.getString("db.user"), config.getString("db.password"));

		DatabaseMetaData md = db.getConnection().getMetaData();
		if (!(md.getTables(null, null, "balances", null).next())) {
			db.query(
					"CREATE TABLE balances (id int NOT NULL PRIMARY KEY AUTO_INCREMENT, name varchar(255) NOT NULL, balance double NOT NULL DEFAULT 5000, isplayer tinyint(1) NOT NULL DEFAULT 0);");
		}
	}
	
	private void registerEconomy() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			final ServicesManager sm = getServer().getServicesManager();
            sm.register(Economy.class, new VaultEconomy(this), this, ServicePriority.Highest);
		}
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new JoinListener(this), this);
	}
	
	private void initCommands() {
		getCommand("money").setExecutor(new MoneyCommand(this));
		getCommand("economy").setExecutor(new AdminCommand());
	}

	public DB getDB() {
		return db;
	}
	
	public FileConfiguration getUUIDStorage() {
		return uuidStorage;
	}
	
	public File getUUIDStorageFile() {
		return uuidStorageFile;
	}
}
