# Player Registration Plugin – PaperMC Project [In Development]

This is a Minecraft server plugin built using the PaperMC API. It adds a simple player registration system that prompts new players to register with a password before they can play. Returning players must log in before gaining access to server commands or gameplay features. The goal is to add an extra layer of access control and identity verification to servers.

## Overview

When a new player joins the server, they are required to register a password using a command. Once registered, the system remembers them and prompts them to log in on future joins. Until they log in, they can’t move, chat, or interact with the world. This helps prevent impersonation and gives server owners more control over player access.

## Features

- **Brute-Force Protection** – Limits the number of login attempts to prevent abuse.
- **Chat and Title Support** – Customize the way messages are displayed to players during registration, login, and other events.
- **Fully Customizable Response Messages** – Easily change the plugin’s messages, such as the registration prompt or incorrect password notifications.

## Commands

- **/loginreload** - Reloads the plugin’s configuration file.
- **/register password confirmPassword** - Allows new players to register.
- **/login password** - Logs in a returning player with their registered password.
- **/login** - Displays a list of available commands.
- **/logout** - Logs out the player.
- **/changepassword oldPassword newPassword confirmNewPassword** - Changes the player’s password before logging in.
- **/changepassword newPassword confirmNewPassword** - Changes the player’s password after logging in.

## Default Config
```yaml
maxFailedAttempts: 3
#in seconds
lockTime: 15
message:
  # title / chat
  type: title
  #title time in seconds
  fadeIn: 1
  stay: 3
  fadeOut: 1
messages:
  register: "§fType §a<password> <password> §fto register."
  login: "§fType §a<your password> §fto log in."
  change_password: "§fType §a<old password> <new password> <new password> §fto change your password."
  warning: "§cAFTER YOU LOG IN DO NOT TYPE YOUR PASSWORD!"
  warning_in_game: "§cHEY I TOLD YOU NOT TO SHARE YOUR PASSWORD!"
  welcome: "§aWelcome to Magic World!"
  incorrect_password: "§aIncorrect password!"
  passwords_do_not_match: "§aThe passwords do not match!"
  account_locked: "§aYour account has been locked for:"
  account_locked_time: "§a%time% seconds"
  username_not_registered: "§aThis username hasn't been registered."
  username_taken: "§aThis username has already been taken."
  already_logged_in: "§aYou are already logged in."
  password_too_short: "§aThe password is not long enough."
  logged_out: "§aYou have been logged out."
  already_logged_out: "§aYou are already logged out."
  error: "§cSomething went wrong!"
  unknown_error: "§cUnknown error, please report this error!"
players:
```

## Plan

- **Compatibility with Databases** – Allow server owners to connect this plugin to a database (e.g., MySQL, SQLite, PostgreSQL)
