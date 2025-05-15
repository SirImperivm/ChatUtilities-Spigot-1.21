package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class ChatGroup {

    private ConfigurationSection config;

    private int weight;
    private String name;
    private boolean def;

    private String format;
    private ConfigurationSection genericSettings;

    private HashMap<String, ChatGroupSection> chatSections;

    public ChatGroup(int weight, String name, boolean def) {
        this.weight = weight;
        this.name = name;
        this.def = def;
    }

    public void configure(ConfigurationSection config) {
        chatSections = new HashMap<>();
        this.config = config;

        ConfigurationSection sections = config.getConfigurationSection("chat-sections");
        if (sections == null || sections.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat group sections section is empty!");

        for (String sectionKey : sections.getKeys(false)) {
            ConfigurationSection sectionConfig = sections.getConfigurationSection(sectionKey);
            ChatGroupSection section = new ChatGroupSection(sectionKey, sectionConfig);
            chatSections.put(sectionKey, section);
        }

        format = config.getString("chat-format", "{prefix} {player-tag} {suffix} &8» {message}");
        genericSettings = config.getConfigurationSection("generic-settings");
        if (genericSettings == null || genericSettings.getKeys(false).isEmpty()) throw new IllegalArgumentException("Generic settings section is empty!");
    }

    public TextComponent createMessage(Player player, String base) {
        TextComponent result = new TextComponent("");
    
        for (ChatGroupSection section : chatSections.values()) {
            section.createComponent(player);
        }
    
        String messageFormat = format;
    
        String chatColor = genericSettings.getString("chat-color", "&#f370cc-&#f0a4f9");
    
        String coloredMessage = chatColor + base;
        messageFormat = messageFormat.replace("{message}", coloredMessage);
    
        for (String sectionKey : chatSections.keySet()) {
            String placeholder = "{" + sectionKey + "}";
        
            if (messageFormat.contains(placeholder)) {
                String[] parts = messageFormat.split(Pattern.quote(placeholder), 2);
            
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    TextComponent before = new TextComponent(Formatter.translate(parts[0]));
                    result.addExtra(before);
                }
            
                result.addExtra(chatSections.get(sectionKey).getComponent());
            
                messageFormat = parts.length > 1 ? parts[1] : "";
            }
        }
    
        if (!messageFormat.isEmpty()) {
            TextComponent remaining = new TextComponent(Formatter.translate(messageFormat));
            result.addExtra(remaining);
        }
    
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }
}