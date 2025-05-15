package me.sirimperivm.chatUtilities.assets.others;

import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.objects.enums.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("all")
public class Errors {

    public static boolean hasPerm(Object target, String node, boolean reply) {
        if (target instanceof Player) {
            Player p = (Player) target;
            if (p.hasPermission(node)) return true;
            if (reply) {
                Map<String, String> placeholders = Map.of("%node%", node);
                p.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), "no-perm", placeholders));
            }
            return false;
        } else if (target instanceof CommandSender) {
            CommandSender s = (CommandSender) target;
            if (s.hasPermission(node)) return true;
            if (reply) {
                Map<String, String> placeholders = Map.of("%node%", node);
                s.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), "no-perm", placeholders));
            }
        }
        return false;
    }

    public static boolean isPlayer(CommandSender s, boolean reply) {
        if (s instanceof Player) return true;
        if (reply) s.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), "no-console", Map.of()));
        return false;
    }
}
