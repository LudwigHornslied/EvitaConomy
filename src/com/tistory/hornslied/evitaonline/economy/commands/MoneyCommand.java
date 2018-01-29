package com.tistory.hornslied.evitaonline.economy.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tistory.hornslied.evitaonline.economy.BalanceManager;
import com.tistory.hornslied.evitaonline.economy.Main;
import com.tistory.hornslied.evitaonline.economy.Resources;
import com.tistory.hornslied.evitaonline.economy.db.DB;
import com.tistory.hornslied.evitaonline.economy.utils.ChatTools;

public class MoneyCommand implements CommandExecutor {
	private final Main plugin;
	private BalanceManager balanceManager;

	public MoneyCommand(Main instance) {
		plugin = instance;
		balanceManager = BalanceManager.getInstance();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {

				sender.sendMessage((balanceManager.hasAccount(sender.getName())
						? Resources.tagEconomy + "소지 금액: " + ChatColor.GREEN
								+ balanceManager.format(balanceManager.getAmount(sender.getName()))
						: Resources.tagEconomy + ChatColor.RED + "돈 정보가 존재하지 않습니다!"));
				return true;
			} else {
				sender.sendMessage(Resources.messageConsole);
				return false;
			}
		} else {
			switch (args[0].toLowerCase()) {
			case "보기":
			case "other":
				if (args.length > 1) {
					Player player = Bukkit.getServer().getPlayer(args[1]);

					if (player == null) {
						sender.sendMessage((balanceManager.hasAccount(args[1])
								? Resources.tagEconomy + ChatColor.GOLD + args[1] + ChatColor.WHITE + " 님의 소지 금액: "
										+ ChatColor.GREEN + balanceManager.format(balanceManager.getAmount(args[1]))
								: Resources.tagEconomy + ChatColor.RED + "돈 정보가 존재하지 않습니다!"));
					} else {
						sender.sendMessage((balanceManager.hasAccount(player.getName())
								? Resources.tagEconomy + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " 님의 소지 금액: "
										+ ChatColor.GREEN + balanceManager.format(balanceManager.getAmount(args[1]))
								: Resources.tagEconomy + ChatColor.RED + "돈 정보가 존재하지 않습니다!"));
					}
					return true;
				} else {
					sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /돈 보기 <플레이어>");
					return false;
				}
			case "보내기":
			case "pay":
			case "give":
				if (sender instanceof Player) {
					if (args.length > 2) {
						double amount;
						try {
							amount = Double.parseDouble(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /돈 보내기 <플레이어> <액수>");
							return false;
						}

						if (amount <= 0) {
							sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "액수는 양수여야 합니다.");
							return false;
						}

						Player player = Bukkit.getServer().getPlayer(args[1]);

						if (player == null) {
							if (balanceManager.hasAccount(args[1]) && !(args[1].startsWith("town-") || args[1].startsWith("nation-"))) {
								if (args[1].equals(sender.getName())) {
									sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "자신에게 돈을 보낼수 없습니다.");
									return false;
								}

								if (balanceManager.hasEnough(sender.getName(), amount)) {
									balanceManager.withdraw(sender.getName(), amount);
									balanceManager.deposit(args[1], amount);
									sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + args[1] + " 님에게 "
											+ balanceManager.format(amount) + " 을 송금하였습니다.");
									return true;
								} else {
									sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "잔액이 부족합니다.");
									return false;
								}
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "존재하지 않는 플레이어입니다. 오프라인이라면 대소문자를 구분하여 적어 주십시오.");
								return false;
							}
						} else {
							if (player.getName().equals(sender.getName())) {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "자신에게 돈을 보낼수 없습니다.");
								return false;
							}

