package me.sirimperivm.chatUtilities;

import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.managers.ChatManager;
import me.sirimperivm.chatUtilities.assets.others.Logger;
import me.sirimperivm.chatUtilities.assets.strings.Prefixer;
import me.sirimperivm.chatUtilities.commands.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class ChatUtilities extends JavaPlugin {

    private ChatUtilities plugin;
    private static ChatUtilities instance;

    private ConfigHandler configHandler;
    private Prefixer prefixer;

    private ChatManager chatManager;

    @Override
    public void onLoad() {
        plugin = this;
        instance = this;
    }

    @Override
    public void onEnable() {
        configHandler = new ConfigHandler(plugin);
        prefixer = new Prefixer(plugin);

        chatManager = new ChatManager(plugin);

        getPluginManager().registerEvents(new Events(plugin), plugin);
        getCommand("chat-utilities").setExecutor(new MainCommand(plugin));
        getCommand("chat-utilities").setTabCompleter(new MainCommand(plugin));

        Logger.success("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        Logger.success("Plugin disabled!");
    }

    public ChatUtilities getPlugin() {
        return plugin;
    }

    public static ChatUtilities getInstance() {
        return instance;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public Prefixer getPrefixer() {
        return prefixer;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }
}
