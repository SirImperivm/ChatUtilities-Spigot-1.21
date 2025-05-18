package me.sirimperivm.chatUtilities;

import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.managers.ChatManager;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;
import me.sirimperivm.chatUtilities.assets.objects.enums.Config;
import me.sirimperivm.chatUtilities.assets.objects.exceptions.ChatMessageException;
import me.sirimperivm.chatUtilities.assets.others.Logger;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

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

        try {
            TextComponent message = playerChatGroup.getMessage(p, baseMessage);
            Bukkit.getServer().spigot().broadcast(message);
            Bukkit.getConsoleSender().sendMessage(Formatter.translate("&b" + p.getName() + "&r: " + baseMessage));
            playerChatGroup.sendChatSound();
        } catch (ChatMessageException ex) {
            p.sendMessage(ConfigHandler.getFormatString(Config.messages.getC(), ex.getMessage(), Map.of()));
        } catch (Exception ex) {
            Logger.fail("An exception occurred while processing a chat message! Enable debug mode for more information.");
            Logger.debug("Sender: " + p.getName());
            Logger.debug("Message: " + baseMessage);
            Logger.debug("Chat Group: " + playerChatGroup.getName());
            Logger.debug("Stacktrace:");
            ex.printStackTrace();
        }
    }
}