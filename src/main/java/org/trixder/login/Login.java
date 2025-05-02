package org.trixder.login;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class Login extends JavaPlugin implements Listener {
    public enum MessageType {
        WELCOME,
        INCORRECT_PASSWORD,
        PASSWORD_MISMATCH,
        ACCOUNT_LOCKED,
        USERNAME_NOT_REGISTERED,
        USERNAME_TAKEN,
        ALREADY_LOGGED_IN,
        PASSWORD_TOO_SHORT,
        ALREADY_LOGGED_OUT,
        LOGGED_OUT,
        ERROR
    }

    public static Users users;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        users = new Users(this);
        // TODO 1: Add permissions for LuckyPerms
        ConfigurationSection playersSection = getConfig().getConfigurationSection("players");

        int accounts = 0;
        if (playersSection != null) {
            accounts = playersSection.getKeys(false).size();
        }

        if (accounts > 0) {
            users.LogoutAll();
        }
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);

        PlayerEventListener.login = this;
        PlayerEventListener.users = users;

        getLogger().info("[Trixder-Plugin] Trixder Plugin Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[Trixder-Plugin] Trixder Plugin Disabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (command.getName().equalsIgnoreCase("logout") && sender.hasPermission("login.normal")) {
                SendMessage(sender, users.Logout(sender.getName()));
            } else if (command.getName().equalsIgnoreCase("loginreload") && sender.hasPermission("login.admin")) {
                if (sender instanceof Player) {
                    if (users.IsLogged(sender.getName())) {
                        this.reloadConfig();
                        sender.sendMessage("§aLogin config reloaded!");
                    } else SendMessage(sender, MessageType.ERROR);
                } else {
                    this.reloadConfig();
                    getLogger().info("[Trixder-Plugin] Login config reloaded!");
                }

            } else if (command.getName().equalsIgnoreCase("login")) {
                Help(sender);
            }

            return true;
        }
        sender.sendMessage("§aCommand with this amount of arguments is not recognized.");
        return true;
    }

    public void Help(CommandSender sender) {
        sender.sendMessage("§aLogin Help:");

        if (sender.hasPermission("login.normal")) {
            sender.sendMessage("§alogin - <password>");
            sender.sendMessage("§aregister - <password> <confirmPassword>");
            sender.sendMessage("§achangepassword - <oldPassword> <newPassword> <confirmNewPassword>");
            sender.sendMessage("§a/logout - Logs you out.");
        }

        if (sender.hasPermission("login.admin")) {
            sender.sendMessage("");
            sender.sendMessage("§aAdmin Commands:");
            sender.sendMessage("§a/loginreload §7- Reloads the login plugin config");
        }
    }

    public void MessageType(CommandSender sender, String message, String secondaryMessage) {
        String messageType = getConfig().getString("message.type", "chat").toLowerCase();

        if (messageType.equals("title")) {
            long fadeIn = getConfig().getLong("message.fadeIn", 1);
            long stay = getConfig().getLong("message.stay", 3);
            long fadeOut = getConfig().getLong("message.fadeOut", 1);
            sender.showTitle(Title.title(
                    Component.text(message), Component.text(secondaryMessage),
                    Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
            ));
        } else {
            sender.sendMessage(message);
            if (!messageType.equals("chat")) getLogger().warning("Invalid message type: " + messageType);
        }
    }

    public void SendMessage(CommandSender sender, MessageType type) {
        Player player = (Player) sender;

        switch (type) {
            case WELCOME -> {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                MessageType(player, getConfig().getString("messages.welcome", "§aWelcome to Magic World!"), "");
            }
            case INCORRECT_PASSWORD -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                users.FailedAttempts(sender.getName());
                MessageType(player, getConfig().getString("messages.password_incorrect", "§aIncorrect password!"), "");
            }
            case PASSWORD_MISMATCH -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.passwords_do_not_match", "§aThe passwords do not match!"), "");
            }
            case ACCOUNT_LOCKED -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                String message = getConfig().getString("messages.account_locked_time", "§a%time% seconds");

                Long time = getConfig().getLong("players." + sender.getName() + ".lockedUntil") - (System.currentTimeMillis() / 1000);

                message = message.replace("%time%", String.valueOf(time));

                MessageType(player, getConfig().getString("messages.account_locked", "§aYour account has been locked for:"), message);
            }
            case USERNAME_NOT_REGISTERED -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.username_not_registered", "§aThis username hasn't been registered."), "");
            }
            case USERNAME_TAKEN -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.username_taken", "§aThis username has already been taken."), "");
            }
            case ALREADY_LOGGED_IN -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.already_logged_in", "§aYou are already logged in."), "");
            }
            case PASSWORD_TOO_SHORT -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.password_too_short", "§aThe password is not long enough."), "");
            }
            case LOGGED_OUT -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.logged_out", "§aYou have been logged out."), "");
            }
            case ALREADY_LOGGED_OUT -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.already_logged_out", "§aYou are already logged out."), "");
            }
            case ERROR -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.error", "§cSomething went wrong!"), "");
            }
            default -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, .5f);
                MessageType(player, getConfig().getString("messages.unknown_error", "§cUnknown error, please report this error!"), "");
            }
        }
    }
}
