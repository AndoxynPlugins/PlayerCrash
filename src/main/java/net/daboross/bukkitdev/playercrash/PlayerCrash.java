package net.daboross.bukkitdev.playercrash;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerCrash extends JavaPlugin {

    private static final String NO_ARGS = ChatColor.DARK_RED + "Please supply an argument.";
    private static final String TOO_MANY_ARGS = ChatColor.DARK_RED + "Too many arguments.";
    private static final String NO_PERMISSION = ChatColor.DARK_RED + "You don't have permission.";
    private static final String USAGE = ChatColor.GREEN + "Usage: " + ChatColor.RED + "/%s " + ChatColor.DARK_GRAY + "<" + ChatColor.DARK_RED + "Player" + ChatColor.DARK_GRAY + ">";
    private PlayerCrashReflection reflection;

    @Override
    public void onEnable() {
        try {
            reflection = new PlayerCrashReflection();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
            getLogger().log(Level.SEVERE, "Error starting reflection.", ex);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("playercrash.crash")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(NO_ARGS);
            sender.sendMessage(String.format(USAGE, label));
            return true;
        }
        if (args.length > 1) {
            sender.sendMessage(TOO_MANY_ARGS);
            sender.sendMessage(String.format(USAGE, label));
            return true;
        }
        Player toCrash = Bukkit.getPlayerExact(args[0]);
        if (toCrash == null) {
            sender.sendMessage(ChatColor.DARK_RED + "The player " + ChatColor.RED + args[0] + ChatColor.DARK_RED + " is not online.");
            return true;
        }
        if (toCrash.hasPermission("playercrash.uncrashable")) {
            sender.sendMessage(ChatColor.DARK_RED + "The player " + ChatColor.RED + toCrash.getName() + ChatColor.DARK_RED + " is uncrashable.");
            return true;
        }
        try {
            reflection.crash(toCrash);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | RuntimeException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "Failed to crash player " + ChatColor.RED + toCrash.getName() + ChatColor.DARK_RED + ". Error logged to console.");
            getLogger().log(Level.SEVERE, "Failed to crash player " + toCrash.getName(), ex);
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "The player " + ChatColor.DARK_GREEN + toCrash.getName() + ChatColor.GREEN + " has been crashed.");
        return true;
    }
}
