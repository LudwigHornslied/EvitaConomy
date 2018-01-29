package com.tistory.hornslied.evitaonline.economy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Hashtable;

import com.tistory.hornslied.evitaonline.economy.db.DB;

public class BalanceManager {
	private static BalanceManager instance;
	private final Main plugin;
	private DB db;

	private Hashtable<String, Double> balances;

	private BalanceManager(Main instance) {
		plugin = instance;
		balances = new Hashtable<>();
		db = plugin.getDB();
	}

	public void initialize() {
		ResultSet rs = db.select("SELECT * FROM balances;");

		try {
			while (rs.next()) {
				balances.put(rs.getString("name"), rs.getDouble("balance"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static BalanceManager getInstance(Main plugin) {
		if (instance != null) {
			return instance;
		} else {
			instance = new BalanceManager(plugin);
			return instance;
		}
	}

	// Nullable
	public static BalanceManager getInstance() {
		return instance;
	}

	public void createAccount(String name, double balance) {
		balances.put(name, balance);
		db.query("INSERT INTO balances (name, balance) VALUES ('" + name + "', " + balance + ");");
	}

	public void createPlayerAccount(String name, double balance) {
		balances.put(name, balance);
		db.query("INSERT INTO balances (name, balance, isplayer) VALUES ('" + name + "', " + balance + ", 1);");
	}

	public void removeAccount(String name) {
		balances.remove(name);
		db.query("DELETE FROM balances WHERE name = '" + name + "';");
	}

	public boolean hasAccount(String name) {
		return balances.containsKey(name);
	}

	public boolean hasEnough(String name, double amount) {
		if (hasAccount(name) && balances.get(name) >= amount) {
			return true;
		} else {
			return false;
		}
	}

	public boolean deposit(String name, double amount) {
		if (hasAccount(name)) {
			balances.put(name, balances.get(name) + amount);
			updateDB(name);

			return true;
		} else {
			return false;
		}
	}

	public boolean withdraw(String name, double amount) {
		if (hasAccount(name) && hasEnough(name, amount)) {
			balances.put(name, balances.get(name) - amount);
			updateDB(name);

			return true;
		} else {
			return false;
		}
	}

	public double getAmount(String name) {
		return balances.get(name);
	}

	public boolean setBalance(String name, double amount) {
		if (hasAccount(name)) {
			balances.put(name, amount);
			updateDB(name);

			return true;
		} else {
			return false;
		}
	}

	public String format(double amount) {
		return new DecimalFormat("###,###", new DecimalFormatSymbols()).format(amount) + " 페론";
	}

	public ArrayList<String> getAccounts() {
		ArrayList<String> accounts = new ArrayList<>();
		balances.keySet().iterator().forEachRemaining(accounts::add);
		return accounts;
	}
	
	private void updateDB(String name) {
		db.query("UPDATE balances SET balance = " + balances.get(name) + " WHERE name = '" + name + "';");
	}
}
