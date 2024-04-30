package ru.gikexe.auth;

import org.bukkit.entity.Player;

import java.util.Map;

public class Data {
	Auth plugin = Auth.me;
	Config players = plugin.players;
	Map<String, Object> data;

	private void check(String name) {
		if (!players.containsKey(name)) players.put(name, Map.of("login", false, "pass", null));
	}

	private void init(String name) {
		check(name);
		data = (Map<String, Object>) players.get(name);
	}

	public Data(String name) {init(name);}
	public Data(Player player) {init(player.getName());}

	public boolean login() {
		return (boolean) data.get("login");
	}

	public void login(boolean value) {
		data.replace("login", value);
	}

	public String pass() {
		return (String) data.get("pass");
	}

	public void pass(String value) {
		data.replace("pass", value);
	}
}
