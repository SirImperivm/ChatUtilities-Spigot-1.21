package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sirimperivm.chatUtilities.assets.others.Strings;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class ChatGroupSection {

    private ConfigurationSection config;

    private String key;
    private String text;

    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;

    private TextComponent component;

    public ChatGroupSection(String key, ConfigurationSection config) {
        this.key = key;
        this.config = config;
    }

    public void createComponent(Player player) {
        String playerName = player.getName();
        UUID playerUuid = player.getUniqueId();
        String playerUuidString = playerUuid.toString();
        Entity playerEntity = player;
        String playerEntityType = playerEntity.getType().toString().toLowerCase();
        String playerEntityName = playerEntity.getName();

        text = config.getString("text");
        component = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, text)));

        ConfigurationSection hoverEventSection = config.getConfigurationSection("hover-action");
        ConfigurationSection clickEventSection = config.getConfigurationSection("click-action");

        if (hoverEventSection != null && !hoverEventSection.getKeys(false).isEmpty()) {
            String type = hoverEventSection.getString("type").toUpperCase();

            switch (type) {
                case "SHOW_ITEM":
                    ConfigurationSection itemSection = hoverEventSection.getConfigurationSection("item-value");
                    if (itemSection == null || itemSection.getKeys(false).isEmpty()) throw new IllegalArgumentException("Hover event item section is empty!");
                    ItemStack item = new ItemStack(Material.getMaterial(itemSection.getString("material", "DIAMOND")), itemSection.getInt("amount", 64));
                    ItemMeta meta = item.getItemMeta();
                    String displayName = itemSection.getString("display-name");
                    if (displayName != null && !displayName.isEmpty()) meta.setDisplayName(Formatter.translate(PlaceholderAPI.setPlaceholders(player, displayName)));
                    List<String> description = itemSection.getStringList("description");
                    List<String> lore = new ArrayList<>();
                    if (description != null && !description.isEmpty()) {
                        for (String desc : description) {
                            lore.add(Formatter.translate(PlaceholderAPI.setPlaceholders(player, desc)));
                        }
                        meta.setLore(lore);
                    }
                    int customModelData = itemSection.getInt("custom-model-data", 1000);
                    meta.setCustomModelData(customModelData);
                    if (itemSection.getBoolean("glowing", false)) {
                        meta.addEnchant(Enchantment.LURE, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    item.setItemMeta(meta);

                    String itemJson = translateItemStackJson(item);
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(itemJson).create());
                    break;
                case "SHOW_ENTITY":
                    ConfigurationSection entitySection = hoverEventSection.getConfigurationSection("entity-value");
                    if (entitySection == null || entitySection.getKeys(false).isEmpty()) throw new IllegalArgumentException("Hover event entity section is empty!");

                    String entityId = entitySection.getString("id");

                    boolean notValidId = entityId == null || entityId.isEmpty() || !entityId.equals("{player_uuid}") || entityId.length() != 36 || Strings.getCountOf("-", entityId) != 4;
                    if (notValidId) {
                        entityId = UUID.randomUUID().toString();
                    } else {
                        entityId = entityId.replace("{player_uuid}", playerUuidString);
                    }

                    String entityType = entitySection.getString("type")
                            .replace("{player_entity}", playerEntityName);
                    String entityName = entitySection.getString("name")
                            .replace("{player_entity_name}", playerEntityName);

                    String entityJson = "{\"id\":\"" + entityId + "\",\"type\":\"" + entityType + "\",\"name\":\"" + entityName + "\"}";

                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ComponentBuilder(entityJson).create());
                    break;
                case "SHOW_TEXT":
                    String textValue = hoverEventSection.getString("text-value");
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Formatter.translate(PlaceholderAPI.setPlaceholders(player, textValue)
                            .replace("{player_name}", playerName)
                            .replace("{player_uuid}", playerUuidString)
                    )).create());
                    break;
                default:
                    throw new IllegalArgumentException("Hover event type " + type + " is not allowed!");
            }

            component.setHoverEvent(hoverEvent);
        }

        if (clickEventSection != null && !clickEventSection.getKeys(false).isEmpty()) {
            String type = clickEventSection.getString("type").toUpperCase();

            switch (type) {
                case "COPY_TO_CLIPBOARD" -> {
                    String copyValue = clickEventSection.getString("copy-value");
                    clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Formatter.translate(PlaceholderAPI.setPlaceholders(player, copyValue)));
                }
                case "OPEN_URL" -> {
                    String urlValue = clickEventSection.getString("url-value");
                    clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Formatter.translate(PlaceholderAPI.setPlaceholders(player, urlValue)));
                }
                case "RUN_COMMAND" -> {
                    String commandValue = clickEventSection.getString("command-value");
                    clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, Formatter.translate(PlaceholderAPI.setPlaceholders(player, commandValue)));
                }
                case "SUGGEST_COMMAND" -> {
                    String commandValue = clickEventSection.getString("command-value");
                    clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, Formatter.translate(PlaceholderAPI.setPlaceholders(player, commandValue)));
                }
                default -> {
                    throw new IllegalArgumentException("Click event type " + type + " is not allowed!");
                }
            }

            component.setClickEvent(clickEvent);
        }
    }

    public static String translateItemStackJson(ItemStack itemStack) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");

            Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItemStack = asNMSCopyMethod.invoke(null, itemStack);

            Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.nbt.NBTTagCompound");
            Object nbtTagCompound = nbtTagCompoundClass.getDeclaredConstructor().newInstance();

            Method saveMethod = nmsItemStack.getClass().getMethod("save", nbtTagCompoundClass);
            Object nbtData = saveMethod.invoke(nmsItemStack, nbtTagCompound);

            return nbtData.toString();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." +
                        Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] +
                        ".inventory.CraftItemStack");

                Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
                Object nmsItemStack = asNMSCopyMethod.invoke(null, itemStack);

                Class<?> compoundClass = Class.forName("net.minecraft.nbt.CompoundTag");
                Object compound = compoundClass.getDeclaredConstructor().newInstance();

                Method saveMethod = nmsItemStack.getClass().getMethod("save", compoundClass);
                Object nbtData = saveMethod.invoke(nmsItemStack, compound);

                return nbtData.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                return "{}";
            }
        }
    }

    public TextComponent getComponent() {
        return component;
    }
}
