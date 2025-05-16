package me.sirimperivm.chatUtilities;

import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.managers.ChatManager;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SuppressWarnings("all")
public class Events implements Listener {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;
    private ChatManager chatManager;

    public Events(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();
        chatManager = plugin.getChatManager();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String baseMessage = e.getMessage();

        e.setCancelled(true);

        ChatGroup playerChatGroup = chatManager.getPlayerChatGroups().get(p);
        if (playerChatGroup == null) return;

        TextComponent message = playerChatGroup.getMessage(p, baseMessage);
        Bukkit.getServer().spigot().broadcast(message);
    }
}