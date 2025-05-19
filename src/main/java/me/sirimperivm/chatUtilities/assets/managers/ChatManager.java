package me.sirimperivm.chatUtilities.assets.managers;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ChatManager {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;

    private ChatGroup defaultGroup;
    private HashMap<String, ChatGroup> chatGroups;

    private Set<Player> mentionsCooldown;
    
    public ChatManager(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();

        configure();
        mentionsCooldown = new HashSet<>();
    }

    public void configure() {
        ConfigurationSection config = configHandler.getChat();
        if (config == null || config.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat config is empty!");

        chatGroups = new HashMap<>();
        int weight = 0;

        for (String groupName : config.getKeys(false)) {
            ConfigurationSection groupConfig = config.getConfigurationSection(groupName);

            boolean byDefault = groupConfig.getBoolean("by-default", false);
            if (byDefault && defaultGroup == null) {
                ChatGroup group = new ChatGroup(this, weight, groupName, true);

                group.configure();
                defaultGroup = group;
                chatGroups.put(groupName, group);
            } else {
                ChatGroup group = new ChatGroup(this, weight, groupName, false);

                group.configure();
                chatGroups.put(groupName, group);
            }

            weight++;
        }
    }

    public ChatGroup getPlayerChatGroup(Player player) {
        List<ChatGroup> sortedGroups = chatGroups.values().stream().sorted(Comparator.comparing(ChatGroup::getWeight)).collect(Collectors.toList());
        String prefix = "chatutilities.group.";

        for (ChatGroup group : sortedGroups) {
            String groupName = group.getName();
            if (player.hasPermission(prefix + groupName)) return group;
        }
        return defaultGroup;
    }

    public void startMentionCooldown(Player player) {
        mentionsCooldown.add(player);

        long cooldown = configHandler.getSettings().getLong("user-mentions.cooldown", 3*20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (inMentionCooldown(player)) mentionsCooldown.remove(player);
            }
        }.runTaskLater(plugin, cooldown);
    }

    public boolean inMentionCooldown(Player player) {
        return mentionsCooldown.contains(player);
    }

    public ChatGroup getDefaultGroup() {
        return defaultGroup;
    }

    public HashMap<String, ChatGroup> getChatGroups() {
        return chatGroups;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}