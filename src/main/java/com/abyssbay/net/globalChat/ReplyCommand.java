package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReplyCommand implements SimpleCommand {
    private final ProxyServer proxyServer;
    private final ConfigManager configManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ReplyCommand(ProxyServer proxyServer, ConfigManager configManager) {
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
        if (args.length < 1) {
            String msg = configManager.getMessage("usage-reply");
            sender.sendMessage(miniMessage.deserialize(msg != null && !msg.isEmpty() ? msg : 
                "§cVerwendung: /reply <Nachricht>"));
            return;
        }

        String message = String.join(" ", args);

        // Get last messager (who sent us a message)
        String targetName = PrivateMessageCommand.getLastMessager(sender.getUsername());
        
        // Fallback: if no one sent us a message, try who we sent to
        if (targetName == null) {
            targetName = PrivateMessageCommand.getLastSent(sender.getUsername());
        }
        
        if (targetName == null) {
            String msg = configManager.getMessage("no-reply-target");
            sender.sendMessage(miniMessage.deserialize(msg != null && !msg.isEmpty() ? msg : 
                "§cDu hast niemanden, dem du antworten kannst!"));
            return;
        }

        // Find target player
        final String finalTargetName = targetName;
        Optional<Player> targetOptional = proxyServer.getAllPlayers()
            .stream()
            .filter(p -> p.getUsername().equalsIgnoreCase(finalTargetName))
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

        // Get sender's current server name
        String senderServerName = sender.getCurrentServer()
            .map(server -> server.getServerInfo().getName())
            .orElse("");

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

        // Update last messager
        PrivateMessageCommand.setLastMessager(target.getUsername(), sender.getUsername());
        
        // Send to Discord webhook
        DiscordWebhookHandler.sendPrivateMessage(sender.getUsername(), target.getUsername(), message, senderServerName, configManager);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Everyone can use this command
        return true;
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        // No arguments needed for /reply
        return Collections.emptyList();
    }
}
