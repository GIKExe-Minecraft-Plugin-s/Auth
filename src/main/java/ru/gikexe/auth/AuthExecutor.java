package ru.gikexe.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AuthExecutor implements CommandExecutor, TabCompleter {
	Auth plugin;

	public AuthExecutor(Auth plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
		if (!sender.isOp()) return true;
		if (args.length >= 4 && args[0].equals("set")) {
			if (args[1].equals("pass")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				((Map<String, Object>) plugin.players.get(player.getName())).replace("pass", args[3]);
				sender.sendMessage("Пароль изменён");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
		// sender.sendMessage(Arrays.toString(args));
		if (args.length <= 1) {
			return List.of("set", "get");
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
