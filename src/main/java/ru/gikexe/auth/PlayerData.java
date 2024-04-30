package ru.gikexe.auth;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerData {
	Player player;
	Location lastLocation;
	Collection<PotionEffect> lastEffect;
//	PlayerInventory lastInventory;

	public PlayerData(Player player) {
		this.player = player;
		lastLocation = player.getLocation();
		lastEffect = player.getActivePotionEffects();
//		lastInventory = player.getInventory();
	}

	public void back() {
		player.clearActivePotionEffects();
		player.addPotionEffects(lastEffect);
		player.teleport(lastLocation);
	}
}
