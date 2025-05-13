package me.sirimperivm.chatUtilities.assets.others;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import org.bukkit.Bukkit;

@SuppressWarnings("all")
public class Logger {

    public static void success(String s) {
        Bukkit.getConsoleSender().sendMessage(Formatter.translate("&a[ChatUtilities] &7" + s));
    }

    public static void info(String s) {
        Bukkit.getConsoleSender().sendMessage(Formatter.translate("&e[ChatUtilities] &7" + s));
    }

    public static void fail(String s) {
        Bukkit.getConsoleSender().sendMessage(Formatter.translate("&c[ChatUtilities] &7" + s));
    }

    public static void debug(String s) {
        boolean enabled = ChatUtilities.getInstance().getConfigHandler().getSettings().getBoolean("debug-mode", false);
        if (enabled) Bukkit.getConsoleSender().sendMessage(Formatter.translate("&b[ChatUtilities - Debug] &7" + s));
    }
}
