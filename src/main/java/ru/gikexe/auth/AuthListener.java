package ru.gikexe.auth;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static org.bukkit.potion.PotionEffectType.*;
import static ru.gikexe.auth.Data.login;
import static ru.gikexe.auth.Data.pass;

public class AuthListener implements Listener {

	Map<String, PlayerData> playerDataMap = new HashMap<>();
	Location loginLocation = new Location(Bukkit.getWorld("world"), 0, 100000, 0);
	Collection<PotionEffect> loginEffect = List.of(
		new PotionEffect(LEVITATION,        -1, 255, false, false),
		new PotionEffect(DAMAGE_RESISTANCE, -1, 255, false, false),
		new PotionEffect(INVISIBILITY,      -1, 255, false, false)
	);

	Component prefix = Auth.me.prefix;
	List<Component> msg = new ArrayList<>(List.of(
		prefix.append(Component.text("Подключился", YELLOW)),
		prefix.append(Component.text("Вошёл", GREEN)),
		prefix.append(Component.text("Отключился", YELLOW)),
		prefix.append(Component.text("Введи пароль для ", WHITE))
			.append(Component.text("регистрации", RED)),
		prefix.append(Component.text("Ты ", WHITE))
			.append(Component.text("зарегистрирован", GREEN))
			.append(Component.text(", твой пароль: ", WHITE)),
		prefix.append(Component.text("Введи пароль для ", WHITE))
			.append(Component.text("входа", RED)),
		prefix.append(Component.text("Вход выполнен", GREEN)),
		prefix.append(Component.text("Неверный пароль", RED)),
		prefix.append(Component.text("Войди", RED))
			.append(Component.text(", чтобы использовать это", WHITE))
	));

	public void setPerm(Player player, String key, Boolean value) {
		player.addAttachment(Auth.me, key, value);
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
		Bukkit.broadcast(Component.text(player.getName(), WHITE).append(msg.get(0)), "gikexe.auth");
		player.sendMessage(msg.get(pass(player) == null ? 3 : 5));
		event.joinMessage(null);
		playerDataMap.put(player.getName(), new PlayerData(player));
		player.clearActivePotionEffects();
		player.addPotionEffects(loginEffect);
		player.teleport(loginLocation);
	}

	private void back(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				playerDataMap.remove(player.getName()).back();
			}
		}.runTask(Auth.me);
	}

	@EventHandler
	public void on(AsyncChatEvent event) {
		Player player = event.getPlayer();
		String text = ((TextComponent) event.message()).content();
		if (login(player)) return;
		if (pass(player) == null) {
			pass(player, text);
			login(player, true);
			player.sendMessage(msg.get(4).append(Component.text(text, GREEN)));
			Bukkit.broadcast(Component.text(player.getName(), WHITE).append(msg.get(1)), "gikexe.auth");
			setPerm(player, "gikexe.auth", true);
			back(player);

		} else if (pass(player).equals(text)) {
			login(player, true);
			player.sendMessage(msg.get(6));
			Bukkit.broadcast(Component.text(player.getName(), WHITE).append(msg.get(1)), "gikexe.auth");
			setPerm(player, "gikexe.auth", true);
			back(player);

		} else player.sendMessage(msg.get(7));
		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!login(player)) playerDataMap.remove(player.getName()).back();
		login(player, false);
		setPerm(player, "gikexe.auth", false);
		Bukkit.broadcast(Component.text(player.getName(), WHITE).append(msg.get(2)), "gikexe.auth");
		event.quitMessage(null);
	}

	//CANSEL NON LOGIN
	private void _cansel(PlayerEvent event, boolean send) {
		Player player = event.getPlayer();
		if (login(player)) return;
		if (send) player.sendMessage(msg.get(8));
		((Cancellable) event).setCancelled(true);
	}

	@EventHandler
	public void on(PlayerCommandPreprocessEvent event) {_cansel(event, true);}
	@EventHandler
	public void on(PlayerDropItemEvent event) {_cansel(event, false);}
	@EventHandler
	public void on(PlayerPickItemEvent event) {_cansel(event, false);}
	@EventHandler
	public void on(PlayerInteractEvent event) {_cansel(event, false);}
}
