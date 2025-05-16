package me.sirimperivm.chatUtilities.assets.managers;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;

import java.util.HashMap;

@SuppressWarnings("all")
public class ChatManager {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;

    private ChatGroup defaultGroup;
    private HashMap<String, ChatGroup> chatGroups;

    public ChatManager(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();


    }
}