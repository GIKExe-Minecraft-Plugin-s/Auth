package ru.gikexe.auth;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerData {
	Player player;
	private final Location location;
	private final GameMode gamemode;

	public PlayerData(Player player) {
		this.player = player;
		location = player.getLocation();
		gamemode = player.getGameMode();
	}

	public void back() {
		player.teleport(location);
		player.setGameMode(gamemode);
	}
}
