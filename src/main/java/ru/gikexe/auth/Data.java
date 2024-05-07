package ru.gikexe.auth;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Data {
	static Config players = Auth.me.players;

	private static HashMap<String, Object> getPlayerData(String name) {
		if (!players.containsKey(name)) players.put(name, new HashMap<>(Map.of("login", false)));
		return (HashMap<String, Object>) players.get(name);
	}

	public static boolean login(String name) {
		return (boolean) getPlayerData(name).get("login");
	}

	public static boolean login(Player player) {
		return login(player.getName());
	}

	public static void login(String name, boolean value) {
		getPlayerData(name).put("login", value);
	}

	public static void login(Player player, boolean value) {
		login(player.getName(), value);
	}

	public static @Nullable String pass(String name) {
		return (String) getPlayerData(name).get("pass");
	}

	public static @Nullable String pass(Player player) {
		return pass(player.getName());
	}

	public static void pass(String name, String value) {
		getPlayerData(name).put("pass", value);
	}

	public static void pass(Player player, String value) {
		pass(player.getName(), value);
	}

}
