package ru.gikexe.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuthExecutor implements CommandExecutor, TabCompleter {
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
		if (!sender.isOp()) return true;
		if (args.length >= 1 && args[0].equals("set")) {
			if (args.length >= 4 && args[1].equals("pass")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				Data data = new Data(player);
				data.pass(args[3]);
				sender.sendMessage("Пароль изменён");
			}
		} else if (args.length >= 1 && args[0].equals("get")) {
			if (args.length >= 3 && args[1].equals("pass")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				Data data = new Data(player);
				sender.sendMessage("Пароль: "+data.pass());
			}
			else if (args.length >= 3 && args[1].equals("login")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				Data data = new Data(player);
				sender.sendMessage("Логин: "+data.login());
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
		if (args.length <= 1) {
			return List.of("set", "get", "save");
		} else if (args.length == 2) {
			if (args[0].equals("set") || args[0].equals("get")) {
				return List.of("pass", "login");
			} else {
				return List.of();
			}
		} else if (args.length == 3) {
			return null;
		}
		return List.of();
	}
}
