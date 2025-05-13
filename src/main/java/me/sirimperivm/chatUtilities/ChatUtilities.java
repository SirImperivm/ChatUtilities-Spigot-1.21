package me.sirimperivm.chatUtilities;

import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public final class ChatUtilities extends JavaPlugin {

    private ChatUtilities plugin;
    private static ChatUtilities instance;

    @Override
    public void onLoad() {
        plugin = this;
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ChatUtilities getPlugin() {
        return plugin;
    }

    public static ChatUtilities getInstance() {
        return instance;
    }
}
