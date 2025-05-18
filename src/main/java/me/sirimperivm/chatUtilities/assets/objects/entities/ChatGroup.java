package me.sirimperivm.chatUtilities.assets.objects.entities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import me.sirimperivm.chatUtilities.assets.managers.ChatManager;
import me.sirimperivm.chatUtilities.assets.objects.enums.Permission;
import me.sirimperivm.chatUtilities.assets.objects.exceptions.ChatMessageException;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class ChatGroup {

    private ChatManager chatManager;
    private ConfigHandler configHandler;

    private int weight;
    private String name;
    private boolean def;

    private HashMap<String, ChatGroupSection> sections;
    private ConfigurationSection genericSettings;

    public ChatGroup(ChatManager chatManager, int weight, String name, boolean def) {
        this.chatManager = chatManager;
        this.weight = weight;
        this.name = name;
        this.def = def;
        
        configHandler = chatManager.getConfigHandler();

        configure();
    }

    public void configure() {
        sections = new HashMap<>();
        for (String key : configHandler.getChat().getConfigurationSection(name + ".chat-sections").getKeys(false)) {
            ChatGroupSection section = new ChatGroupSection(this, key);
            sections.put(key, section);
        }
    }

    public TextComponent getMessage(Player player, String baseMessage) throws ChatMessageException {
        ConfigurationSection config = configHandler.getChat().getConfigurationSection(name);
        if (config == null || config.getKeys(false).isEmpty()) throw new IllegalArgumentException("Chat group does not exist!");

        ConfigurationSection genericSettings = configHandler.getChat().getConfigurationSection(name + ".generic-settings");
        if (genericSettings == null || genericSettings.getKeys(false).isEmpty()) throw new IllegalArgumentException("Generic settings section does not exist!");
        this.genericSettings = genericSettings;

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

        if (sectionCount == 0) {
            mainComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, chatFormat)));
        } else {
            matcher.reset();

            int lastEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastEnd) {
                    String textBefore = chatFormat.substring(lastEnd, matcher.start());
                    TextComponent textComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, textBefore)));
                    mainComponent.addExtra(textComponent);
                }

                String sectionName = matcher.group(1);
            
                if (!sectionName.equals("message")) {
                    ChatGroupSection section = sections.get(sectionName);
                    if (section != null) {
                        section.configure(player);
                        TextComponent sectionComponent = section.getComponent();
                        mainComponent.addExtra(sectionComponent);
                    }
                }

                lastEnd = matcher.end();
            }

            if (lastEnd < chatFormat.length()) {
                String remainingText = chatFormat.substring(lastEnd);
                TextComponent remainingComponent = new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, remainingText)));
                mainComponent.addExtra(remainingComponent);
            }
        }

        TextComponent processedBaseMessage = processBaseMessage(player, baseMessage);
        mainComponent.addExtra(processedBaseMessage);
    
        return mainComponent;
    }

    private TextComponent processBaseMessage(Player player, String baseMessage) throws ChatMessageException {
        boolean hasColorPermission = player.hasPermission(Permission.GLOBAL_PERMISSION.getNode()) || player.hasPermission(Permission.CHAT_COLORED.getNode());

        String[] specialChars = { "&k", "&l", "&m", "&n", "&o", "§k", "§l", "§m", "§n", "§o" };
        boolean hasSpecialPermission = player.hasPermission(Permission.GLOBAL_PERMISSION.getNode()) || player.hasPermission(Permission.CHAT_SPECIAL.getNode());

        boolean hasHexPermission = player.hasPermission(Permission.GLOBAL_PERMISSION.getNode()) || player.hasPermission(Permission.CHAT_HEX.getNode());

        if ((baseMessage.contains("&") || baseMessage.contains("§")) && !hasColorPermission) throw new ChatMessageException("chat-exceptions.colored");

        boolean containsSpecialChars = false;
        for (String word : baseMessage.split(" ")) {
            for (String specialChar : specialChars) {
                if (word.contains(specialChar)) {
                    containsSpecialChars = true;
                    break;
                }
            }
            if (containsSpecialChars) break;
        }

        if (containsSpecialChars && !hasSpecialPermission) throw new ChatMessageException("chat-exceptions.special");
        if (baseMessage.contains("&#") && !hasHexPermission) throw new ChatMessageException("chat-exceptions.hex");
        
        String baseChatColor = genericSettings.getString("chat-color");
        
        boolean chatItemEnabled = configHandler.getSettings().getBoolean("chat-item.enabled", true);
        if (chatItemEnabled) {
            String triggerPatternString = configHandler.getSettings().getString("chat-item.trigger-pattern", "[i]");
            boolean hasChatItemPermission = player.hasPermission(Permission.GLOBAL_PERMISSION.getNode()) || player.hasPermission(Permission.CHATITEM_USE.getNode());
            
            if (baseMessage.contains(triggerPatternString) && hasChatItemPermission) {
                ItemStack item = getItem(player);
                if (item == null) throw new ChatMessageException("chat-exceptions.empty-hand");
                
                String itemShowingFormat = genericSettings.getString("item-showing-format");
                String formattedItemText = Formatter.translate(itemShowingFormat.replace("{default-item-display-name}",
                    item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString()));
                
                TextComponent itemComponent = new TextComponent(formattedItemText);
                itemComponent.setHoverEvent(wrapItem(item));
                
                TextComponent finalComponent = new TextComponent();
                
                String[] parts = baseMessage.split(Pattern.quote(triggerPatternString), -1);
                
                for (int i = 0; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) {
                        finalComponent.addExtra(new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, baseChatColor + parts[i]))));
                    }
                    
                    if (i < parts.length - 1) {
                        finalComponent.addExtra(itemComponent);
                    }
                }
                
                return finalComponent;
            }
        }

        return new TextComponent(Formatter.translate(PlaceholderAPI.setPlaceholders(player, baseChatColor + baseMessage)));
    }

    public HoverEvent wrapEntity(Player player, String id, String type, String displayedName) {
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

    public HoverEvent wrapItem(ItemStack item) {
        String material = item.getType().toString();
        int amount = item.getAmount();
        ItemMeta meta = item.getItemMeta();
        String displayName = meta.hasDisplayName() ? Formatter.translate(meta.getDisplayName()) : "";
        List<String> description = meta.hasLore() ? Formatter.translate(meta.getLore()) : new ArrayList<>();
        int customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;

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
    
    public void sendChatSound() {
        boolean enabled = configHandler.getSettings().getBoolean("chat-sounds.enabled", true);
        if (!enabled) return;
        
        String soundId = configHandler.getSettings().getString("chat-sounds.sound", "ui.button.click");
        Sound sound = getSound(soundId);
        if (sound == null) return;
        
        float volume = (float) configHandler.getSettings().getDouble("chat-sounds.volume", 1.0);
        float pitch = (float) configHandler.getSettings().getDouble("chat-sounds.pitch", 1.0);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public Sound getSound(String soundId) {
        String soundIdLower = soundId.toLowerCase();
        String soundName;
        
        if (soundIdLower.contains("minecraft:")) {
            String[] splitter = soundIdLower.split(":");
            if (splitter.length < 2) {
                return null;
            }
            soundName = splitter[1];
        } else {
            soundName = soundIdLower;
        }
        
        NamespacedKey namespacedKey = NamespacedKey.minecraft(soundName);
        Registry<Sound> soundRegistry = Registry.SOUNDS;
        return soundRegistry.get(namespacedKey);
    }
    
    private ItemStack getItem(Player player) {
        if (player.getInventory().isEmpty()) return null;
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) item = player.getInventory().getItemInOffHand();
        
        return item;
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

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}