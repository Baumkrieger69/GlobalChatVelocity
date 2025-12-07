package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IgnoreCommand implements SimpleCommand {
    private final GlobalChatPlugin plugin;
    private final ConfigManager configManager;
    private final ProxyServer proxyServer;
    private static final ConcurrentHashMap<java.util.UUID, Set<java.util.UUID>> ignoreMap = new ConcurrentHashMap<>();

    public IgnoreCommand(GlobalChatPlugin plugin, ConfigManager configManager, ProxyServer proxyServer) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.proxyServer = proxyServer;
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
            // Show ignored list
            Set<java.util.UUID> ignoredSet = ignoreMap.getOrDefault(player.getUniqueId(), new HashSet<>());
            if (ignoredSet.isEmpty()) {
                source.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<#FFD700>Du ignorierst niemanden!"));
            } else {
                StringBuilder sb = new StringBuilder("<#FFD700>Ignorierte Spieler: ");
                ignoredSet.forEach(uuid -> {
                    var ignoredPlayer = proxyServer.getPlayer(uuid);
                    if (ignoredPlayer.isPresent()) {
                        sb.append(ignoredPlayer.get().getUsername()).append(", ");
                    }
                });
                // Remove trailing ", "
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                }
                source.sendMessage(MiniMessage.miniMessage().deserialize(sb.toString()));
            }
            return;
        }

        String targetName = args[0];
        var targetPlayer = proxyServer.getPlayer(targetName);

        if (targetPlayer.isEmpty()) {
            String msg = configManager.getMessage("player-not-online")
                    .replace("<player>", targetName);
            source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return;
        }

        Player target = targetPlayer.get();
        Set<java.util.UUID> ignoredSet = ignoreMap.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());

        if (ignoredSet.contains(target.getUniqueId())) {
            // Remove from ignore
            ignoredSet.remove(target.getUniqueId());
            String msg = configManager.getMessage("ignore-removed")
                    .replace("<player>", target.getUsername());
            source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            plugin.getLogger().info(player.getUsername() + " ignoriert " + target.getUsername() + " nicht mehr");
        } else {
            // Add to ignore
            ignoredSet.add(target.getUniqueId());
            String msg = configManager.getMessage("ignore-added")
                    .replace("<player>", target.getUsername());
            source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            plugin.getLogger().info(player.getUsername() + " ignoriert jetzt " + target.getUsername());
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source() instanceof Player;
    }

    @Override
    public java.util.List<String> suggest(Invocation invocation) {
        if (invocation.arguments().length == 0) {
            Collection<Player> players = proxyServer.getAllPlayers();
            java.util.List<String> suggestions = new ArrayList<>();
            for (Player player : players) {
                suggestions.add(player.getUsername());
            }
            return suggestions;
        }
        return java.util.Collections.emptyList();
    }

    public static boolean isIgnoring(java.util.UUID ignoringPlayer, java.util.UUID targetPlayer) {
        Set<java.util.UUID> ignoredSet = ignoreMap.getOrDefault(ignoringPlayer, new HashSet<>());
        return ignoredSet.contains(targetPlayer);
    }

    public static void addIgnore(java.util.UUID ignoringPlayer, java.util.UUID targetPlayer) {
        ignoreMap.computeIfAbsent(ignoringPlayer, k -> ConcurrentHashMap.newKeySet()).add(targetPlayer);
    }

    public static void removeIgnore(java.util.UUID ignoringPlayer, java.util.UUID targetPlayer) {
        Set<java.util.UUID> ignoredSet = ignoreMap.get(ignoringPlayer);
        if (ignoredSet != null) {
            ignoredSet.remove(targetPlayer);
        }
    }
}
