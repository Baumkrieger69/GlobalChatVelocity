package com.abyssbay.net.globalChat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatListener {
    private final ProxyServer proxyServer;
    private final ConfigManager configManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatListener(ProxyServer proxyServer, ConfigManager configManager) {
        this.proxyServer = proxyServer;
        this.configManager = configManager;
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String serverName = player.getCurrentServer()
            .map(server -> server.getServerInfo().getName())
            .orElse("unknown");

        // Wenn der Server blackgelisted ist, nur lokal behalten
        if (configManager.isBlacklisted(serverName)) {
            // Message bleibt nur auf dem aktuellen Server
            return;
        }

        String message = event.getMessage();
        String playerName = player.getUsername();
        String format = configManager.getGlobalChatFormat();
        
        // Format: <player>, <message>, <servername>
        String formattedMessage = format
            .replace("<player>", playerName)
            .replace("<message>", message);
        
        Component chatComponent = miniMessage.deserialize(formattedMessage);

        // An alle Spieler AUSSER auf dem gleichen Server senden
        // (verhindert Duplikate)
        proxyServer.getAllPlayers().forEach(targetPlayer -> {
            String targetServerName = targetPlayer.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("unknown");

            // Nicht an den gleichen Server senden (Duplikate vermeiden)
            // und nicht an blackgelistete Server senden
            if (!targetServerName.equals(serverName) && 
                !configManager.isBlacklisted(targetServerName)) {
                targetPlayer.sendMessage(chatComponent);
            }
        });

        // Prevent default processing since we handled distribution
        event.setResult(PlayerChatEvent.ChatResult.denied());
    }
}
