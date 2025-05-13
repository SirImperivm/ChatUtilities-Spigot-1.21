package me.sirimperivm.chatUtilities.assets.strings;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.handlers.ConfigHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

@SuppressWarnings("all")
public class Prefixer {

    private ChatUtilities plugin;
    private ConfigHandler configHandler;

    private FileConfiguration settings;

    private HashMap<String, Prefix> prefixes = new HashMap<>();

    public Prefixer(ChatUtilities plugin) {
        this.plugin = plugin;
        configHandler = plugin.getConfigHandler();

        settings = configHandler.getSettings();

        for (String prefixSetting : settings.getConfigurationSection("prefixes").getKeys(false) ) {
            String prefixString = settings.getString("prefixes." + prefixSetting + ".prefix-string");
            String colorCode = settings.getString("prefixes." + prefixSetting + ".color-code");
            String placeholdersColor = settings.getString("prefixes." + prefixSetting + ".placeholders-color");

            prefixes.put(prefixSetting, new Prefix(prefixString, colorCode, placeholdersColor));
        }
    }

    public String getPrefixString(String prefixTag) {
        if (!prefixes.containsKey(prefixTag)) throw new IllegalArgumentException("Prefix tag does not exist!");
        return prefixes.get(prefixTag).getPrefixString();
    }

    public String getColorCode(String prefixTag) {
        if (!prefixes.containsKey(prefixTag)) throw new IllegalArgumentException("Prefix tag does not exist!");
        return prefixes.get(prefixTag).getColorCode();
    }

    public String getPlaceholdersColor(String prefixTag) {
        if (!prefixes.containsKey(prefixTag)) throw new IllegalArgumentException("Prefix tag does not exist!");
        return prefixes.get(prefixTag).getPlaceholdersColor();
    }
}

@SuppressWarnings("all")
class Prefix{
    private String prefixString, colorCode, placeholdersColor;

    public Prefix(String prefixString, String colorCode, String placeholdersColor) {
        this.prefixString = prefixString;
        this.colorCode = colorCode;
        this.placeholdersColor = placeholdersColor;
    }

    public String getPrefixString() {
        return prefixString;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getPlaceholdersColor() {
        return placeholdersColor;
    }
}
