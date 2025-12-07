package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.stream.Collectors;

public class PrivateMessageCommand implements SimpleCommand {
    private final ProxyServer proxyServer;
    private final ConfigManager configManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    // Store last message sender for /reply command
    private static final Map<String, String> lastMessager = new HashMap<>();
    // Store who we last sent a message to (for /reply fallback)
    private static final Map<String, String> lastSent = new HashMap<>();

    public PrivateMessageCommand(ProxyServer proxyServer, ConfigManager configManager) {
        this.proxyServer = proxyServer;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        // Only players can use this command
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("§cNur Spieler können Nachrichten senden!"));
            return;
        }

        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();

        // Check arguments
        if (args.length < 2) {
            String msg = configManager.getMessage("usage-msg");
            sender.sendMessage(miniMessage.deserialize(msg != null && !msg.isEmpty() ? msg : 
                "§cVerwendung: /msg <Spieler> <Nachricht>"));
            return;
        }

        String targetName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Check if sender is on a blacklisted server
        String senderServerName = sender.getCurrentServer()
            .map(server -> server.getServerInfo().getName())
            .orElse("");
        
        if (configManager.isBlacklisted(senderServerName)) {
            String msg = configManager.getMessage("blacklist-error-sender");
            String errorMsg = msg != null && !msg.isEmpty() ? msg : "§cDu kannst von diesem Server aus keine Nachrichten senden!";
            sender.sendMessage(miniMessage.deserialize(errorMsg));
            return;
        }

        // Find target player (search across all servers)
        Optional<Player> targetOptional = proxyServer.getAllPlayers()
            .stream()
            .filter(p -> p.getUsername().equalsIgnoreCase(targetName))
            .findFirst();

        if (targetOptional.isEmpty()) {
            String msg = configManager.getMessage("player-not-online");
            String errorMsg = msg != null && !msg.isEmpty() ? msg : "§cDer Spieler <player> ist nicht online!";
            errorMsg = errorMsg.replace("<player>", targetName);
            sender.sendMessage(miniMessage.deserialize(errorMsg));
            return;
        }

        Player target = targetOptional.get();

        // Don't send PM to players on blacklisted servers
        String targetServerName = target.getCurrentServer()
            .map(server -> server.getServerInfo().getName())
            .orElse("unknown");
        
        if (configManager.isBlacklisted(targetServerName)) {
            String msg = configManager.getMessage("player-blacklisted");
            String errorMsg = msg != null && !msg.isEmpty() ? msg : "§cDer Spieler <player> befindet sich auf einem deaktivierten Server!";
            errorMsg = errorMsg.replace("<player>", targetName);
            sender.sendMessage(miniMessage.deserialize(errorMsg));
            return;
        }

        // Check if target is ignoring the sender
        if (IgnoreCommand.isIgnoring(target.getUniqueId(), sender.getUniqueId())) {
            String msg = configManager.getMessage("player-ignored");
            String errorMsg = msg != null && !msg.isEmpty() ? msg : "§cDer Spieler <player> hat dich ignoriert!";
            errorMsg = errorMsg.replace("<player>", targetName);
            sender.sendMessage(miniMessage.deserialize(errorMsg));
            return;
        }

        // Check if target is away
        if (AwayCommand.isAway(target.getUniqueId())) {
            String awayReason = AwayCommand.getAwayReason(target.getUniqueId());
            String msg = configManager.getMessage("player-away");
            String awayMsg = msg != null && !msg.isEmpty() ? msg : "§cDer Spieler <player> ist gerade nicht verfügbar: <reason>";
            awayMsg = awayMsg.replace("<player>", target.getUsername())
                    .replace("<reason>", awayReason);
            sender.sendMessage(miniMessage.deserialize(awayMsg));
        }

        // Get sender's current server name (already retrieved above)

        // Send message to target
        String formatReceiver = configManager.getPrivateMsgFormatReceiver();
        String receiverMsg = formatReceiver
            .replace("<sender>", sender.getUsername())
            .replace("<server>", senderServerName)
            .replace("<message>", message);
        target.sendMessage(miniMessage.deserialize(receiverMsg));

        // Send confirmation to sender
        String formatSender = configManager.getPrivateMsgFormatSender();
        String senderMsg = formatSender
            .replace("<recipient>", target.getUsername())
            .replace("<message>", message);
        sender.sendMessage(miniMessage.deserialize(senderMsg));

        // Store for /reply
        lastMessager.put(target.getUsername(), sender.getUsername());
        lastSent.put(sender.getUsername(), target.getUsername());

        // Send to Discord webhook
        DiscordWebhookHandler.sendPrivateMessage(sender.getUsername(), target.getUsername(), message, senderServerName, configManager);

        // Broadcast to SocialSpy
        SocialSpyCommand.broadcastPrivateMessage(
            sender.getUsername(), 
            target.getUsername(), 
            message, 
            senderServerName, 
            configManager
        );
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Everyone can use this command
        return true;
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        // Tab completion for all player names (including from blacklisted servers)
        if (args.length == 1) {
            return proxyServer.getAllPlayers()
                .stream()
                .map(Player::getUsername)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
    
    public static String getLastMessager(String playerName) {
        return lastMessager.get(playerName);
    }
    
    public static void setLastMessager(String playerName, String senderName) {
        lastMessager.put(playerName, senderName);
    }
    
    public static String getLastSent(String playerName) {
        return lastSent.get(playerName);
    }
    
    public static void setLastSent(String playerName, String targetName) {
        lastSent.put(playerName, targetName);
    }
}
