package me.sirimperivm.chatUtilities.assets.managers;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.objects.entities.ChatGroup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public class ChatManager {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;

    private ChatGroup defaultGroup;
    private HashMap<String, ChatGroup> chatGroups;
    private HashMap<Player, ChatGroup> playerChatGroups;

    private BukkitTask refreshPlayerTask;

    public ChatManager(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();

        configure();
        startRefreshingPlayers();
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

    public void startRefreshingPlayers() {
        if (refreshPlayerTask != null && !refreshPlayerTask.isCancelled()) {
            refreshPlayerTask.cancel();
            refreshPlayerTask = null;
        }

        long timer = configHandler.getSettings().getLong("tasks.refresh-player.timer", 20 * 20);

        refreshPlayerTask = new BukkitRunnable() {
            @Override
            public void run() {
                playerChatGroups = new HashMap<>();

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    List<String> playerGroupNames = getPlayerChatGroups(player);
                    ChatGroup selectedGroup = null;
                    int highestWeight = -1;

                    for (String groupName : playerGroupNames) {
                        ChatGroup group = chatGroups.get(groupName);
                        if (group != null && group.getWeight() > highestWeight) {
                            highestWeight = group.getWeight();
                            selectedGroup = group;
                        }
                    }

                    if (selectedGroup == null) {
                        selectedGroup = defaultGroup;
                    }

                    playerChatGroups.put(player, selectedGroup);
                }
            }
        }.runTaskTimer(plugin, 0L, timer);
    }

    public List<String> getPlayerChatGroups(Player player) {
        List<String> groupPermissions = new ArrayList<>();
        String prefix = "chatutilities.group.";

        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();

        for (PermissionAttachmentInfo permission : permissions) {
            if (permission.getPermission().startsWith(prefix)) {
                groupPermissions.add(permission.getPermission().replace(prefix, ""));
            }
        }

        return groupPermissions;
    }

    public ChatGroup getDefaultGroup() {
        return defaultGroup;
    }

    public HashMap<String, ChatGroup> getChatGroups() {
        return chatGroups;
    }

    public HashMap<Player, ChatGroup> getPlayerChatGroups() {
        return playerChatGroups;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}