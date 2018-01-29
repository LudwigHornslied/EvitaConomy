package com.tistory.hornslied.evitaonline.economy.listeners;

import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.tistory.hornslied.evitaonline.economy.BalanceManager;
import com.tistory.hornslied.evitaonline.economy.Main;

public class JoinListener implements Listener {
	private final Main plugin;
	private BalanceManager balanceManager;

	public JoinListener(Main instance) {
		plugin = instance;
		balanceManager = BalanceManager.getInstance(plugin);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) throws IOException {
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		FileConfiguration uuidStorage = plugin.getUUIDStorage();
		String name = player.getName();

		if (uuidStorage.contains(uuid)) {
			System.out.println("unprofit");
			String oldName = uuidStorage.getString(uuid);

			System.out.println(balanceManager.getAmount(oldName));
			if (!oldName.equals(name)) {
				balanceManager.createPlayerAccount(name, balanceManager.getAmount(oldName));
				balanceManager.removeAccount(oldName);
				uuidStorage.set(uuid, name);
				uuidStorage.save(plugin.getUUIDStorageFile());
			}
		} else {
			System.out.println("profit");
			uuidStorage.set(uuid, name);
			uuidStorage.save(plugin.getUUIDStorageFile());

			if (!balanceManager.hasAccount(name))
				balanceManager.createPlayerAccount(name, 5000);
		}
	}
}
