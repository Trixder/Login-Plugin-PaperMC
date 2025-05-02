package org.trixder.login;

import com.google.common.hash.Hashing;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.charset.StandardCharsets;

import static org.trixder.login.Login.MessageType.*;

public class Users {
    private final Login login;

    public Users(Login login) {
        this.login = login;
    }

    private FileConfiguration getConfig() {
        return login.getConfig();
    }

    private void saveConfig() {
        login.saveConfig();
    }

    // Checks if the player is registered
    public boolean IsRegistered(String playerName) {
        return getConfig().contains("players." + playerName);
    }

    // Checks if the player has password
    public boolean PlayerHasPassword(String playerName) {
        return getConfig().contains("players." + playerName + ".password");
    }

    // Checks if the player is logged in
    public boolean IsLogged(String playerName) {
        return getConfig().getBoolean("players." + playerName + ".loggedIn");
    }

    // Cheks if the password is correct
    public boolean CheckPassword(String playerName, String password) {
        return !(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()).equals(getConfig().getString("players." + playerName + ".password"));
    }

    // Encrypts the password
    private String EncryptPassword(String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    // Updates how many times has the player failed at guessing the password
    public void FailedAttempts(String playerName) {
        if (!PlayerHasPassword(playerName)) return;
        int failedAttempts = getConfig().getInt("players." + playerName + ".failedAttempts") + 1;

        getConfig().set("players." + playerName + ".failedAttempts", failedAttempts);

        if (failedAttempts == getConfig().getInt("maxFailedAttempts")) {
            getConfig().set("players." + playerName + ".lockedUntil", System.currentTimeMillis() / 1000 + getConfig().getInt("lockTime"));
        }

        saveConfig();
    }

    // Checks if the player is still locked out
    public boolean Locked(String playerName) {
        long lockedUntil = getConfig().getLong("players." + playerName + ".lockedUntil");

        if (lockedUntil > System.currentTimeMillis() / 1000) return true;
        else if (lockedUntil == 0) return false;

        getConfig().set("players." + playerName + ".failedAttempts", 0);
        getConfig().set("players." + playerName + ".lockedUntil", 0);
        saveConfig();
        return false;
    }

    // Add player to the yml file
    public void AddPlayer(String playerName, String password) {
        login.getLogger().info(password);
        getConfig().set("players." + playerName + ".password", EncryptPassword(password));
        getConfig().set("players." + playerName + ".loggedIn", true);
        getConfig().set("players." + playerName + ".failedAttempts", 0);
        getConfig().set("players." + playerName + ".lockedUntil", 0);
        saveConfig();
    }

    // Checks if it is possible to create user
    public Login.MessageType Register(String playerName, String password, String confirmPassword) {
        if (PlayerHasPassword(playerName)) {
            if (IsLogged(playerName)) return ALREADY_LOGGED_IN;
            else return USERNAME_TAKEN;
        } else if (!password.equals(confirmPassword)) return PASSWORD_MISMATCH;
        else if (password.length() < 6) return PASSWORD_TOO_SHORT;

        AddPlayer(playerName, password);

        return WELCOME;
    }

    // Changes the password of an account while logged out
    public Login.MessageType ChangePassword(String playerName, String oldPassword, String newPassword, String confirmNewPassword) {
        if (Locked(playerName)) return ACCOUNT_LOCKED;
        else if (newPassword.length() < 6) return PASSWORD_TOO_SHORT;
        else if (!newPassword.equals(confirmNewPassword)) return PASSWORD_MISMATCH;
        else if (CheckPassword(playerName, oldPassword)) return INCORRECT_PASSWORD;

        AddPlayer(playerName, newPassword);

        return WELCOME;
    }

    // logs out the player
    public Login.MessageType Logout(String playerName) {
        if (IsRegistered(playerName)) {
            getConfig().set("players." + playerName + ".loggedIn", false);
            saveConfig();
            return LOGGED_OUT;
        } else return ALREADY_LOGGED_OUT;
    }

    // logs in the player
    public Login.MessageType Login(String playerName, String password) {
        if (IsLogged(playerName)) return ALREADY_LOGGED_IN;
        else if (Locked(playerName)) return ACCOUNT_LOCKED;
        else if (CheckPassword(playerName, password)) return INCORRECT_PASSWORD;
        else if (!IsRegistered(playerName)) return USERNAME_NOT_REGISTERED;

        getConfig().set("players." + playerName + ".loggedIn", true);
        getConfig().set("players." + playerName + ".failedAttempts", 0);
        saveConfig();

        return WELCOME;
    }

    // Sets the logged in status of all users to false
    public void LogoutAll() {
        for (String playerName : getConfig().getConfigurationSection("players").getKeys(false)) {
            Logout(playerName);
        }
    }
}
