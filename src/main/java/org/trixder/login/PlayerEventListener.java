package org.trixder.login;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerEventListener implements Listener {
    public static Login login;
    public static Users users;

    private void SendMessage(Player player) {
        if (users.IsRegistered(player.getName())) {
            player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.login")));
            player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.change_password")));
        }
        else player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.register")));

        player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.warning")));
    }

    private boolean isLogged(Player player) {
        return !Login.users.IsLogged(player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Login.users.Logout(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        SendMessage(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (isLogged(e.getPlayer()) && !e.getFrom().toVector().equals(e.getTo().toVector())) e.setTo(e.getFrom());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickUp(PlayerAttemptPickupItemEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onMount(EntityMountEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = (Player)e.getEntity();
            if (isLogged(player)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        String input = PlainTextComponentSerializer.plainText().serialize(e.message());
        String playerName = e.getPlayer().getName();

        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);

            String[] args = input.split(" ");

            if (users.IsRegistered(playerName)) {
                if (args.length == 1) {
                    // <password>
                    login.SendMessage(e.getPlayer(), users.Login(playerName, input));
                } else if (args.length == 3) {
                    // <oldPassword> <newPassword> <newPassword>
                    login.SendMessage(e.getPlayer(), users.ChangePassword(playerName, args[0], args[1], args[2]));
                    e.getPlayer().sendMessage("password changed");
                } else login.Help(e.getPlayer());

            } else {
                if (args.length == 2) {
                    // <password> <password>
                    login.SendMessage(e.getPlayer(), users.Register(playerName, args[0], args[1]));
                } else login.Help(e.getPlayer());

            }
        } else if (!users.CheckPassword(playerName, input)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.warning_in_game")));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player && isLogged(player)) {
            e.setCancelled(true);
            SendMessage(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player && isLogged(player)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReceiveDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player player && isLogged(player)) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            SendMessage(e.getPlayer());
        }
    }
}