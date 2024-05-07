package ru.gikexe.auth;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static ru.gikexe.auth.Data.login;
import static ru.gikexe.auth.Data.pass;

public class AuthExecutor implements CommandExecutor, TabCompleter {
	Component prefix = Auth.me.prefix;

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
		if (!sender.isOp()) return true;
		if (args.length >= 1 && args[0].equals("set")) {
			if (args.length >= 4 && args[1].equals("pass")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				pass(player, args[3]);
				sender.sendMessage(prefix.append(Component.text("Пароль изменён", WHITE)));
			}
		} else if (args.length >= 1 && args[0].equals("get")) {
			if (args.length >= 3 && args[1].equals("pass")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				sender.sendMessage(prefix.append(Component.text("Пароль: ", WHITE))
					.append(Component.text(String.valueOf(pass(player)), pass(player) == null ? RED : GREEN)));
			}
			else if (args.length >= 3 && args[1].equals("login")) {
				Player player = Bukkit.getServer().getPlayer(args[2]);
				if (player == null) return true;
				sender.sendMessage(prefix.append(Component.text("Логин: ", WHITE))
					.append(Component.text(login(player), login(player) ? GREEN : RED)));
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, String[] args) {
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
