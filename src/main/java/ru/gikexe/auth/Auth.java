package ru.gikexe.auth;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static ru.gikexe.auth.Data.login;

public final class Auth extends JavaPlugin {
	static Auth me;
	public Server server;
	public PluginManager manager;

	Component prefix = Component.text(" -> ", GRAY);
	AuthListener listener;
	AuthExecutor executor;

	public Config players;

	public void onEnable() {
		me = this;
		server = getServer();
		manager = server.getPluginManager();

		players = new Config(this, "players.yml");
		for (Player player : server.getOnlinePlayers()) login(player, true);

		listener = new AuthListener();
		manager.registerEvents(listener, this);

		executor = new AuthExecutor();
		PluginCommand command = getCommand("auth");
		if (command != null) {
			command.setExecutor(executor);
			command.setTabCompleter(executor);
		}
	}

	public void onDisable() {
		for (String name : players.keySet()) {
			if (!login(name)) {
				Player player = server.getPlayer(name);
				if (player == null) continue;
				listener.playerDataMap.remove(name).back();
				player.kick(
					Component.text("не вошедшие покидают сервер при перезагрузке", RED),
					PlayerKickEvent.Cause.RESTART_COMMAND);
			}
			login(name, false);
		}
		players.save();
	}
}
