package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ReloadCommand implements SimpleCommand {
    private final ConfigManager configManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        // Only console can reload
        if (invocation.source() instanceof Player) {
            invocation.source().sendMessage(miniMessage.deserialize(
                "<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> " +
                "<#808080>| <#FFD700>Du hast keine Berechtigung dafür!"
            ));
            return;
        }

        // Reload config
        try {
            configManager.reloadConfig();
            invocation.source().sendMessage(Component.text("§a[GlobalChat] Konfiguration erfolgreich neu geladen!"));
        } catch (Exception e) {
            invocation.source().sendMessage(Component.text("§c[GlobalChat] Fehler beim Laden der Konfiguration: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Only console has permission
        return !(invocation.source() instanceof Player);
    }
}
