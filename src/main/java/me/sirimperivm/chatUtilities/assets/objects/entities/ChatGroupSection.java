package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@SuppressWarnings("all")
public class ChatGroupSection {


    private ChatGroup chatGroup;
    private ConfigHandler configHandler;

    private String key;
    private String text;

    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;

    private TextComponent component;

    public ChatGroupSection(ChatGroup chatGroup, String key) {
        this.chatGroup = chatGroup;
        this.key = key;

        configHandler = chatGroup.getConfigHandler();
    }

    public void configure(Player player) {
        ConfigurationSection section = configHandler.getChat().getConfigurationSection(chatGroup.getName() + ".chat-sections." + key);
        if (section == null || section.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat group section does not exist!");

        text = Formatter.translate(PlaceholderAPI.setPlaceholders(player, section.getString("text")
                .replace("{player_name}", player.getName())
                .replace("{player_display_name}", player.getDisplayName())
                .replace("{player_uuid}", player.getUniqueId().toString())
        ));

        component = new TextComponent(text);

        ConfigurationSection hoverAction = section.getConfigurationSection("hover-action");
        ConfigurationSection clickAction = section.getConfigurationSection("click-action");

        if (hoverAction != null && !hoverAction.getKeys(false).isEmpty()) {
            String hoverActionType = section.getString("hover-action.type").toUpperCase();

            switch (hoverActionType) {
                case "SHOW_ITEM" -> {
                    String materialName = section.getString("hover-action.item-value.material");
                    int amount = section.getInt("hover-action.item-value.amount");
                    String displayName = section.getString("hover-action.item-value.display-name");
                    List<String> description = section.getStringList("hover-action.item-value.description");
                    int customModelData = section.getInt("hover-action.item-value.custom-model-data");

                    ItemStack item = new ItemStack(Material.getMaterial(materialName), amount);
                    ItemMeta meta = item.getItemMeta();
                    if (displayName != null && !displayName.isEmpty()) meta.setDisplayName(displayName);
                    if (description != null && !description.isEmpty()) meta.setLore(description);
                    if (customModelData != 0) meta.setCustomModelData(customModelData);
                    item.setItemMeta(meta);

                    hoverEvent = chatGroup.wrapItem(item);
                }
                case "SHOW_ENTITY" -> {
                    hoverEvent = chatGroup.wrapEntity(player, section.getString("hover-action.entity-value.id"), section.getString("hover-action.entity-value.type"), section.getString("hover-action.entity-value.name")
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

            component.setHoverEvent(hoverEvent);
        }

        if (clickAction != null && !clickAction.getKeys(false).isEmpty()) {
            String clickActionType = section.getString("click-action.type").toUpperCase();

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

            component.setClickEvent(clickEvent);
        }
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