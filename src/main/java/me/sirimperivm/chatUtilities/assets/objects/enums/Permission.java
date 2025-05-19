package me.sirimperivm.chatUtilities.assets.objects.enums;

import me.sirimperivm.chatUtilities.ChatUtilities;

@SuppressWarnings("all")
public enum Permission {

    GLOBAL_PERMISSION(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.global-permission", "chatutilities.*")),
    CHAT_COLORED(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-formats.colored", "chatutilities.chat.colored")),
    CHAT_SPECIAL(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-formats.special", "chatutilities.chat.special")),
    CHATITEM_USE(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-item.use", "chatutilities.chat-item.use")),
    CHATMENTIONS_SEND(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-mentions.send", "chatutilities.chat-mentions.send")),
    CHATMENTIONS_RECEIVE(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-mentions.receive", "chatutilities.chat-mentions.receive")),
    CHATMENTIONS_BYPASS_COOLDOWN(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-mentions.bypass-cooldown", "chatutilities.chat-mentions.bypass-cooldown")),
    COMMAND_MAIN(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-utilities-command.main", "chatutilities.command.main")),
    COMMAND_RELOAD(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-utilities-command.reload", "chatutilities.command.reload"));

    private String node;

    Permission(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }
}