							if (balanceManager.hasEnough(sender.getName(), amount)) {
								balanceManager.withdraw(sender.getName(), amount);
								balanceManager.deposit(player.getName(), amount);
								sender.sendMessage(Resources.tagEconomy + ChatColor.GREEN + player.getName() + " 님에게 "
										+ balanceManager.format(amount) + " 을 송금하였습니다.");
								player.sendMessage(Resources.tagEconomy + ChatColor.GREEN + sender.getName()
										+ " 님이 당신에게 " + balanceManager.format(amount) + " 을 송금하였습니다.");
								return true;
							} else {
								sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "잔액이 부족합니다.");
								return false;
							}
						}
					} else {
						sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /돈 보내기 <플레이어> <액수>");
						return false;
					}
				} else {
					sender.sendMessage(Resources.messageConsole);
					return false;
				}
			case "순위":
			case "top":
				String messages[];
				ResultSet rs = plugin.getDB().select("SELECT * FROM balances WHERE isplayer = 1 ORDER BY balance DESC;");
				try {
					if (args.length == 1 || Integer.parseInt(args[1]) == 1) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "돈 기록 불러오는 중...");
						if (DB.getResultSetSize(rs) == 0) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "신고 기록이 존재하지 않습니다.");
							return false;
						} else {

							try {
								if (DB.getResultSetSize(rs) < 8) {
									messages = new String[DB.getResultSetSize(rs)];
								} else {
									messages = new String[8];
								}

								int i = 0;
								while (rs.next() && i < 8) {
									messages[i] = ChatColor.GREEN + "(" + (i + 1) + ")" + " " + ChatColor.GRAY
											+ rs.getString("name") + " " + ChatColor.RED
											+ balanceManager.format(rs.getDouble("balance"));
									i++;
								}

								sender.sendMessage(ChatTools.formatTitle("돈 순위(1페이지)", ChatColor.GREEN));
								sender.sendMessage(ChatColor.GREEN + "(순위) " + ChatColor.GRAY + "(닉네임) " + ChatColor.RED
										+ "(소지 금액)");
								sender.sendMessage(messages);
								rs.close();
								return true;
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
						}
					} else {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "신고 기록 불러오는 중...");
						int index = Integer.parseInt(args[1]);
						if (DB.getResultSetSize(rs) < (index - 1) * 8 + 1) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당 페이지는 존재하지 않습니다.");
							return false;
						} else {
							try {
								if (DB.getResultSetSize(rs) < index * 8) {
									messages = new String[DB.getResultSetSize(rs) - (index - 1) * 8];
								} else {
									messages = new String[8];
								}

								rs.absolute((index - 1) * 8);

								int i = 0;
								while (rs.next() && i <= messages.length - 1) {
									messages[i] = ChatColor.GREEN + "(" + ((index -1) * 8 + i + 1) + ")" + " " + ChatColor.GRAY
											+ rs.getString("name") + " " + ChatColor.RED
											+ balanceManager.format(rs.getDouble("balance"));
									i++;
								}

								sender.sendMessage(ChatTools.formatTitle("돈 순위(" + index + "페이지)", ChatColor.GREEN));
								sender.sendMessage(ChatColor.GREEN + "(순위) " + ChatColor.GRAY + "(닉네임) " + ChatColor.RED
										+ "(소지 금액)");
								sender.sendMessage(messages);
								rs.close();
								return true;
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
						}
					}
				} catch (NumberFormatException e) {
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					sender.sendMessage(Resources.tagEconomy + ChatColor.RED + "명령어 사용 방법: /돈 순위 <페이지>");
					return false;
				}
			default:
				sender.sendMessage(ChatTools.formatTitle("돈 관련 명령어"));
				sender.sendMessage(ChatTools.formatCommand("", "/돈", "", "소지 금액을 확인합니다."));
				sender.sendMessage(ChatTools.formatCommand("", "/돈", "보기 <플레이어>", "다른 플레이어의 소지 금액을 봅니다."));
				sender.sendMessage(ChatTools.formatCommand("", "/돈", "보내기 <플레이어> <액수>", "다른 플레이어에게 돈을 보냅니다."));
				sender.sendMessage(ChatTools.formatCommand("", "/돈", "순위", "소지 금액이 제일 많은 플레이어를 보여줍니다."));
				return true;
			}
		}
	}
}
