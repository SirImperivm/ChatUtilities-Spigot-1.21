package me.sirimperivm.chatUtilities.assets.handlers;

import me.sirimperivm.chatUtilities.ChatUtilities;
import me.sirimperivm.chatUtilities.assets.others.Logger;
import me.sirimperivm.chatUtilities.assets.strings.Formatter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Map;

@SuppressWarnings("all")
public class ConfigHandler {

    private ChatUtilities plugin;

    private File folder;

    private File settingsFile, messagesFile, chatFile;
    private FileConfiguration settings, messages, chat;

    public ConfigHandler(ChatUtilities plugin) {
        this.plugin = plugin;

        folder = plugin.getDataFolder();

        settingsFile = new File(folder, "settings.yml");
        settings = new YamlConfiguration();
        messagesFile = new File(folder, "messages.yml");
        messages = new YamlConfiguration();
        chatFile = new File(folder, "chat.yml");
        chat = new YamlConfiguration();

        if (!folder.exists()) folder.mkdirs();

        createAll();
        try {
            loadAll();
        } catch (IOException | InvalidConfigurationException e) {
            Logger.fail("Failed to load config files! Plugin will be disabled.");
            e.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private void create(File f) {
        String n = f.getName();
        if (f.exists()) return;

        try {
            Files.copy(plugin.getResource(n), f.toPath(), new CopyOption[0]);
        } catch (IOException e) {
            Logger.fail("Failed to create " + n + " file!");
            e.printStackTrace();
        }
    }

    public void save(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            c.save(f);
        } catch (IOException e) {
            Logger.fail("Failed to save " + n + " file!");
            e.printStackTrace();
        }
    }

    public void load(FileConfiguration c, File f) throws IOException, InvalidConfigurationException {
        String n = f.getName();
        c.load(f);
    }

    private void createAll() {
        create(settingsFile);
        create(messagesFile);
        create(chatFile);
    }

    public void saveAll() {
        save(settings, settingsFile);
        save(messages, messagesFile);
        save(chat, chatFile);
    }

    public void loadAll() throws IOException, InvalidConfigurationException {
        load(settings, settingsFile);
        load(messages, messagesFile);
        load(chat, chatFile);
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public File getChatFile() {
        return chatFile;
    }

    public FileConfiguration getChat() {
        return chat;
    }

    public static String getFormatString(String target, Map<String, String> placeholders) {
        return Formatter.format(ChatUtilities.getInstance().getPrefixer(), target, placeholders);
    }

    public static String getFormatString(FileConfiguration c, String path, Map<String, String> placeholders) {
        return Formatter.format(ChatUtilities.getInstance().getPrefixer(), c.getString(path), placeholders);
    }

    public static String getFormatString(FileConfiguration c, String path, String d, Map<String, String> placeholders) {
        return Formatter.format(ChatUtilities.getInstance().getPrefixer(), c.getString(path, d), placeholders);
    }
}
