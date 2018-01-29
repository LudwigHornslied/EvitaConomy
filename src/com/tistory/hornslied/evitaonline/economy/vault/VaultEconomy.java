package com.tistory.hornslied.evitaonline.economy.vault;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.tistory.hornslied.evitaonline.economy.BalanceManager;
import com.tistory.hornslied.evitaonline.economy.Main;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class VaultEconomy implements Economy {
	private final BalanceManager bm = BalanceManager.getInstance();
	private Plugin plugin = null;
	private Main evitaconomy = null;
	
	public VaultEconomy(Plugin plugin) {
		this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (evitaconomy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("Essentials");
            if (ec != null && ec.isEnabled()) {
            	evitaconomy = (Main) ec;
            	Bukkit.getLogger().info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "EvitaConomy"));
            }
        }
	}
	
	public class EconomyServerListener implements Listener {
		VaultEconomy economy = null;

        public EconomyServerListener(VaultEconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.evitaconomy == null) {
                Plugin ec = event.getPlugin();

                if (ec.getDescription().getName().equals("EvitaConomy")) {
                    economy.evitaconomy = (Main) ec;
                    Bukkit.getLogger().info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "EvitaConomy"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.evitaconomy != null) {
                if (event.getPlugin().getDescription().getName().equals("EvitaConomy")) {
                    economy.evitaconomy = null;
                    Bukkit.getLogger().info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), "EvitaConomy"));
                }
            }
        }
    }

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public boolean createPlayerAccount(String name) {
		if (bm.hasAccount(name)) {
			return false;
		} else {
			bm.createAccount(name, 0);
			return true;
		}
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		if (bm.hasAccount(player.getName())) {
			return false;
		} else {
			bm.createPlayerAccount(player.getName(), 5000);
			return true;
		}
	}

	@Override
	public boolean createPlayerAccount(String name, String world) {
		if (bm.hasAccount(name)) {
			return false;
		} else {
			bm.createPlayerAccount(name, 0);
			return true;
		}
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String world) {
		if (bm.hasAccount(player.getName())) {
			return false;
		} else {
			bm.createPlayerAccount(player.getName(), 5000);
			return true;
		}
	}

	@Override
	public String currencyNamePlural() {
		return "페론";
	}

	@Override
	public String currencyNameSingular() {
		return "페론";
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse depositPlayer(String name, double amount) {
		if (amount < 0) return new EconomyResponse(0, getBalance(name), ResponseType.FAILURE, "Cannot desposit negative funds.");
		
		if(bm.deposit(name, amount)) {
			return new EconomyResponse(amount, getBalance(name), ResponseType.SUCCESS, "Successed.");
		} else {
			return new EconomyResponse(0, getBalance(name), ResponseType.FAILURE, "Non exist account.");
		}
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(String name, String world, double amount) {
		return depositPlayer(name, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public String format(double amount) {
		return bm.format(amount);
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public double getBalance(String name) {
		return bm.getAmount(name);
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return bm.getAmount(player.getName());
	}

	@Override
	public double getBalance(String name, String world) {
		return bm.getAmount(name);
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return bm.getAmount(player.getName());
	}

	@Override
	public List<String> getBanks() {
		return bm.getAccounts();
	}

	@Override
	public String getName() {
		return "EvitaConomy";
	}

	@Override
	public boolean has(String name, double amount) {
		return bm.hasEnough(name, amount);
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return bm.hasEnough(player.getName(), amount);
	}

	@Override
	public boolean has(String name, String world, double amount) {
		return bm.hasEnough(name, amount);
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		return bm.hasEnough(player.getName(), amount);
	}

	@Override
	public boolean hasAccount(String name) {
		return bm.hasAccount(name);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return bm.hasAccount(player.getName());
	}

	@Override
	public boolean hasAccount(String name, String world) {
		return bm.hasAccount(name);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		return bm.hasAccount(player.getName());
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public EconomyResponse isBankMember(String name, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Does not support it.");
	}

	@Override
	public boolean isEnabled() {
		if (evitaconomy == null) {
            return false;
        } else {
            return evitaconomy.isEnabled();
        }
	}

	@Override
	public EconomyResponse withdrawPlayer(String name, double amount) {
		if (amount < 0) return new EconomyResponse(0, getBalance(name), ResponseType.FAILURE, "Cannot withdraw negative funds.");
		
		if(bm.withdraw(name, amount)) {
			return new EconomyResponse(amount, getBalance(name), ResponseType.SUCCESS, "Successed.");
		} else {
			return new EconomyResponse(0, getBalance(name), ResponseType.FAILURE, "Non exist account or not enough balance in account.");
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String name, String world, double amount) {
		return withdrawPlayer(name, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

}
