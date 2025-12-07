package com.abyssbay.net.globalChat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

/**
 * Intercepts /msg command from Minecraft servers
 * and redirects to GlobalChat's PrivateMessageCommand
 */
public class MinecraftChatCommandHandler {
    private final ProxyServer proxyServer;
    private final ConfigManager configManager;

    public MinecraftChatCommandHandler(ProxyServer proxyServer, ConfigManager configManager) {
        this.proxyServer = proxyServer;
        this.configManager = configManager;
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        
        // Check if it's a /msg command (also catches /message, /m, /pm, /whisper)
        if (message.startsWith("/msg ") || message.startsWith("/message ") || 
            message.startsWith("/m ") || message.startsWith("/pm ") || 
            message.startsWith("/whisper ")) {
            
            // Block the command from being sent to the server
            event.setResult(PlayerChatEvent.ChatResult.denied());
            
            // Parse the command
            Player sender = event.getPlayer();
            String[] parts = message.split(" ", 3);
            
            if (parts.length < 3) {
                String msg = configManager.getMessage("usage-msg");
                sender.sendMessage(Component.text(msg != null && !msg.isEmpty() ? msg : 
                    "§cVerwendung: /msg <Spieler> <Nachricht>"));
                return;
            }
            
            String targetName = parts[1];
            String privateMessage = parts[2];
            
            // Find target player across all servers
            var targetOptional = proxyServer.getAllPlayers()
                .stream()
                .filter(p -> p.getUsername().equalsIgnoreCase(targetName))
                .findFirst();
            
            if (targetOptional.isEmpty()) {
                String msg = configManager.getMessage("player-not-online");
                String errorMsg = msg != null && !msg.isEmpty() ? msg : 
                    "§cDer Spieler <player> ist nicht online!";
                errorMsg = errorMsg.replace("<player>", targetName);
                sender.sendMessage(Component.text(errorMsg));
                return;
            }
            
            Player target = targetOptional.get();
            
            // Don't send PM to players on blacklisted servers
            String targetServerName = target.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("unknown");
            
            if (configManager.isBlacklisted(targetServerName)) {
                String msg = configManager.getMessage("player-blacklisted");
                String errorMsg = msg != null && !msg.isEmpty() ? msg : 
                    "§cDer Spieler <player> befindet sich auf einem deaktivierten Server!";
                errorMsg = errorMsg.replace("<player>", targetName);
                sender.sendMessage(Component.text(errorMsg));
                return;
            }
            
            // Get sender's current server name
            String senderServerName = sender.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("");
            
            // Send message to target
            String formatReceiver = configManager.getPrivateMsgFormatReceiver();
            String receiverMsg = formatReceiver
                .replace("<sender>", sender.getUsername())
                .replace("<server>", senderServerName)
                .replace("<message>", privateMessage);
            target.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(receiverMsg));
            
            // Send confirmation to sender
            String formatSender = configManager.getPrivateMsgFormatSender();
            String senderMsg = formatSender
                .replace("<recipient>", target.getUsername())
                .replace("<message>", privateMessage);
            sender.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(senderMsg));
            
            // Update last messager
            PrivateMessageCommand.setLastMessager(target.getUsername(), sender.getUsername());
        }
    }
}
