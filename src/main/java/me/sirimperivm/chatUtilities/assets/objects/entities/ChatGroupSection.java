package me.sirimperivm.chatUtilities.assets.objects.entities;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("all")
public class ChatGroupSection {

    private String key;
    private String text;

    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;

    private TextComponent component;

    public ChatGroupSection(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public TextComponent getComponent() {
        return component;
    }
}