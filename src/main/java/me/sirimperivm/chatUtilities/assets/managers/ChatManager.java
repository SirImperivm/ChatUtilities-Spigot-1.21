package me.sirimperivm.chatUtilities.assets.managers;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

        configure();
    }

    public void configure() {
        chatGroups = new HashMap<>();
        FileConfiguration config = configHandler.getChat();

        ConfigurationSection section = config.getConfigurationSection("");
        if (section == null || section.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat section is empty!");

        int weight = 0;
        ChatGroup defaultGroup = null;

        for (String groupName : section.getKeys(false)) {
            ConfigurationSection groupConfig = section.getConfigurationSection(groupName);
            if (groupConfig == null || groupConfig.getKeys(false).isEmpty()) continue;

            boolean isDefault = groupConfig.getBoolean("by-default", false);

            ChatGroup group = new ChatGroup(weight, groupName, isDefault);
            group.configure(groupConfig);

            chatGroups.put(groupName, group);

            if (isDefault) {
                if (defaultGroup != null) {
                    group.setDef(false);
                } else {
                    defaultGroup = group;
                }
            }

            weight++;
        }

        this.defaultGroup = defaultGroup;
    }

    public ChatGroup getPlayerChatGroup(Player player) {
        ChatGroup highestWeightGroup = null;
        int highestWeight = -1;

        for (String groupName : chatGroups.keySet()) {
            if (!player.hasPermission("chatutilities.group." + groupName)) continue;
            ChatGroup group = chatGroups.get(groupName);

            if (group.getWeight() <= highestWeight) continue;
            highestWeight = group.getWeight();
            highestWeightGroup = group;
        }

        if (highestWeightGroup != null) {
            return highestWeightGroup;
        }

        if (defaultGroup != null) {
            return defaultGroup;
        }

        if (!chatGroups.isEmpty()) {
            return chatGroups.values().iterator().next();
        }

        return null;
    }

    public HashMap<String, ChatGroup> getChatGroups() {
        return chatGroups;
    }
}
