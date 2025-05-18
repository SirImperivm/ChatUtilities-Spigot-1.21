package me.sirimperivm.chatUtilities.assets.objects.enums;

import me.sirimperivm.chatUtilities.ChatUtilities;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("all")
public enum Config {

    settings(ChatUtilities.getInstance().getConfigHandler().getSettings(), ChatUtilities.getInstance().getConfigHandler().getSettingsFile()),
    messages(ChatUtilities.getInstance().getConfigHandler().getMessages(), ChatUtilities.getInstance().getConfigHandler().getMessagesFile()),
    chat(ChatUtilities.getInstance().getConfigHandler().getChat(), ChatUtilities.getInstance().getConfigHandler().getChatFile());

    private FileConfiguration c;
    private File f;

    Config(FileConfiguration c, File f) {
        this.c = c;
        this.f = f;
    }

    public FileConfiguration getC() {
        return c;
    }

    public File getF() {
        return f;
    }
}
