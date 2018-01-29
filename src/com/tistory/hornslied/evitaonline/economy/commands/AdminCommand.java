package com.tistory.hornslied.evitaonline.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tistory.hornslied.evitaonline.economy.BalanceManager;
import com.tistory.hornslied.evitaonline.economy.Resources;
import com.tistory.hornslied.evitaonline.economy.utils.ChatTools;

public class AdminCommand implements CommandExecutor {
	private BalanceManager balanceManager;

	public AdminCommand() {
		balanceManager = BalanceManager.getInstance();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("evita.admin")) {
			if (args.length == 0) {
				sender.sendMessage(ChatTools.formatTitle("경제 관련 명령어"));
				sender.sendMessage(ChatTools.formatCommand("관리자", "/경제", "설정 <플레이어> <액수>", "플레이어의 소지 금액을 설정합니다."));
				sender.sendMessage(ChatTools.formatCommand("관리자", "/경제", "입금 <플레이어> <액수>", "플레이어에게 돈을 입금합니다."));
				sender.sendMessage(ChatTools.formatCommand("관리자", "/경제", "출금 <플레이어> <액수>", "플레이어의 돈을 출금합니다."));
				return true;
			} else {
				switch (args[0].toLowerCase()) {
				case "설정":
				case "set":
					if (args.length > 2) {
						double amount;
						try {
							amount = Double.parseDouble(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 설정 <플레이어> <액수>");
							return false;
						}

						if (amount <= 0) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "액수는 양수여야 합니다.");
							return false;
						}

						Player player = Bukkit.getServer().getPlayer(args[1]);

						if (player == null) {
							if (balanceManager.hasAccount(args[1])) {
								balanceManager.setBalance(args[1], amount);
								sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1] + " 의 돈을 "
										+ balanceManager.format(amount) + " 으로 설정하였습니다.");
								return true;
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "존재하지 않는 플레이어입니다.");
								return false;
							}
						} else {
							balanceManager.setBalance(player.getName(), amount);
							sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1] + " 의 돈을 "
									+ balanceManager.format(amount) + " 으로 설정하였습니다.");
							player.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "관리자가 당신의 돈을 "
									+ balanceManager.format(amount) + " 으로 설정하였습니다.");
							return true;
						}
					} else {
						sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 설정 <플레이어> <액수>");
						return false;
					}
				case "입금":
				case "deposit":
					if (args.length > 2) {
						double amount;
						try {
							amount = Double.parseDouble(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 입금 <플레이어> <액수>");
							return false;
						}

						if (amount <= 0) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "액수는 양수여야 합니다.");
							return false;
						}

						Player player = Bukkit.getServer().getPlayer(args[1]);

						if (player == null) {
							if (balanceManager.hasAccount(args[1])) {
								balanceManager.deposit(args[1], amount);
								sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1] + " 에게 "
										+ balanceManager.format(amount) + " 을 입금하였습니다.");
								return true;
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "존재하지 않는 플레이어입니다.");
								return false;
							}
						} else {
							balanceManager.deposit(player.getName(), amount);
							sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1] + " 에게 "
									+ balanceManager.format(amount) + " 을 입금하였습니다.");
							player.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "관리자가 당신에게 "
									+ balanceManager.format(amount) + " 을 입금하였습니다..");
							return true;
						}
					} else {
						sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 입금 <플레이어> <액수>");
						return false;
					}
				case "출금":
				case "withdraw":
					if (args.length > 2) {
						double amount;
						try {
							amount = Double.parseDouble(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 출금 <플레이어> <액수>");
							return false;
						}

						if (amount <= 0) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "액수는 양수여야 합니다.");
							return false;
						}

						Player player = Bukkit.getServer().getPlayer(args[1]);

						if (player == null) {
							if (balanceManager.hasAccount(args[1])) {
								if (balanceManager.hasEnough(args[1], amount)) {
									balanceManager.withdraw(args[1], amount);
									sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1]
											+ " 에게서 " + balanceManager.format(amount) + " 을 출금하였습니다.");
									return true;
								} else {
									sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "해당 플레이어의 잔액이 부족합니다.");
									return false;
								}
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "존재하지 않는 플레이어입니다.");
								return false;
							}
						} else {
							if (balanceManager.hasEnough(player.getName(), amount)) {
								balanceManager.withdraw(player.getName(), amount);
								sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "플레이어 " + args[1] + " 에게서 "
										+ balanceManager.format(amount) + " 을 출금하였습니다.");
								player.sendMessage(Resources.tagEconomy + ChatColor.GREEN + "관리자가 당신에게서 "
										+ balanceManager.format(amount) + " 을 출금하였습니다..");
								return true;
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "해당 플레이어의 잔액이 부족합니다.");
								return false;
							}
						}
					} else {
						sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /경제 출금 <플레이어> <액수>");
						return false;
					}
				default:
					sender.sendMessage(ChatTools.formatTitle("경제 관련 명령어"));
					sender.sendMessage(ChatTools.formatCommand("어드민", "/경제", "설정 <플레이어> <액수>", "플레이어의 소지 금액을 설정합니다."));
					sender.sendMessage(ChatTools.formatCommand("어드민", "/경제", "입금 <플레이어> <액수>", "플레이어에게 돈을 입금합니다."));
					sender.sendMessage(ChatTools.formatCommand("어드민", "/경제", "출금 <플레이어> <액수>", "플레이어의 돈을 출금합니다."));
					return true;
				}
			}
		} else {
			sender.sendMessage(Resources.messagePermission);
			return false;
		}
	}

}
