package org.trixder.login;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerEventListener implements Listener {
    public static Login login;
    public static Users users;

    private void SendMessage(Player player) {
        if (!users.IsLogged(player.getName())) player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.login")));
        else player.sendMessage(Objects.requireNonNull(login.getConfig().getString("messages.register")));
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
        if (isLogged(e.getPlayer())) {
            if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
                e.setTo(e.getFrom());
                //SendMessage(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            //SendMessage(e.getPlayer());
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerAttemptPickupItemEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = (Player)e.getEntity();
            if (isLogged(player)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            //SendMessage(e.getPlayer());
            login.Help(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            //SendMessage(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            //SendMessage(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
            //SendMessage(e.getPlayer());
        }
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
        if (e.getEntity() instanceof Player player && isLogged(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReceiveDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player player && isLogged(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (isLogged(e.getPlayer())) {
            String msg = e.getMessage().toLowerCase();
            e.getPlayer().sendMessage(msg);

            if (!msg.startsWith("/register") && !msg.startsWith("/login") && !msg.startsWith("/changepassword")) {
                e.setCancelled(true);
                login.Help(e.getPlayer());
            }

        }
    }
}