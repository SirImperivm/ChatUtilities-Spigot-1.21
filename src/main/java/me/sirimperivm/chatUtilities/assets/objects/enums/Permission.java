package me.sirimperivm.chatUtilities.assets.objects.enums;

import me.sirimperivm.chatUtilities.ChatUtilities;

@SuppressWarnings("all")
public enum Permission {

    GLOBAL_PERMISSION(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.global-permission", "chatutilities.*")),
    CHAT_COLORED(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-formats.colored", "chatutilities.chat.colored")),
    CHAT_SPECIAL(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-formats.special", "chatutilities.chat.special")),
    CHAT_HEX(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-formats.hex", "chatutilities.chat.hex")),
    CHATITEM_USE(ChatUtilities.getInstance().getConfigHandler().getSettings().getString("permissions.chat-item.use", "chatutilities.chat-item.use"));

    private String node;

    Permission(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }
}
