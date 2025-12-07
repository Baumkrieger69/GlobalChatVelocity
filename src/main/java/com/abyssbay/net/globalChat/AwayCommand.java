package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class AwayCommand implements SimpleCommand {
    private final GlobalChatPlugin plugin;
    private final ConfigManager configManager;
    private static final ConcurrentHashMap<java.util.UUID, String> awayPlayers = new ConcurrentHashMap<>();

    public AwayCommand(GlobalChatPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    configManager.getMessage("no-permission")));
            return;
        }

        Player player = (Player) source;

        if (args.length == 0) {
            // Toggle mit Standardgrund
            if (awayPlayers.containsKey(player.getUniqueId())) {
                awayPlayers.remove(player.getUniqueId());
                source.sendMessage(MiniMessage.miniMessage().deserialize(
                        configManager.getMessage("away-disabled")));
                plugin.getLogger().info(player.getUsername() + " ist nicht mehr weg");
            } else {
                String reason = configManager.getAwayDefaultReason();
                awayPlayers.put(player.getUniqueId(), reason);
                String msg = configManager.getMessage("away-enabled")
                        .replace("<reason>", reason);
                source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
                plugin.getLogger().info(player.getUsername() + " ist weg: " + reason);
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            // Away deaktivieren
            if (awayPlayers.containsKey(player.getUniqueId())) {
                awayPlayers.remove(player.getUniqueId());
                source.sendMessage(MiniMessage.miniMessage().deserialize(
                        configManager.getMessage("away-disabled")));
                plugin.getLogger().info(player.getUsername() + " ist nicht mehr weg");
            } else {
                source.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<#FFD700>Du bist nicht weg!"));
            }
        } else {
            // Mit Grund
            String reason = String.join(" ", args);
            awayPlayers.put(player.getUniqueId(), reason);
            String msg = configManager.getMessage("away-enabled")
                    .replace("<reason>", reason);
            source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            plugin.getLogger().info(player.getUsername() + " ist weg: " + reason);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source() instanceof Player;
    }

    @Override
    public java.util.List<String> suggest(Invocation invocation) {
        return java.util.Collections.emptyList();
    }

    public static boolean isAway(java.util.UUID playerUuid) {
        return awayPlayers.containsKey(playerUuid);
    }

    public static String getAwayReason(java.util.UUID playerUuid) {
        return awayPlayers.getOrDefault(playerUuid, "AFK");
    }

    public static void setAway(java.util.UUID playerUuid, String reason) {
        awayPlayers.put(playerUuid, reason);
    }

    public static void clearAway(java.util.UUID playerUuid) {
        awayPlayers.remove(playerUuid);
    }
}
