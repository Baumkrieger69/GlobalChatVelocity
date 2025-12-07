package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SocialSpyCommand implements SimpleCommand {
    private final GlobalChatPlugin plugin;
    private final ConfigManager configManager;
    private static final Set<java.util.UUID> socialSpyEnabled = ConcurrentHashMap.newKeySet();
    private static GlobalChatPlugin staticPlugin;

    public SocialSpyCommand(GlobalChatPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        staticPlugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    configManager.getMessage("no-permission")));
            return;
        }

        Player player = (Player) source;

        // Permission check
        if (!PermissionProvider.canUseSocialSpy(player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    configManager.getMessage("no-permission")));
            return;
        }

        // Toggle SocialSpy
        if (socialSpyEnabled.contains(player.getUniqueId())) {
            socialSpyEnabled.remove(player.getUniqueId());
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    configManager.getMessage("socialspy-disabled")));
            plugin.getLogger().info(player.getUsername() + " hat SocialSpy deaktiviert");
        } else {
            socialSpyEnabled.add(player.getUniqueId());
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    configManager.getMessage("socialspy-enabled")));
            plugin.getLogger().info(player.getUsername() + " hat SocialSpy aktiviert");
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            return false;
        }
        Player player = (Player) invocation.source();
        return PermissionProvider.canUseSocialSpy(player);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return Collections.emptyList();
    }

    public static boolean isSocialSpyEnabled(java.util.UUID playerUuid) {
        return socialSpyEnabled.contains(playerUuid);
    }

    public static Set<java.util.UUID> getSocialSpyPlayers() {
        return new java.util.HashSet<>(socialSpyEnabled);
    }

    public static void broadcastPrivateMessage(String sender, String recipient, String message, String server, ConfigManager configManager) {
        if (staticPlugin == null || configManager == null) {
            return;
        }

        String socialspyFormat = configManager.getMessage("socialspy-format");
        if (socialspyFormat == null || socialspyFormat.isEmpty()) {
            socialspyFormat = "<#555555>[<#C7FFD8>SS<#555555>] <#AAAAAA><sender> <#555555>-> <#AAAAAA><recipient><#555555>: <#FFFFFF><message>";
        }

        String formattedMessage = socialspyFormat
                .replace("<sender>", sender)
                .replace("<recipient>", recipient)
                .replace("<message>", message)
                .replace("<server>", server);

        var proxyServer = staticPlugin.getProxyServer();
        for (java.util.UUID spyUuid : socialSpyEnabled) {
            var spyPlayer = proxyServer.getPlayer(spyUuid);
            if (spyPlayer.isPresent()) {
                Player spy = spyPlayer.get();
                // Don't spam the participants
                if (!spy.getUsername().equals(sender) && !spy.getUsername().equals(recipient)) {
                    spy.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage));
                }
            }
        }
    }
}
