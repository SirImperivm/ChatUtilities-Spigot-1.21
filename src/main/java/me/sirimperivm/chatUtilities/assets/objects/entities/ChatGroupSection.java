package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class ChatGroupSection {

    private ChatUtilities plugin = ChatUtilities.getInstance();
    private ConfigHandler configHandler = plugin.getConfigHandler();

    private ChatGroup chatGroup;

    private String key;
    private String text;

    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;

    private TextComponent component;

    public ChatGroupSection(ChatGroup chatGroup, String key) {
        this.chatGroup = chatGroup;
        this.key = key;
    }

    public void configure(Player player) {
        ConfigurationSection section = configHandler.getChat().getConfigurationSection(chatGroup.getName() + ".chat-sections." + key);
        if (section == null || section.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat group section does not exist!");

        text = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("text")
                .replace("{player_name}", player.getName())
                .replace("{player_display_name}", player.getDisplayName())
                .replace("{player_uuid}", player.getUniqueId().toString())
        ));

        String hoverActionType = section.getString("hover-action.type").toUpperCase();
        String clickActionType = section.getString("click-action.type").toUpperCase();

        switch (hoverActionType) {
            case "SHOW_ITEM" -> {
                hoverEvent = wrapItem(section.getConfigurationSection("hover-action.item-value"));
            }
            case "SHOW_ENTITY" -> {
                hoverEvent = wrapEntity(player, section.getString("hover-action.entity-value.id"), section.getString("hover-action.entity-value.type"), section.getString("hover-action.entity-value.name")
                        .replace("{player_name}", player.getName())
                        .replace("{player_display_name}", player.getDisplayName())
                );
            }
            case "SHOW_TEXT" -> {
                String hoverText = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("hover-action.text-value")));
                hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create());
            }
            default -> {
                throw new IllegalArgumentException("Invalid hover action type!");
            }
        }

        switch (clickActionType) {
            case "RUN_COMMAND" -> {
                String command = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("click-action.command-value")
                        .replace("{player_name}", player.getName())
                        .replace("{player_display_name}", player.getDisplayName())
                        .replace("{player_uuid}", player.getUniqueId().toString())
                ));
                clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            }
            case "SUGGEST_COMMAND" -> {
                String command = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("click-action.command-value")
                        .replace("{player_name}", player.getName())
                        .replace("{player_display_name}", player.getDisplayName())
                        .replace("{player_uuid}", player.getUniqueId().toString())
                ));
                clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            }
            case "OPEN_URL" -> {
                String url = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("click-action.url-value")
                        .replace("{player_name}", player.getName())
                        .replace("{player_display_name}", player.getDisplayName())
                        .replace("{player_uuid}", player.getUniqueId().toString())
                ));
                clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            }
            case "COPY_TO_CLIPBOARD" -> {
                String text = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("click-action.text-value")
                        .replace("{player_name}", player.getName())
                        .replace("{player_display_name}", player.getDisplayName())
                        .replace("{player_uuid}", player.getUniqueId().toString())
                ));
                clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text);
            }
            default -> {
                throw new IllegalArgumentException("Invalid click action type!");
            }
        }

        component = new TextComponent(text);
        component.setHoverEvent(hoverEvent);
        component.setClickEvent(clickEvent);
    }

    private HoverEvent wrapEntity(Player player, String id, String type, String displayedName) {
        String entityUuid = null;
        if (player == null) entityUuid = UUID.randomUUID().toString();
        else {
            if (id.equals("{player_uuid}")) entityUuid = player.getUniqueId().toString();
            else if (id.equals("{random_uuid}")) entityUuid = UUID.randomUUID().toString();
            else throw new IllegalArgumentException("Invalid entity id!");
        }

        String entityType = type.toLowerCase();
        if (!entityType.startsWith("minecraft:")) entityType = "minecraft:" + entityType.replace(" ", "_").replace(".", "_");

        String entityName = Formatter.translate(PlaceholderAPI.setPlaceholders(player, displayedName));
        Entity entityContent = new Entity(entityType, entityUuid, new TextComponent(entityName));
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, entityContent);
    }

    private HoverEvent wrapItem(ConfigurationSection itemConf) {
        String material = itemConf.getString("material");
        int amount = itemConf.getInt("amount");
        String displayName = Formatter.translate(itemConf.getString("display-name"));
        List<String> description = itemConf.getStringList("description");
        int customModelData = itemConf.getInt("custom-model-data");
        boolean glowing = itemConf.getBoolean("glowing");

        StringBuilder nbtBuilder = new StringBuilder();
        nbtBuilder.append("{");

        if (!displayName.isEmpty() || !description.isEmpty()) {
            nbtBuilder.append("display:{");

            if (!displayName.isEmpty()) {
                nbtBuilder.append("Name:'{\"text\":\"").append(displayName).append("\"}',");
            }

            if (!description.isEmpty()) {
                nbtBuilder.append("Lore:[");
                for (int i = 0; i < description.size(); i++) {
                    nbtBuilder.append("\"{\\\"text\\\":\\\"").append(description.get(i)).append("\\\"}\",");
                }
                if (!description.isEmpty()) {
                    nbtBuilder.deleteCharAt(nbtBuilder.length() - 1);
                }
                nbtBuilder.append("],");
            }

            if (nbtBuilder.charAt(nbtBuilder.length() - 1) == ',') {
                nbtBuilder.deleteCharAt(nbtBuilder.length() - 1);
            }

            nbtBuilder.append("},");
        }

        if (customModelData > 0) {
            nbtBuilder.append("CustomModelData:").append(customModelData).append(",");
        }

        if (glowing) {
            nbtBuilder.append("Enchantments:[{id:\"minecraft:lure\",lvl:1}],HideFlags:1,");
        }

        if (nbtBuilder.charAt(nbtBuilder.length() - 1) == ',') {
            nbtBuilder.deleteCharAt(nbtBuilder.length() - 1);
        }

        nbtBuilder.append("}");

        HoverEvent result = null;

        try {
            Item itemContent = new Item(
                    "minecraft:" + material.toLowerCase(),
                    amount,
                    ItemTag.ofNbt(nbtBuilder.toString())
                    );

            result = new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemContent);
        } catch (Exception e) {
            String legacyJson = "{\"id\":\"minecraft:" + material.toLowerCase() + "\",\"count\":" + amount + ",\"tag\":" + nbtBuilder.toString() + "}";

            result = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(legacyJson).create()
            );
        }

        return result;
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