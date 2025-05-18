package me.sirimperivm.chatUtilities.commands;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.managers.ChatManager;
import me.sirimperivm.chatUtilities.assets.objects.enums.Config;
import me.sirimperivm.chatUtilities.assets.objects.enums.Permission;
import me.sirimperivm.chatUtilities.assets.others.Errors;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class MainCommand implements CommandExecutor, TabCompleter {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;
    private ChatManager chatManager;

    public MainCommand(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();
        chatManager = plugin.getChatManager();
    }

    private void getUsage(CommandSender sender, String label) {
        List<String> commands = configHandler.getMessages().getStringList("chatutilities-command.usage");
        for (String command : commands) {
            sender.sendMessage(Formatter.translate(command
                    .replace("%command-label%", label)
            ));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String globalNode = Permission.GLOBAL_PERMISSION.getNode();
        String mainNode = Permission.COMMAND_MAIN.getNode();
        if (!Errors.hasPerm(sender, globalNode, false) && !Errors.hasPerm(sender, mainNode, true)) return false;

        if (args.length != 1) {
            getUsage(sender, label);
            return false;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            String reloadNode = Permission.COMMAND_RELOAD.getNode();
            if (!Errors.hasPerm(sender, globalNode, false) && !Errors.hasPerm(sender, mainNode, true)) return false;

            try {
                configHandler.loadAll();
                chatManager.configure();
                chatManager.startRefreshingPlayers();
                sender.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), "plugin-reloaded.successfully", Map.of()));
            } catch (Exception e) {
                sender.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), "plugin-reloaded.failed", Map.of()));
            }
            return false;
        }

        getUsage(sender, label);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String globalNode = Permission.GLOBAL_PERMISSION.getNode();
        String mainNode = Permission.COMMAND_MAIN.getNode();

        if (sender.hasPermission(globalNode) || sender.hasPermission(mainNode)) {
            if (args.length == 1) {
                String reloadNode = Permission.COMMAND_RELOAD.getNode();
                List<String> tablist = new ArrayList<>();
                tablist.add("help");
                if (sender.hasPermission(globalNode) || sender.hasPermission(reloadNode)) tablist.add("reload");
                return tablist;
            }
        }
        return new ArrayList<>();
    }
}
