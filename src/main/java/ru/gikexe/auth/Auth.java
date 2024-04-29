package ru.gikexe.auth;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class Auth extends JavaPlugin {
	public Server server;
	public PluginManager manager;
	public Config players;
	public AuthListener listener;
	public AuthExecutor executor;

	public void onEnable() {
		server = getServer();
		manager = server.getPluginManager();

		listener = new AuthListener(this);
		manager.registerEvents(listener, this);
		PluginCommand command = getCommand("auth");
		if (command != null) {
			executor = new AuthExecutor(this);
			command.setExecutor(executor);
			command.setTabCompleter(executor);
		}

		players = new Config(this, "players.yml");
		for (Player player : server.getOnlinePlayers()) {
			((Map<String, Object>) players.get(player.getName())).replace("login", true);
		}
	}

	public void onDisable() {
		if (players != null) {
			for (String name : players.keySet()) {
				Map<String, Object> data = listener.getData(name);
				if ((boolean) data.get("login")) {
					data.replace("login", false);
				} else {
					Player player = server.getPlayer(name);
					if (player == null) continue;
					player.clearActivePotionEffects();
					player.addPotionEffects(listener.lastEffect.remove(player.getName()));
					player.teleport(listener.lastLocation.remove(player.getName()));
					player.kick(Component.text("не вошедшие покидают сервер при перезагрузке", RED), PlayerKickEvent.Cause.RESTART_COMMAND);
				}
			}
			players.save();
		}

	}
}
