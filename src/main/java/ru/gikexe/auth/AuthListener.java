package ru.gikexe.auth;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.potion.PotionEffectType.*;

public class AuthListener implements Listener {
	Auth plugin;
	Server server;

	Component prefix = Component.text(" -> ", GRAY);
	List<Component> msg = new ArrayList<>(List.of(
		// 0
		prefix.append(Component.text("Подключился", YELLOW)),
		// 1
		prefix.append(Component.text("Вошёл", GREEN)),
		// 2
		prefix.append(Component.text("Отключился", YELLOW)),
		// 3
		prefix.append(Component.text("Введи пароль для ", WHITE))
			.append(Component.text("регистрации", RED)),
		// 4
		prefix.append(Component.text("Ты ", WHITE))
			.append(Component.text("зарегистрирован", GREEN))
			.append(Component.text(", твой пароль: ", WHITE)),
		// 5
		prefix.append(Component.text("Введи пароль для ", WHITE))
			.append(Component.text("входа", RED)),
		// 6
		prefix.append(Component.text("Вход выполнен", GREEN)),
		// 7
		prefix.append(Component.text("Неверный пароль", RED)),
		// 8
		prefix.append(Component.text("Войди", RED))
			.append(Component.text(", чтобы использовать это", WHITE))

	));

	public Config players;
	private final Location loginLocation;
	public Map<String, Location> lastLocation = new HashMap<>();
	public Map<String, Collection<PotionEffect>> lastEffect = new HashMap<>();


	public AuthListener(Auth plugin) {
		this.plugin = plugin;
		server = plugin.server;
		players = plugin.players;
		loginLocation = new Location(plugin.server.getWorld("owerworld"), 0, 100000, 0);
	}

	public Map<String, Object> getData(String name) {
		if (!players.containsKey(name)) players.put(name, new HashMap<>(Map.of("login", false)));
		return (Map<String, Object>) players.get(name);
	}

	public Map<String, Object> getData(Player player) {
		return getData(player.getName());
	}

	public void setPerm(Player player, String key, Boolean value) {
		player.addAttachment(plugin, key, value);
		player.recalculatePermissions();
	}

	@EventHandler
	public void on(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		if (!name.matches("[a-zA-Zа-яА-Я0-9_]+")) {
			event.kickMessage(Component.text("недопустимый ник", RED));
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		} else if (Bukkit.getServer().getPlayer(name) != null) {
			event.kickMessage(Component.text("игрок с ником \""+name+"\" уже есть на сервере", RED));
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		server.broadcast(Component.text(player.getName(), WHITE).append(msg.get(0)), "gikexe.auth");
		player.sendMessage(msg.get(getData(player).get("pass") == null ? 3 : 5));
		event.joinMessage(null);
		lastLocation.put(player.getName(), player.getLocation());
		lastEffect.put(player.getName(), player.getActivePotionEffects());
		player.clearActivePotionEffects();
		player.addPotionEffect(new PotionEffect(SLOW_FALLING, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(DAMAGE_RESISTANCE, -1, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(INVISIBILITY, -1, 255, false, false, false));
		player.teleport(loginLocation);
	}

	private void back(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.clearActivePotionEffects();
				player.addPotionEffects(lastEffect.remove(player.getName()));
				player.teleport(lastLocation.remove(player.getName()));
			}
		}.runTask(plugin);
	}

	@EventHandler
	public void on(AsyncChatEvent event) {
		Player player = event.getPlayer();
		String text = ((TextComponent) event.message()).content();
		Map<String, Object> data = getData(player);
		if ((boolean) data.get("login")) return;
		if (data.get("pass") == null) {
			data.put("pass", text);
			data.replace("login", true);
			player.sendMessage(msg.get(4).append(Component.text(text, GREEN)));
			server.broadcast(Component.text(player.getName(), WHITE).append(msg.get(1)), "gikexe.auth");
			setPerm(player, "gikexe.auth", true);
			back(player);

		} else if (data.get("pass").equals(text)) {
			data.replace("login", true);
			player.sendMessage(msg.get(6));
			server.broadcast(Component.text(player.getName(), WHITE).append(msg.get(1)), "gikexe.auth");
			setPerm(player, "gikexe.auth", true);
			back(player);

		} else {
			player.sendMessage(msg.get(7));
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getData(player).replace("login", false);
		setPerm(player, "gikexe.auth", false);
		server.broadcast(Component.text(player.getName(), WHITE).append(msg.get(2)), "gikexe.auth");
		event.quitMessage(null);
	}

	//CANSEL NON LOGIN
	private void _cansel(PlayerEvent event) {
		Player player = event.getPlayer();
		if ((boolean) getData(player).get("login")) return;
		player.sendMessage(msg.get(8));
		((Cancellable) event).setCancelled(true);
	}

	@EventHandler
	public void on(PlayerCommandPreprocessEvent event) {_cansel(event);}
	@EventHandler
	public void on(PlayerDropItemEvent event) {_cansel(event);}
	@EventHandler
	public void on(PlayerPickItemEvent event) {_cansel(event);}
	@EventHandler
	public void on(PlayerInteractEvent event) {_cansel(event);}
}