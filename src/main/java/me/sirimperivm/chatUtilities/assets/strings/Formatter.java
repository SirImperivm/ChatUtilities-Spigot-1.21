package me.sirimperivm.chatUtilities.assets.strings;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class Formatter {

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String translate(String s, Map<String, String> placeholders) {
        if (s == null) return "";
        if (placeholders == null || placeholders.isEmpty()) return translate(s);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return translate(s);
    }

    public static List<String> translate(List<String> l) {
        if (l == null || l.isEmpty()) return new ArrayList<>();
        List<String> translated = new ArrayList<>();
        for (String s : l) {
            translated.add(translate(s));
        }
        return translated;
    }

    public static List<String> translate(List<String> l, Map<String, String> placeholders) {
        if (l == null || l.isEmpty()) return new ArrayList<>();
        List<String> translated = new ArrayList<>();
        for (String s : l) {
            translated.add(translate(s, placeholders));
        }
        return translated;
    }

    public static String format(Prefixer p, String s, Map<String, String> placeholders) {
        if (s == null) return "";
        
        if (s.matches("^[a-z-]+/.*")) {
            String[] prefixSplitter = s.split("/");

            String prefixTag = prefixSplitter[0];
            String message = prefixSplitter[1];

            String prefix = p.getPrefixString(prefixTag) + p.getColorCode(prefixTag);

            if (placeholders != null && !placeholders.isEmpty()) {
                String colorCode = p.getColorCode(prefixTag);
                String placeholdersColor = p.getPlaceholdersColor(prefixTag);

                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    message = message.replace(key, placeholdersColor + value + colorCode);
                }
            }

            s = prefix + message;
        } else {
            String message = s;
            if (placeholders != null && !placeholders.isEmpty()) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    message = message.replace(key, value);
                }
            }

            s = message;
        }

        return translate(s);
    }
}