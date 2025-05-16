package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class ChatGroup {

    private ChatUtilities plugin = ChatUtilities.getInstance();
    private ConfigHandler configHandler = plugin.getConfigHandler();

    private int weight;
    private String name;
    private boolean def;

    private HashMap<String, ChatGroupSection> sections;

    public ChatGroup(int weight, String name, boolean def) {
        this.weight = weight;
        this.name = name;
        this.def = def;

        configure();
    }

    public void configure() {
        sections = new HashMap<>();
        for (String key : configHandler.getChat().getConfigurationSection(name + ".chat-sections").getKeys(false)) {
            ChatGroupSection section = new ChatGroupSection(this, key);
            sections.put(key, section);
        }
    }

    public TextComponent getMessage(Player player, String baseMessage) {
        ConfigurationSection config = configHandler.getChat().getConfigurationSection(name);
        if (config == null || config.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat group does not exist!");

        ConfigurationSection genericSettings = configHandler.getChat().getConfigurationSection(name + ".generic-settings");
        if (genericSettings == null || genericSettings.getKeys(false).isEmpty()) throw new IllegalArgumentException("Generic settings section does not exist!");

        String chatColor = genericSettings.getString("chat-color");
        String userTagFormat = genericSettings.getString("user-tag-format");
        String itemShowingFormat = genericSettings.getString("item-showing-format");

        TextComponent mainComponent = new TextComponent();
        String chatFormat = config.getString("chat-format");

        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_-]+)\\}");
        Matcher matcher = pattern.matcher(chatFormat);

        int sectionCount = 0;
        while (matcher.find()) {
            sectionCount++;
            if (sectionCount > 1) break;
        }

        if (sectionCount <= 1) {
            mainComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, chatFormat)));
            return mainComponent;
        }

        matcher.reset();

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String textBefore = chatFormat.substring(lastEnd, matcher.start());
                TextComponent textComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, textBefore)));
                mainComponent.addExtra(textComponent);
            }

            String sectionName = matcher.group(1);

            ChatGroupSection section = sections.get(sectionName);
            if (section == null)
                continue;

            section.configure(player);
            TextComponent sectionComponent = section.getComponent();
            mainComponent.addExtra(sectionComponent);

            lastEnd = matcher.end();
        }

        if (lastEnd < chatFormat.length()) {
            String remainingText = chatFormat.substring(lastEnd);
            TextComponent remainingComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, remainingText)));
            mainComponent.addExtra(remainingComponent);
        }

        String coloredMessage = Formatter.translate(PlaceholderAPI.setPlaceholders(player, chatColor + baseMessage));
        mainComponent.addExtra(new TextComponent(coloredMessage));

        return mainComponent;
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